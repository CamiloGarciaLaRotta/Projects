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
#include <semaphore.h>

///////////////////////////////////////////////////////////
//                  CONSTANTS                            //
///////////////////////////////////////////////////////////

#define TABLES_PER_SECTION  ((unsigned int)10)
#define IDX_SECTION_A       ((unsigned int)0)
#define IDX_SECTION_B       ((unsigned int)10)
#define BUFF_SIZE           ((unsigned int)80)           
#define MAX_ARGS            ((unsigned int)4)          
#define ARG_SIZE            BUFF_SIZE / MAX_ARGS


///////////////////////////////////////////////////////////
//                  FUNCTION DECLARATIONS                //
///////////////////////////////////////////////////////////

// shell functions
unsigned int parse_line(char line[BUFF_SIZE], char args[MAX_ARGS][ARG_SIZE]);
int exec_line(unsigned int token_count, char args[MAX_ARGS][ARG_SIZE]);
void print_usage(void);
void print_welcome_banner(void);

// manager actions
int create_shm(void);
int create_manager(void);
void init_manager(void);
void print_manager(void);
int add_reserve(char *client, unsigned int section, unsigned int table_num);
int get_min_free_idx(unsigned int section);
int is_available(unsigned int section, unsigned int table_num);
int validate_args(unsigned int section, unsigned int table_num);

// exit program handlers
void handle_success(void);
void handle_error(char *msg);
void handle_SIGINT(int signum);
int deallocate_mem(void);
int deallocate_sem(void);


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
//                  GLOBAL VARIABLES                     //
///////////////////////////////////////////////////////////

RESERVATION *shared_manager;
sem_t *mutex;       // semaphore to ensure mutex of shared memory
sem_t *active;      // semaphore to signal when no process is currently 
                    // using the sharem memory -> unlink

// shared memory params
const char* mem_name = "/cgarci26_mem";
const char* mutex_sem_name = "/cgarci26_mutex";
const char* active_sem_name = "/cgarci26_active";
const size_t mem_size = sizeof(2 * TABLES_PER_SECTION * sizeof(RESERVATION));

// shared memory flags
const int mem_create = O_RDWR | O_CREAT;
const int sem_create = O_CREAT;
const int protection = PROT_READ | PROT_WRITE;
const int visibility = MAP_SHARED;
const mode_t permissions = S_IRUSR | S_IWUSR;


///////////////////////////////////////////////////////////
//                          MAIN                         //
///////////////////////////////////////////////////////////

int main(int argc, char *argv[])
{
    if (argc > 2) 
    {
        print_usage();
        handle_success();
    }

    char line[BUFF_SIZE];           // line to be parsed and tokenized
    char args[MAX_ARGS][20];        // tokens parsed from line
    unsigned int token_count, i;       
    
    // configure mutual exclusion and active semaphores
    mutex = sem_open(mutex_sem_name, sem_create, permissions, 1);
    active = sem_open(active_sem_name, sem_create, permissions, 0);
    
    // active acts as a counter of current # of procs using shared mem
    // when active == 0  no other proc is using the shared mem
    // so it can be unlinked completely as stated in assignment requirements
    sem_post(active);

    // configure reservation manager
    if (create_shm() == -1) { handle_error("create_shm()"); }

    // attach signal handlers
    if (signal(SIGINT, handle_SIGINT) == SIG_ERR) { handle_error("SIGINT handler"); }

    print_welcome_banner();
   
    if (argc == 2)
    {
        // file given as CL input, execute every line

        FILE *fp;
        
        if ((fp = fopen(argv[1], "r")) == NULL) { handle_error("fopen()"); }

        while (fgets(line, BUFF_SIZE, fp) != NULL)
        {
            token_count = parse_line(line, args); 
            
            if (token_count == 0) { continue; }
            
            if (exec_line(token_count,args) == -1) { return -1; }
            
            // clear cmd parsing variables
            memset(&line[0], 0 , sizeof(line));
            for (i = 0; i < MAX_ARGS; i++) { args[i][0] = '\0'; }
        }

        fclose(fp);

        sem_wait(active);

        if (deallocate_mem() == -1) { handle_error("deallocate_mem()"); }
        if (deallocate_sem() == -1) { handle_error("deallocate_sem()"); }
        
        handle_success();

    }
    else
    {
        // no CL argument given, execute shell 
        
        while(1)
        {
            // clear cmd parsing variables
            memset(&line[0], 0 , sizeof(line));
            for (i = 0; i < MAX_ARGS; i++) { args[i][0] = '\0'; }
             
            printf(" > ");

            fgets(line, BUFF_SIZE, stdin);
        
            if (line == NULL || strlen(line) == 1) { continue; }
            else { token_count = parse_line(line,args); } 
            
            if (exec_line(token_count, args) == -1) { print_usage(); }
        }
    }
}


// tokenize input line, return number of tokens parsed
unsigned int parse_line(char line[BUFF_SIZE], char args[MAX_ARGS][ARG_SIZE])
{
    unsigned int token_count = 0;
    char *token;
    char *whitespace = " \t\n\f\r\v";

    token = strtok(line, whitespace);
    while (token != NULL && token_count < MAX_ARGS)
    {
        strncpy(args[token_count++], token, strlen(token)+1);
        token = strtok(NULL, whitespace);
    }

    return token_count;
}

// execute tokenized command, -1 if command has bad syntax, 0 else
int exec_line(unsigned int token_count, char args[MAX_ARGS][ARG_SIZE])
{
    sleep(1);
    if (strcmp(args[0], "exit") == 0)
    {
        if (token_count == 1)
        {
            
            // notify through semaphore process won't be using shared mem anymore
            sem_wait(active);

            if (deallocate_mem() == -1) { handle_error("deallocate_mem()"); }
            if (deallocate_sem() == -1) { handle_error("deallocate_sem()"); }
            
            handle_success();
        }
        else { print_usage(); }
    }
    else if (strncmp(args[0], "init", strlen("init")) == 0) 
    { 
        if (token_count == 1)
        {
            if (sem_wait(mutex) == -1) { handle_error("sem_wait()"); }
            init_manager();
            if (sem_post(mutex) == -1) { handle_error("sem_wait()"); }
            
            printf("Succesfully cleared reservations.\n");
        }
        else { return -1; }

    } 
    else if (strncmp(args[0], "status", strlen("status")) == 0)
    {
        if (token_count == 1)
        {
            if (sem_wait(mutex) == -1) { handle_error("sem_wait()"); }
            print_manager(); 
            if (sem_post(mutex) == -1) { handle_error("sem_wait()"); }
        }
        else { return -1; }
    }
    else if (strncmp(args[0], "reserve", strlen("reserve")) == 0)
    {
        if (token_count < 3) { return -1; }
        else 
        {
            int table_num;
            unsigned int section, u_table_num;

            // section is defined by its minimum idx
            if (strncmp(args[2], "A", strlen(args[2])) == 0) { section = IDX_SECTION_A; }
            else if (strncmp(args[2], "B", strlen(args[2])) == 0) { section = IDX_SECTION_B; }
            else 
            {
                printf("Invalid section. Must be A or B.\n");

                return 0;
            }

            // a table_num 0 implies no preference from the user
            table_num = (args[3] == '\0') ? 0 : atoi(args[3]);
            if (table_num < 0) 
            {
                printf("Table number can't be negative.\n");    

                return 0;
            }
             
            u_table_num = table_num;

            if (table_num != 0 && validate_args(section, u_table_num) == -1) 
            {
                printf("Invalid section/table_number range.\n");
            }
            else
            {
                int result;

                if (sem_wait(mutex) == -1) { handle_error("sem_wait()"); }
                result = add_reserve(args[1],section, u_table_num);
                if (sem_post(mutex) == -1) { handle_error("sem_wait()"); }
                
                if (result == -1) 
                {
                    if (u_table_num == 0) 
                    {
                        printf("Failed to reserve, no empty table.\n"); 
                    }
                    else { printf("Failed to reserve, table is occupied.\n"); }
                }
                else
                {
                    printf("Succesfully reserved table.\n"); 
                }
            }
        }
    }
    else { return -1; }

    return 0;
}

// create shared memory and set global ptr to it
int create_shm(void)
{
    int new_mem = -1;

    int fd_shm = shm_open(mem_name, O_CREAT | O_EXCL | O_RDWR, permissions);

    if (errno == EEXIST)
    {
        // memory exists already
        fd_shm = shm_open(mem_name, O_RDWR, permissions);
    }
    else
    {
        // memory hasnt been created
        new_mem = 0;
        fd_shm = shm_open(mem_name, O_CREAT | O_RDWR, permissions);
        
        if (ftruncate(fd_shm, mem_size) == -1) { return -1; }
    }

    shared_manager = (RESERVATION *) mmap(NULL, mem_size, 
                                          protection, visibility,
                                          fd_shm,0);
    
    if (shared_manager == MAP_FAILED) { return -1; }
    
    close(fd_shm);
   
    if (new_mem == 0) { init_manager(); }
    
    return 0;
}

// clear all reservations from shared_manager
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
    printf("\nReservation Manager\n");
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

    printf("\n");
}

// attempt to add reservation to shared_manager
int add_reserve(char *client, unsigned int section, unsigned int table_num)
{
    int idx;

    if (table_num == 0)
    {
        // we attempt to choose smallest available table in input section
        idx = get_min_free_idx(section);
    }
    else
    {
        // we attempt to choose input table 
        idx = is_available(section, table_num);
    }
    
    if (idx == -1) { return -1; }

    shared_manager[idx].status = 1;
    strncpy(shared_manager[idx].client, client, strlen(client));

    return 0;
}

// if a table in the section is available return idx, if not return -1 
int get_min_free_idx(unsigned int section)
{
    int i = section;
    int end_of_section = section + TABLES_PER_SECTION;
    
    while (shared_manager[i].status == 1 && i < end_of_section) { i++; }

    if (i == end_of_section) { return -1; }

    return i;
}

// checks if section.table_num is available: if so return idx, if not return -1
int is_available(unsigned int section, unsigned int table_num)
{
    unsigned int offset = (section == IDX_SECTION_A) ? 100 : 190;
    unsigned int i = table_num - offset;
    
    if (shared_manager[i].status == 1) { return -1; }

    return i;
    
}

// checks if pair section-table_num are valid
int validate_args(unsigned int section, unsigned int table_num)
{
    unsigned int offset = (section == IDX_SECTION_A) ? 100 : 190;
    unsigned int i = table_num - offset;

    if (i < 2 * TABLES_PER_SECTION)
    {
        if ((section == IDX_SECTION_A && i < TABLES_PER_SECTION) ||
                (section == IDX_SECTION_B && i >= TABLES_PER_SECTION))
        {
            return 0;
        }
    }
    
    return -1;
}

// if SIGINT caught, kill current process
void handle_SIGINT(int signum)
{
    printf("\nCaptured signal: %d\n",signum);

    sem_wait(active);

    if (deallocate_mem() == -1) { handle_error("deallocate_mem()"); }
    if (deallocate_sem() == -1) { handle_error("deallocate_sem()"); }

    handle_success();
}

void handle_success(void) { exit(EXIT_SUCCESS); }

void handle_error(char *msg) 
{ 
    perror(msg);
    exit(EXIT_FAILURE);
}

int deallocate_mem(void)
{

    int i;

    if (munmap(shared_manager, mem_size) == -1) { return -1; }

    sem_getvalue(active, &i);
    if (i == 0)
    {
        // no other process using shared memory
        // requirements indicate we must unlink

        return shm_unlink(mem_name);
    }

    return 0;
}

int deallocate_sem(void)
{
    int i;

    if (sem_close(mutex) == -1) { return -1; }
        
    sem_getvalue(active, &i);
    if (i == 0)
    {
        // no other process using shared memory
        // requirements indicate we must unlink
        return sem_unlink(mutex_sem_name);
    }

    return 0;
}

void print_usage(void)
{ 
    printf("\nUsage\n"); 
    printf("--------------\n"); 
    printf("RUN: ./a.out <input_file>(optional)\n"); 
    printf("SHELL COMMANDS\n");
    printf("- init\n"); 
    printf("- exit\n"); 
    printf("- status\n"); 
    printf("- read <file_name>\n"); 
    printf("- reserve <client_name> <section> <table_number>(optional)\n\n"); 
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

