#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>

#define ARGS_SIZE 20
#define CHAR_BUFFER 256

///////////////////////////////////////////////////////////
//  TODO    HANDLE ENTER && CTRL+D
//          IMPLEMENT LS,CD,CAT,CP,FG,JOBS
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


int main(void)
{
    char *args[ARGS_SIZE];
    char pwd[CHAR_BUFFER], prompt[CHAR_BUFFER];
    const char *separator = " > ";
    int bg, i;

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
            perror("\nExiting TinyShell");
            exit(EXIT_SUCCESS);
        }
        if (token_count == 0)
        {
            // user entered no cmd
            // display prompt again
            continue;
        }
        
        // implementation of built-in cmds
        // that don't require forking
        if (strcmp(args[0], "exit") == 0)
        {
            printf("Exiting TinyShell ...\n");
            exit(EXIT_SUCCESS);
        }
        else if (strcmp(args[0],"cd") == 0)
        {
            char *dst = NULL;

            if (args[1] == NULL)
            {
                // no destination arg, $HOME is implied
                dst = getenv("HOME");
                if (dst == NULL)
                {
                    printf("Error: $HOME is not initialized");
                    exit(EXIT_FAILURE);
                }
            }
            else { dst = args[1]; }

            int result = chdir(dst);
            if (result == -1)
            {
                perror("Couldn't cd");
                exit(EXIT_FAILURE);
            }
        }
        else
        {
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
                
                // check for implemented built-in cmds
                if (strcmp(args[0],"ls") == 0)
                {
                    // TODO
                    exit(EXIT_SUCCESS); 
                }
                else if (strcmp(args[0],"cat") == 0)
                {
                    // TODO 
                }
                else if (strcmp(args[0],"cp") == 0)
                {
                    // TODO 
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
                    // non implemented cmd,
                    // pass directly to execvp

                    execvp(args[0],args);
                
                    _exit(EXIT_FAILURE);
                }
            }
        }
    }
}
