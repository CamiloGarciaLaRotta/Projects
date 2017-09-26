///////////////////////////////////////////////////////////
//  ECSE 427 - Assignment #1                             //
//  Camilo Garcia La Rotta                               //   
//  ID #260657037                                        //
///////////////////////////////////////////////////////////
// TODO TODO TODO FIX DELETE FROM JOBS FIX DELETE DONE JOB INFINITE LOOP 
///////////////////////////////////////////////////////////
//  TODO    HANDLE SIGNALS -> GET SIGNAL FROM JOBS 
//          IMPLEMENT FG
//          VALGRIND
///////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////
//                  HEADER FILES                         //
///////////////////////////////////////////////////////////

// general purpose imports
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <dirent.h>
#include <time.h>

// syscalls and signals
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/syscall.h>
#include <signal.h>

///////////////////////////////////////////////////////////
//                  CONSTANTS                            //
///////////////////////////////////////////////////////////

#define ARGS_SIZE 10            // max # of arguments in command line
#define CHAR_BUFFER 1024        // standar read/write buffer size
#define DISPLAY_MSG 1           // boolean for handle_success()

///////////////////////////////////////////////////////////
//                  DATA STRUCTURES                      //
///////////////////////////////////////////////////////////

// linked list for job handling
typedef struct Job 
{
    pid_t pid;
    char    cmd[CHAR_BUFFER];   // full cmd of the job        
    char    *status;            // current status of the job
    struct  Job *next;          // next linked list job
} Job;

///////////////////////////////////////////////////////////
//                  FUNCTION DECLARATIONS                //
///////////////////////////////////////////////////////////

// tokenize user's input command, returns number of tokens parsed
int get_cmd(const char* prompt, char *args[], int *bg, char *full_cmd);

// generate and store shell prompt with present working directory
void generate_prompt(char pwd[], const char *separator, char *prompt);

// Job linked list actions
int add_job(pid_t pid, char *cmd, char* status);
int remove_job(pid_t pid);
int remove_done_jobs(void);
void print_jobs(void);

// signal handlers
// kill current process 
void handle_SIGINT(int signum);
// remove finishes job from linked list 
//void handle_SIGCHLD(int signum);

// exit program handlers
void handle_success(int display_msg);
void handle_error(char *msg);

// pause execution of program for a random amount of < 10 seconds
void rand_sleep(void);

void print_welcome_banner(void);

///////////////////////////////////////////////////////////
//                  GLOBAL VARIABLES                     //
///////////////////////////////////////////////////////////

Job *HEAD_JOB;


int main(void)
{
    // initialize command parsing variables
    char *args[ARGS_SIZE];
    char pwd[CHAR_BUFFER], prompt[CHAR_BUFFER], full_cmd[CHAR_BUFFER];
    const char *separator = " > ";
    int bg, i;
    
    // generate seed
    time_t now;
    srand((unsigned int) (time(&now)));

    // initialize jobs linked list
    HEAD_JOB = malloc(sizeof(Job));
    if (HEAD_JOB == NULL) { handle_error("malloc()"); }
    
    HEAD_JOB->pid = getpid();
    HEAD_JOB->cmd[0] = '\0';
    HEAD_JOB->status = "MAIN PROCESS";
    HEAD_JOB->next = NULL;

    // attach signal handlers
    // ignore SIGTSTP signal
    if (signal(SIGTSTP,SIG_IGN) == SIG_ERR) 
    { 
        handle_error("SIGTSTP handler failed"); 
    }
    
    // SIGINT kills current process
    if (signal(SIGINT, handle_SIGINT) == SIG_ERR)
    {
        handle_error("SIGINT handler failed"); 
    }
    
    // SIGCHLD removes child job from linked list
    //if (signal(SIGCHLD, handle_SIGCHLD) == SIG_ERR)
    //{
    //    handle_error("SIGCHLD handler failed"); 
    //}

    print_welcome_banner();

    while(1)
    {
        // reset parsing and prompt variables
        for (i = 0; i < ARGS_SIZE; i++) { args[i] = NULL; }
        pwd[0] = prompt[0] = full_cmd[0] = '\0';
        bg = 0;
    
        generate_prompt(pwd, separator, prompt);

        // tokenize input command
        int token_count = get_cmd(prompt, args, &bg, full_cmd); 
        if (token_count == -1) 
        {
            // no cmd entered or EOF flag
            handle_success(DISPLAY_MSG);
        }
        if (token_count == 0)
        {
            // user entered no cmd
            // display prompt again
            continue;
        }
        
        // implementation of built-in cmds that don't require forking
        if (strcmp(args[0], "exit") == 0) { 
            free(HEAD_JOB);
      
            handle_success(DISPLAY_MSG); 
        }
        
        if (strcmp(args[0],"cd") == 0)
        {
            int result;
            char *dst = NULL;

            if (args[1] == NULL)
            {
                // no destination arg, $HOME is implied
                dst = getenv("HOME");
                if (dst == NULL) { handle_error("getenv()"); }
            }
            else { dst = args[1]; }

            result = chdir(dst);
            if (result == -1) { handle_error("cd"); } 

            // cleanup variables TODO check which ones are redundant
            dst = NULL;
            for (i = 0; i < ARGS_SIZE; i++) { args[i] = NULL; }
            pwd[0] = prompt[0] = full_cmd[0] = '\0';
            bg = 0;
        }
        else if (strcmp(args[0],"fg") == 0)
        {
            int pid, status;
            if ((pid = atoi(args[1])) == 0) { handle_error("atoi()"); }

            waitpid(pid, &status, 0);
        }
        else if (strcmp(args[0],"jobs") == 0)
        {
            if (remove_done_jobs() == -1) { handle_error("remove_done_jobs()"); }
            
            if (HEAD_JOB->next == NULL) { printf("No background jobs.\n"); }
            else { print_jobs(); }
        }
        else
        {
            // actions requiring forking
            
            pid_t child_pid = fork();

            if (child_pid == -1) { handle_error("fork()"); }
            
            if (child_pid > 0)
            {
                // inside parent process

                // check background flag
                if (bg == 0)
                {
                    int status;
                    waitpid(child_pid, &status, 0);
                }
                else
                {
                    // inform user of process PID
                    printf("PID = %d\n",child_pid); 
                    
                    char *tmp_status = "RUNNING";
                    add_job(child_pid, full_cmd, tmp_status);
                }
            }
            else if (child_pid == 0)
            {
                // inside child process
                
                // pause for < 10sec facilitate visualizing bg fg processes
                rand_sleep();
                
                // check for implemented built-in cmds
                if (strcmp(args[0],"ls") == 0)
                {
                    int fd, file_count, buf_pos;
                    char buf[CHAR_BUFFER];
                    char *path;
                    struct dirent *dir;
                   
                    // define target path
                    path = (args[1] == NULL) ? "." : args[1]; 
                    
                    fd = open(path, O_RDONLY | O_DIRECTORY);
                    if (fd == -1) { handle_error("open()"); }

                    file_count = syscall(SYS_getdents, fd, buf, CHAR_BUFFER);
                    if (file_count == -1) { handle_error("getdents"); }
                    
                    // display the name of all the retrieved files
                    for (buf_pos = 0; buf_pos < file_count; buf_pos += dir->d_reclen)
                    {
                        dir = (struct dirent *)(buf + buf_pos);
                        printf("%s\t\t",dir->d_name-1);
                    }
                    printf("\n");
                    
		    if (close(fd) == -1) { handle_error("close"); }
                   //free(dir);
                    
                    handle_success(!DISPLAY_MSG);
             }
                else if (strcmp(args[0],"cat") == 0)
                {
                    int src_fd, read_bytes;
		    char buf[CHAR_BUFFER];
                    
                    // open source and destination file descriptors
		    src_fd = open(args[1], O_RDONLY);
		    if (src_fd == -1) { handle_error("open()"); }

                    // transfer bytes from source to destination
		    while ((read_bytes = read(src_fd, buf, CHAR_BUFFER)) > 0)
		    {
			if (write(STDOUT_FILENO, buf, read_bytes) != read_bytes)
			{
			    handle_error("write()");
			}
		    }
		    if (read_bytes == -1) { handle_error("read()"); }

		    if (close(src_fd) == -1) { handle_error("close"); }

                    handle_success(!DISPLAY_MSG);
                }
                else if (strcmp(args[0],"cp") == 0)
                {
                    int src_fd, dst_fd, read_bytes;
		    char buf[CHAR_BUFFER];
                    
                    // create if non existent, overwrite if existent
		    const int dst_open_flags = O_CREAT | O_WRONLY | O_TRUNC;
                    // rw-rw---
		    const mode_t dst_perms =  S_IRUSR | S_IWUSR | S_IRGRP;

                    // open source and destination file descriptors
		    src_fd = open(args[1], O_RDONLY);
		    if (src_fd == -1) { handle_error("open()"); }

		    dst_fd = open(args[2], dst_open_flags, dst_perms);
		    if (src_fd == -1) { handle_error("open()"); }

                    // transfer bytes from source to destination
		    while ((read_bytes = read(src_fd, buf, CHAR_BUFFER)) > 0)
		    {
			if (write(dst_fd, buf, read_bytes) != read_bytes)
			{
			    handle_error("write()");
			}
		    }
		    if (read_bytes == -1) { handle_error("read()"); }

		    if (close(src_fd) == -1) { handle_error("close"); }
		    if (close(dst_fd) == -1) { handle_error("close"); }

                    handle_success(!DISPLAY_MSG);
                }
                else 
                {
                    // non built-in cmds, pass directly to execvp
                    execvp(args[0],args);
               
                    // should never reach this point
                    _exit(EXIT_FAILURE);
                }
            }
        }
    }
}


// tokenize user's input command
// returns number of tokens parsed including binary file
int get_cmd(const char* prompt, char *args[], int *bg, char *full_cmd)
{
    printf("%s",prompt);

    unsigned int cmd_len = 0, token_count = 0, i = 0;
    char *token, *cmd = NULL;
    size_t linecap = 0;

    cmd_len = getline(&cmd, &linecap, stdin);
    
    // if no input or EOF flag, exit program
    if ((cmd_len <= 0) || (strcmp(cmd,"\000") == 0)) return -1;
    
    // if newline or empty string, redisplay prompt
    if ((strcmp(cmd," ") == 0) || (strcmp(cmd,"\n") == 0)) return 0;
    
    // store full command
    strcpy(full_cmd, cmd);

    // remove carriage return
    full_cmd[cmd_len-2] = '\0';

    // check if last character in line is background flag
    *bg = (cmd[cmd_len-2] == '&') ? 1 : 0;
    
    // tokenize input command
    while ((token = strsep(&cmd, " \t\n")) != NULL)
    {
        // replace non printable chars by space
        for(i = 0; i < strlen(token); i++)
        {
            if (token[i] <= 32) { token[i] = '\0'; }
        }

        if (strlen(token) > 0) { args[token_count++] = token; }
    }
    
    // if background flag high, erase last arg '&' 
    if (*bg == 1) { args[token_count-1] = NULL; }
    
    free(cmd);
    free(token);

    return token_count;
}

// generate and store shell prompt with present working directory
void generate_prompt(char pwd[], const char *separator, char *prompt)
{
    // get present directory name
    getcwd(pwd, CHAR_BUFFER);
    
    strcat(prompt,pwd);
    strcat(prompt,separator);
}

// add job to linked list
int add_job(pid_t pid, char *cmd, char *status)
{
    Job *j = (Job *)malloc(sizeof(Job));
    if (j == NULL) { handle_error("malloc()"); } //TODO DO I HANDLE HERE OR IN MAIN?
    j->pid = pid;
    strcpy(j->cmd,cmd);
    j->status = status;
    j->next = HEAD_JOB->next;

    HEAD_JOB->next = j;

    return 0;
}

// remove job from linked list
int remove_job(pid_t pid)
{
    Job *j = HEAD_JOB;

    while (j->next != NULL)
    {
        if (j->next->pid == pid) { break; }
        j = j->next;
    }

    if (j->next->pid == pid)
    {
        Job *k;

        if (j == HEAD_JOB)
        {
            // node to be removed is first in linked list
            k = HEAD_JOB->next;
            HEAD_JOB->next = HEAD_JOB->next->next;
        }
        else
        {
            k = j->next;
            j->next = j->next->next;
        }

        free(k);
        return 0;
    }
    else
    {
        // no node with given pid found
        return -1;
    }
}

// remove DONE jobs from linked list
int remove_done_jobs(void)
{
    pid_t pid;
    Job *j = HEAD_JOB->next;

    while (j != NULL)
    {
        if (strcmp(j->status,"DONE") == 0)
        {
            pid = j->pid;
            j = j->next;

            if (remove_job(pid) == -1) { return -1; }
        }
        else { j = j->next; }
    }

    return 0;
}

// print linked list of jobs with current status
void print_jobs(void)
{
    int status;
    Job *j = HEAD_JOB;

    printf("PID\t\tSTATUS\t\tCOMMAND\n");
    
    while (j->next != NULL)
    { 
        j = j->next;

        j->status = (waitpid(j->pid,&status,WNOHANG) == 0) ? "RUNNING" : "DONE";
         
        printf("%d\t\t%s\t\t%s\n",j->pid,j->status,j->cmd); 
    }
}

// if SIGINT caught, kill current process
void handle_SIGINT(int signum)
{
    if (getpid() == HEAD_JOB->pid)
    {
        // only main process handles SIGINT
        printf("\nCaptured signal: %d\n",signum);

        if (HEAD_JOB->next == NULL)
        {
            // no other processes have been added
            // kill the tinyshell
            printf("No other processes running left to kill\n");
            printf("Killing the main TinyShell ...\n");
            raise(SIGTERM);
        }

        // find an active child process
        int status;
        Job *j = HEAD_JOB->next;
        
        while(j->next != NULL)
        {
            if (waitpid(j->pid,&status,WNOHANG) == 0)
            { 
                if (kill(j->pid, SIGTERM) == -1) { handle_error("kill()"); }
                printf("killed process with PID: %d\n",j->pid);
                
                if (remove_job(j->pid) == -1) { handle_error("remove_job()"); }
                
                break; 
            } 

            j = j->next;
        }
        
        j = NULL;
    }
}

// program exit handlers 
void handle_success(int display_msg)
{
    if (display_msg == 1) { printf("Exiting TinyShell\n"); }
    exit(EXIT_SUCCESS);
}
void handle_error(char *msg)
{
    perror(msg);
    exit(EXIT_FAILURE);
}
// pause execution of program for a random amount of < 10 seconds
void rand_sleep(void) { sleep(rand() % 15); }

void print_welcome_banner(void)
{
    printf("\n\n");
    printf("\tWelcome to the TinyShell\n");
    printf("\tECSE 427 - Assignment #1\n");
    printf("\t------------------------\n");
    printf("\tCamilo Garcia La Rotta\n");
    printf("\tID #260657037\n");
    printf("\t-----------------------\n");
    printf("\n\n");
}
