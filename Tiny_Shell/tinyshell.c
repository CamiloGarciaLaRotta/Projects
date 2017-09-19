#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>

#define ARGS_SIZE 20

// tokenize user's input command
// returns number of tokens parsed including binary file
int get_cmd(const char* prompt, char *args[], int *bg)
{
    printf("%s",prompt);

    unsigned int cmd_len = 0, token_count = 0, i = 0;
    char *token, *cmd = NULL;
    size_t linecap = 0;

    cmd_len = getline(&cmd, &linecap, stdin);
    if (cmd_len <= 0)  return -1; 
    //if (strcmp(cmd,"\n")) return 0; 

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


int main(void)
{
    char *args[ARGS_SIZE];
    int bg, i;

    while(1)
    {
        // reset args
        for (i = 0; i < ARGS_SIZE; i++) { args[i] = NULL; }
        bg = 0;

        int token_count = get_cmd("\n> ", args, &bg); 
        if (token_count == -1) 
        {
            perror("Exiting TinyShell");
            exit(EXIT_FAILURE);
        }
        
        //pid_t pid = getpid();
        pid_t child_pid = fork();

        if (child_pid == -1)
        { 
            perror("Failed to fork"); 
            exit(EXIT_FAILURE);
        }
        else if (child_pid > 0)
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
            
            execvp(args[0],args);
            
            _exit(EXIT_FAILURE);
        }
    }
}
