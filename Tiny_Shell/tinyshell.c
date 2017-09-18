#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>

// tokenize user's input command
// returns number of tokens parsed
int get_cmd(const char* prompt, char **file, char *args[], int *bg)
{
    printf("%s",prompt);

    unsigned int cmd_len, token_count, i = 0;
    char *token, *cmd = NULL;
    size_t linecap = 0;

    cmd_len = getline(&cmd, &linecap, stdin);
    if (0 >= cmd_len) { return -1; }

    // check if last character in line is background flag
    *bg = (cmd[cmd_len-2] == '&') ? 1 : 0;
    
    while ((token = strsep(&cmd, " \t\n")) != NULL)
    {
        // replace non printable chars by space
        for(i = 0; i < strlen(token); i++)
        {
            if (token[i] <= 32) { token[i] = '\0'; }
        }

        if (strlen(token) > 0)
        {
            // first token is executable file
            if (token_count == 0)
            {
                *file = token;
                token_count++;
            }
            else 
            {
                args[token_count++] = token; 
            }
        }
    }

    return token_count;
}



int main(void)
{
    char *file;
    char *args[20];
    int bg;
    while(1)
    {
        bg = 0;
        int token_count = get_cmd("\n> ", file, args, &bg); 
        if (token_count == -1) 
        {
            perror("Failed to read user input");
            exit(EXIT_FAILURE);
        }
        
        printf("%s\n",args[0]);


        pid_t pid = getpid();
        pid_t child_pid = fork();

        if (child_pid == -1)
        { 
            perror("Failed to fork"); 
            exit(EXIT_FAILURE);
        }
        else if (child_pid == 0)
        {
            // inside child process
            execvp(file, args);
            _exit(EXIT_FAILURE);
        }
        else if (child_pid > 0)
        {
            // inside parent process
            if (bg == 1)
            {
                int status;
                waitpid(pid, &status, 0);
                printf("%d",status);
            }

            printf("finito papa");
        }
        
    }
}
