///////////////////////////////////////////////////////////
//  ECSE 427 - Assignment #1                             //
//  Camilo Garcia La Rotta                               //   
//  ID #260657037                                        //
///////////////////////////////////////////////////////////

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

// tokenize user's input command
// returns number of tokens parsed
int get_cmd(const char* prompt, char *args[], int *bg, char *full_cmd);

// get/add/remove job from linked list
Job *get_job(pid_t pid);
int add_job(pid_t pid, char *cmd, char* status);
int remove_job(pid_t pid);

// exit program handlers
void handle_success(int display_msg);
void handle_error(char *msg);

// SIGINT signal handler
void kill_curr_proc(int signum);

// pause execution of program for a random amount of < 10 seconds
void rand_sleep(void);

// generate and store shell prompt with present working directory
void generate_prompt(char pwd[], const char *separator, char *prompt);

void print_welcome_banner(void);

///////////////////////////////////////////////////////////
//                  GLOBAL VARIABLES                     //
///////////////////////////////////////////////////////////

Job *HEAD_JOB, *TAIL_JOB;


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
    
    //TAIL_JOB = malloc(sizeof(Job));
    //if (TAIL_JOB == NULL) { handle_error("malloc()"); }

    TAIL_JOB = HEAD_JOB;
    
    HEAD_JOB->pid = getpid();
    HEAD_JOB->cmd[0] = '\0';
    HEAD_JOB->status = "MAIN PROCESS";
    HEAD_JOB->next = TAIL_JOB;

    // attach signal handlers
    // ignore SIGTSTP signal
    if (signal(SIGTSTP,SIG_IGN) == SIG_ERR) 
    { 
        handle_error("SIGTSTP handler failed"); 
    }
    
    // SIGINT kills current process
    if (signal(SIGINT, kill_curr_proc) == SIG_ERR)
    {
        handle_error("SIGINT handler failed"); 
    }

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
    //        if (HEAD_JOB == TAIL_JOB) { free(HEAD_JOB); }
    //        else 
    //        {
                free(HEAD_JOB);
    //            free(TAIL_JOB);
                TAIL_JOB = NULL;
    //        }
      
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
            // print jobs linked list
            if (HEAD_JOB == TAIL_JOB) { printf("No background jobs.\n"); }
            else
            {
                Job *j = HEAD_JOB;
            
                printf("PID\tSTATUS\tCOMMAND\n");
                do 
                { 
                    j = j->next;
                    printf("%d\t%s\t%s\n",j->pid,j->status,j->cmd); 
                }
                while (j != TAIL_JOB);
            }
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
                    
                    char *tmp_status = "TODO"; //TODO
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

// retrieve job with input pid
Job *get_job(pid_t pid)
{
    Job *j = HEAD_JOB;
    
    while (j != TAIL_JOB)
    {
        j = j->next;
        if (j->pid == pid) { break; }
    }

    if (j == HEAD_JOB) { j = NULL; }

    return j;
}

// add job to linked list
int add_job(pid_t pid, char *cmd, char* status)
{
    TAIL_JOB->next = malloc(sizeof(Job));
    if (TAIL_JOB->next == NULL) { handle_error("malloc()"); }
    
    TAIL_JOB = TAIL_JOB->next;
    
    TAIL_JOB->pid = pid;
    strcpy(TAIL_JOB->cmd, cmd);
    TAIL_JOB->status = status;
    TAIL_JOB->next = NULL;
    
    return 0;
}

// remove job from linked list
int remove_job(pid_t pid)
{
    Job *j = HEAD_JOB;

    while(j->next != TAIL_JOB)
    {
        if (j->next->pid == pid) { break; }
        j = j->next;
    }

    if (j->next->pid == pid) 
    {
        // remove node, reattach neighbours
        Job *k = j->next;
        j->next = j->next->next;
        
        free(k);

        return 0;
    }
    else
    {
        // error, no job with given pid found
        return -1;
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

//void kil(int signum)
void kill_curr_proc(int signum)
{
    printf("\nCaptured signal: %d\n",signum);

    Job *j = HEAD_JOB;
    
    // find current active job
    while(j != TAIL_JOB)
    {
        j = j->next;
        if (strcmp(j->status,"running") == 0) { break; }
    }

    if (j == HEAD_JOB)
    {
        // no other processes have been added
        // kill the tinyshell
        printf("No other processes running left to kill\n");
        printf("Killing the main TinyShell ...\n");
        raise(SIGTERM);
    }
    
    // at this point j points either to the last job added to the link list
    // or to another process which is currently rnning
    // either way we kill it
    if (kill(j->pid, SIGTERM) == -1) { handle_error("kill()"); }
    
    if (remove_job(j->pid) == -1) { handle_error("remove_job()"); }
    
    j = NULL;

    printf("killed process with PID: %d\n",j->pid);
    
    // reattach signal to handler
    //if(signal(SIGINT, kill_curr_proc) ==SIG_ERR) { handle_error("signal()"); }
}

// pause execution of program for a random amount of < 10 seconds
void rand_sleep(void) { sleep(rand() % 10); }

// generate and store shell prompt with present working directory
void generate_prompt(char pwd[], const char *separator, char *prompt)
{
    // get present directory name
    getcwd(pwd, CHAR_BUFFER);
    
    strcat(prompt,pwd);
    strcat(prompt,separator);
}

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
