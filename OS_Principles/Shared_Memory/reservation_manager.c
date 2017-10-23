///////////////////////////////////////////////////////////
//  ECSE 427 - Assignment #2                             //
//  Camilo Garcia La Rotta                               //   
//  ID #260657037                                        //
///////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////
//                  HEADER FILES                         //
///////////////////////////////////////////////////////////

// general purpose imports
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <string.h>

// shared memory imports
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <sys/wait.h>
#include <sys/types.h>


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
int create_shm(void);
int create_manager(void);
void init_manager(void);
void print_manager(void);
int add_reserve(char *client, char *section, unsigned int table_num);

// signal handlers
void handle_SIGINT(int signum);

// exit program handlers
void handle_success(void);
void handle_error(char *msg);
int deallocate_mem(void);

// miscelaneous
void print_welcome_banner(void);


///////////////////////////////////////////////////////////
//                  GLOBAL VARIABLES                     //
///////////////////////////////////////////////////////////

RESERVATION *shared_manager;

// shared memory params
const char* mem_name = "/cgarci26";
const size_t mem_size = sizeof(2 * TABLES_PER_SECTION * sizeof(RESERVATION));

// shared memory flags
const int open_flag = O_CREAT | O_RDWR;
const int protection = PROT_READ | PROT_WRITE;
const int visibility = MAP_ANONYMOUS | MAP_SHARED;
const mode_t permissions = S_IRUSR | S_IWUSR | S_IRGRP;

int main(void)
{
    int i;                      
    char args[MAX_ARGS][20];    
    
    for (i = 0; i < MAX_ARGS; i++) { args[i][0] = '\0'; }
    
    // configure reservation manager
    if (create_shm() == -1) { handle_error("create_shm()"); }
    init_manager();
    
    // attach signal handlers
    if (signal(SIGINT, handle_SIGINT) == SIG_ERR) { handle_error("SIGINT handler"); }

    print_welcome_banner();

    while(1)
    {
        // tokenize input command
        int token_count = get_cmd(args); 
        if (token_count == 0) { continue; }
        
        // implementation of built-in cmds that don't require forking
        if (strcmp(args[0], "exit") == 0)
        {
            if (deallocate_mem() == -1) { handle_error("deallocate_mem()"); }
            
            handle_success();
        }

        pid_t child_pid = fork();
        if (child_pid == -1) { handle_error("fork()"); }

        if (child_pid > 0)
        {
            int status;
            waitpid(child_pid, &status, 0);
        }
        else if (child_pid == 0)
        {
            if (strcmp(args[0], "init") == 0) { 
                init_manager();
            } 
            else if (strcmp(args[0], "status") == 0)
            { 
                print_manager(); 
            }
            else if (strcmp(args[0], "reserve") == 0)
            {
                unsigned int table_num;
                
                if (token_count == 3) { table_num = 0; }
                else if (token_count == 4) { table_num = atoi(args[3]); }
                else
                {
                    printf("bad request");
                }

                if (add_reserve(args[1],args[2],table_num) == -1)
                {
                    printf("failed to add reservation"); 
                }
                else
                {
                    printf("GG"); 
                }
            }
            
            handle_success(); 
        }
        
        // clear arguments
        for (i = 0; i < MAX_ARGS; i++) { args[i][0] = '\0'; }
    }
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
int create_shm(void)
{
    int fd_shm = shm_open(mem_name, open_flag, permissions);
    if (fd_shm == -1) { return -1; }
   
    if (ftruncate(fd_shm, mem_size) == -1) { return -1; }

    shared_manager = (RESERVATION *) mmap(NULL, mem_size, 
                                          protection, visibility,
                                          fd_shm,0);

    if (shared_manager == MAP_FAILED) { return -1; }
    
    return 0;
}

int create_manager(void)
{
    return 1;
}

void init_manager(void)
{
    unsigned int a = IDX_SECTION_A;
    unsigned int b = IDX_SECTION_B;
    unsigned int a_offset = 100;
    unsigned int b_offset = 190;
    
    for (; a < TABLES_PER_SECTION; a++)
    {
        shared_manager[a].table_num  = a + a_offset;         
        shared_manager[a].status     = 0;
        shared_manager[a].client[0]  = '\0';            
    }
    
    for (; b < 2*TABLES_PER_SECTION; b++)
    {
        shared_manager[b].table_num  = b + b_offset;         
        shared_manager[b].status     = 0;
        shared_manager[b].client[0]  = '\0';            
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
        table_num = shared_manager[i].table_num;         
        
        printf("Section: %s\t",section);
        printf("Table: %d\t",table_num);
        
        status = shared_manager[i].status;
        if (status == 0) { printf("Status: Free\n"); }
        else
        {   
            client = shared_manager[i].client;            
            
            printf("Status: Reserved\t");
            printf("Client: %s\n",client);
        }  
    }
}

int add_reserve(char *client, char *section, unsigned int table_num)
{
    return 0;
}

// if SIGINT caught, kill current process
void handle_SIGINT(int signum)
{
    printf("Captured signal: %d",signum);
    handle_error("SIGINT");
}

// program exit handlers 
void handle_success(void)
{
    exit(EXIT_SUCCESS); 
}
void handle_error(char *msg) 
{ 
    perror(msg);
    exit(EXIT_FAILURE);
}

int deallocate_mem(void)
{
    if ((munmap(shared_manager, mem_size) == -1) || (shm_unlink(mem_name) == -1))
    {
        return -1;
    }

    return 0;
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


