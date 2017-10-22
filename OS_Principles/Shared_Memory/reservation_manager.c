///////////////////////////////////////////////////////////
//  ECSE 427 - Assignment #2                             //
//  Camilo Garcia La Rotta                               //   
//  ID #260657037                                        //
///////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////
//                  HEADER FILES                         //
///////////////////////////////////////////////////////////

// general purpose imports
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <signal.h>
#include <string.h>

/*
 *  #include <sys/types.h>
 *  #include <sys/wait.h>
 *  #include <dirent.h>
 *  #include <time.h>
 *
 *  // syscalls and signals
 *  #include <fcntl.h>
 *  #include <unistd.h>
 *  #include <sys/stat.h>
 *  #include <sys/syscall.h>
 */

///////////////////////////////////////////////////////////
//                  CONSTANTS                            //
///////////////////////////////////////////////////////////

#define TABLES_PER_SECTION  10
#define IDX_SECTION_A       0
#define IDX_SECTION_B       10
#define BUFF_SIZE           80           
#define MAX_ARGS            4          
#define ARG_SIZE            BUFF_SIZE / MAX_ARGS

///////////////////////////////////////////////////////////
//                  DATA STRUCTURES                      //
///////////////////////////////////////////////////////////

// linked list for job handling
typedef struct RESERVATION 
{
    unsigned int    table_num;         
    unsigned int    status;             // 0 free, 1 reserved
    char            client[BUFF_SIZE];            

} RESERVATION;

///////////////////////////////////////////////////////////
//                  FUNCTION DECLARATIONS                //
///////////////////////////////////////////////////////////

// tokenize user's input command, returns number of tokens parsed
unsigned int get_cmd(char args[MAX_ARGS][ARG_SIZE]);

// manager actions
void init_manager(void);
void print_manager(void);

// signal handlers
void handle_SIGINT(int signum);

// exit program handlers
void handle_success(void);
void handle_error(char *msg);

void print_welcome_banner(void);

///////////////////////////////////////////////////////////
//                  GLOBAL VARIABLES                     //
///////////////////////////////////////////////////////////

RESERVATION R_manager[2 * TABLES_PER_SECTION];

/*
 * // create if non existent, overwrite if existent
 * const int dst_open_flags = O_CREAT | O_WRONLY | O_TRUNC;
 * // rw-rw---
 * const mode_t dst_perms =  S_IRUSR | S_IWUSR | S_IRGRP;
 */

int main(void)
{
    // initialize command parsing variables
    int i;
    char args[MAX_ARGS][20];
    for (i = 0; i < MAX_ARGS; i++) { args[i][0] = '\0'; }
    
    // initialize reservation manager
    init_manager(); 

    // attach signal handlers
    if (signal(SIGINT, handle_SIGINT) == SIG_ERR)
    {
        handle_error("SIGINT handler failed"); 
    }

    print_welcome_banner();

    while(1)
    {
        // tokenize input command
        int token_count = get_cmd(args); 
        if (token_count == 0)
        {
            // user did carriage return, display prompt again
            continue;
        }
        
        // implementation of built-in cmds that don't require forking
        if (strcmp(args[0], "exit") == 0) { handle_success(); }
        if (strcmp(args[0], "init") == 0) { init_manager(); } // TODO ADD SYNCHRO
        if (strcmp(args[0], "status") == 0) { print_manager(); }
        if (strcmp(args[0], "reserve") == 0)
        {
            int status;
            unsigned int table_num;
            
            if (token_count == 3) { table_num = 0; }
            else if (token_count == 4) { table_num = atoi(arg[3]); }
            else
            {
                printf("bad request");
                continue;
            }

            if (add_reserve(arg[1],arg[2],table_num) == -1)
            {
                printf("failed to add reservation"); 
            }
            else
            {
                printf("GG"); 
            }
        }
        
        // clear arguments
        for (i = 0; i < MAX_ARGS; i++) { args[i][0] = '\0'; }
    }

//            // actions requiring forking
//            
//            pid_t child_pid = fork();
//
//            if (child_pid == -1) { handle_error("fork()"); }
//            
//            if (child_pid > 0)
//            {
//                // inside parent process
//
//                // check background flag
//                if (bg == 0)
//                {
//                    int status;
//                    
//                    // store current foreground job
//                    HEAD_JOB->fg_child_pid = child_pid;
//                    waitpid(child_pid, &status, 0);
//                }
//                else
//                {
//                    // inform user of process PID
//                    printf("PID = %d\n",child_pid); 
//                    
//                    char *tmp_status = "RUNNING";
//                    if (add_job(child_pid, full_cmd, tmp_status) == -1)
//                    {
//                        handle_error("add_job()");
//                    }
//                }
//            }
//            else if (child_pid == 0)
//            {
//                // inside child process
//                
//                // pause for < 10sec facilitate visualizing bg fg processes
//                rand_sleep();
//                
//                // check for implemented built-in cmds
//                if (strcmp(args[0],"ls") == 0)
//                {
//                    int src_fd, dst_fd, file_count, buf_pos;
//                    char buf[BUFF_SIZE];
//                    char *src_path, *dst_path;
//                    struct dirent *dir;
//                   
//                    // define source and destination path
//                    if (redir == 1)
//                    {
//                        if (strcmp(args[1],">") == 0)
//                        {
//                            // no source target defines, pwd implied
//                            src_path = ".";
//                            dst_path = args[2];
//                        }
//                        else
//                        {
//                            // user specified different repo to ls
//                            src_path = args[1];
//                            dst_path = args[3];
//                        }
//                    }
//                    else
//                    {
//                        // no redirection
//                        dst_path = NULL;
//
//                        if (args[1] == NULL)
//                        {
//                            // ls command with no arguments 
//                            src_path = ".";
//                        }
//                        else
//                        {
//                            // user specified different repo to ls
//                            src_path = args[1];
//                        }
//                    }
//                    
//                    src_fd = open(src_path, O_RDONLY | O_DIRECTORY);
//                    if (src_fd == -1) { handle_error("open()"); }
//
//                    file_count = syscall(SYS_getdents, src_fd, buf, BUFF_SIZE);
//                    if (file_count == -1) { handle_error("getdents"); }
//                    
//                    if (redir == 1)
//                    {
//                        // output redirection towards another file
//                        dst_fd = open(dst_path, dst_open_flags, dst_perms);
//		        if (dst_fd == -1) { handle_error("open()"); }
//                    }
//                    else { dst_fd = STDOUT_FILENO; }
//
//                    // display the name of all the retrieved files
//                    for (buf_pos = 0; buf_pos < file_count; buf_pos += dir->d_reclen)
//                    {
//                        dir = (struct dirent *)(buf + buf_pos);
//                        dprintf(dst_fd,"%s\t\t",dir->d_name-1);
//                    }
//                    printf("\n");
//                    
//		    if (close(src_fd) == -1) { handle_error("close"); }
//		    if (redir == 1)
//                    {
//                        if (close(dst_fd) == -1) { handle_error("close"); }
//                    }
//                    
//                    handle_success(!DISPLAY_MSG);
//             }
//                else if (strcmp(args[0],"cat") == 0)
//                {
//                    int src_fd, dst_fd, read_bytes;
//		    char buf[BUFF_SIZE];
//                    
//                    // open source and destination file descriptors
//		    src_fd = open(args[1], O_RDONLY);
//		    if (src_fd == -1) { handle_error("open()"); }
//                    
//                    if (redir == 1)
//                    {
//                        // output redirection towards another file
//                        dst_fd = open(args[3], dst_open_flags, dst_perms);
//		        if (dst_fd == -1) { handle_error("open()"); }
//                    }
//                    else { dst_fd = STDOUT_FILENO; }
//
//                    // transfer bytes from source to destination
//		    while ((read_bytes = read(src_fd, buf, BUFF_SIZE)) > 0)
//		    {
//			if (write(dst_fd, buf, read_bytes) != read_bytes)
//			{
//			    handle_error("write()");
//			}
//		    }
//		    if (read_bytes == -1) { handle_error("read()"); }
//
//                    // close file descriptors
//		    if (close(src_fd) == -1) { handle_error("close"); }
//                    if (redir == 1) 
//                    {
//		        if (close(dst_fd) == -1) { handle_error("close"); }
//                    }
//
//                    handle_success(!DISPLAY_MSG);
//                }
//                else if (strcmp(args[0],"cp") == 0)
//                {
//                    int src_fd, dst_fd, read_bytes;
//		    char buf[BUFF_SIZE];
//                    
//                    // open source and destination file descriptors
//		    src_fd = open(args[1], O_RDONLY);
//		    if (src_fd == -1) { handle_error("open()"); }
//
//		    dst_fd = open(args[2], dst_open_flags, dst_perms);
//		    if (src_fd == -1) { handle_error("open()"); }
//
//                    // transfer bytes from source to destination
//		    while ((read_bytes = read(src_fd, buf, BUFF_SIZE)) > 0)
//		    {
//			if (write(dst_fd, buf, read_bytes) != read_bytes)
//			{
//			    handle_error("write()");
//			}
//		    }
//		    if (read_bytes == -1) { handle_error("read()"); }
//
//		    if (close(src_fd) == -1) { handle_error("close"); }
//		    if (close(dst_fd) == -1) { handle_error("close"); }
//
//                    handle_success(!DISPLAY_MSG);
//                }
//                else 
//                {
//                    // non built-in cmds, pass directly to execvp
//                    execvp(args[0],args);
//               
//                    // should never reach this point
//                    _exit(EXIT_FAILURE);
//                }
//            }
//        }
//    }
}


// tokenize user's input command, return number of tokens parsed
unsigned int get_cmd(char args[MAX_ARGS][ARG_SIZE])
{
    printf(" > ");

    unsigned int token_count = 0;
    char buff[BUFF_SIZE];       // stdin buff
    char *token;                // strtok ptr
    char *whitespace = " \t\n\f\r\v";

    if (fgets(buff, BUFF_SIZE, stdin) != NULL)
    {
        token = strtok(buff, whitespace);
        while (token != NULL && token_count < MAX_ARGS)
        {
            strncpy(args[token_count++], token, strlen(token)+1);
            token = strtok(NULL, whitespace);
        }
    }

    free(token);
    
    return token_count;
}

// manager actions
void init_manager(void)
{
    unsigned int a = IDX_SECTION_A;
    unsigned int b = IDX_SECTION_B;
    unsigned int a_offset = 100;
    unsigned int b_offset = 190;
    
    for (; a < TABLES_PER_SECTION; a++)
    {
        R_manager[a].table_num  = a + a_offset;         
        R_manager[a].status     = 0;
        R_manager[a].client[0]  = '\0';            
    }
    
    for (; b < 2*TABLES_PER_SECTION; b++)
    {
        R_manager[b].table_num  = b + b_offset;         
        R_manager[b].status     = 0;
        R_manager[b].client[0]  = '\0';            
    }
}

void print_manager(void)
{
    printf("Reservation Manager\n");
    printf("-------------------\n");
    
    char *section, *client;
    unsigned int table_num, status, i;
    for (i=0; i < 2*TABLES_PER_SECTION; i++)
    {
        section = (i < IDX_SECTION_B) ? "A" : "B";
        table_num = R_manager[i].table_num;         
        
        printf("Section: %s\t",section);
        printf("Table: %d\t",table_num);
        
        status = R_manager[i].status;
        if (status == 0) { printf("Status: Free\n"); }
        else
        {   
            client = R_manager[i].client;            
            
            printf("Status: Reserved\t");
            printf("Client: %s\n",client);
        }  
    }
}

// if SIGINT caught, kill current process
void handle_SIGINT(int signum)
{
    printf("\nCaptured signal: %d\n",signum);
    handle_success();
}

// program exit handlers 
void handle_success()
{
    exit(EXIT_SUCCESS); 
}
void handle_error(char *msg) 
{ 
    perror(msg);
    exit(EXIT_FAILURE);
}

void print_welcome_banner(void)
{
    printf("\n\n");
    printf("\tWelcome to the Reservation Manager\n");
    printf("\t    ECSE 427 - Assignment #2\n");
    printf("\t    ------------------------\n");
    printf("\t    Camilo Garcia La Rotta\n");
    printf("\t    ID #260657037\n");
    printf("\t    -----------------------\n");
    printf("\n\n");
}


