// general purpose imports
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <dirent.h>

// syscalls
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/syscall.h>

#define ARGS_SIZE 10
#define CHAR_BUFFER 1024 
#define DISPLAY_MSG 1

///////////////////////////////////////////////////////////
//  ECSE 427 - Assignment #1                             //
//  Camilo Garcia La Rotta                               //   
//  ID #260657037                                        //
///////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////
//  TODO    HANDLE SIGNALS 
//          IMPLEMENT CAT,FG,JOBS
//          VALGRIND
///////////////////////////////////////////////////////////


// tokenize user's input command
// returns number of tokens parsed including binary file
int get_cmd(const char* prompt, char *args[], int *bg)
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
    
    // check if last character in line is background flag
    *bg = (cmd[cmd_len-2] == '&') ? 1 : 0;
    
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

int main(void)
{
    char *args[ARGS_SIZE];
    char pwd[CHAR_BUFFER], prompt[CHAR_BUFFER];
    const char *separator = " > ";
    int bg, i;

    // welcome banner
    printf("\n\n");
    printf("\tWelcome to the TinyShell\n");
    printf("\tECSE 427 - Assignment #1\n");
    printf("\t------------------------\n");
    printf("\tCamilo Garcia La Rotta\n");
    printf("\tID #260657037\n");
    printf("\t-----------------------\n");
    printf("\n\n");

    while(1)
    {
        // reset args
        for (i = 0; i < ARGS_SIZE; i++) { args[i] = NULL; }
        pwd[0] = prompt[0] = '\0';
        bg = 0;

        // get present directory name
        getcwd(pwd, sizeof(pwd));
        strcat(prompt,pwd);
        strcat(prompt,separator);

        int token_count = get_cmd(prompt, args, &bg); 
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
        
        // implementation of built-in cmds
        // that don't require forking
        if (strcmp(args[0], "exit") == 0) { handle_success(DISPLAY_MSG); }
        
        if (strcmp(args[0],"cd") == 0)
        {
            char *dst = NULL;
            int result;

            if (args[1] == NULL)
            {
                // no destination arg, $HOME is implied
                dst = getenv("HOME");
                if (dst == NULL) { handle_error("getenv()"); }
            }
            else { dst = args[1]; }

            result = chdir(dst);
            if (result == -1) { handle_error("cd"); } 
        }
        else
        {
            // actions requiring forking
            
            //pid_t pid = getpid();
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
            }
            else if (child_pid == 0)
            {
                // inside child process
                
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
                else if (strcmp(args[0],"fg") == 0)
                {
                    // TODO 
                }
                else if (strcmp(args[0],"jobs") == 0)
                {
                    // TODO 
                }
                else 
                {
                    // non built-in cmds,
                    // pass directly to execvp
                    execvp(args[0],args);
               
                    // should never reach this point
                    _exit(EXIT_FAILURE);
                }
            }
        }
    }
}
