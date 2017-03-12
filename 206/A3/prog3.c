// CAMILO GARCIA LA ROTTA
// 260657037
// Q3

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <string.h> // for atoi
#include "lib.h"

int main(int argc, char** argv) {

  // validate input
  if(argc != 3) {
    printf("Usage: ./prog3 x y | x,y positive integers\n");
    return EXIT_FAILURE;
  }

  printf("MAIN PROC STARTED\n");

  // store arguments
  int x = atoi(argv[1]);
  int y = atoi(argv[2]);

  printf("Number of Random Numbers : %d\n", x);
  printf("Fibonacci Input : %d\n", y);

  // child PIDs
  int status;
  int pid;
  int sortChildPID;
  int fibChildPID;

  // sort children
  sortChildPID = fork();
  if(sortChildPID == 0) {

    // inide sort child 
    printf("SORT PROC STARTS\n");
    
    sort(x);
    
    printf("SORT PROC EXITS\n");
    exit(EXIT_SUCCESS);
  } else {
    
    // inside parent, launch fibChild
    printf("BUBBLE SORT PROC CREATED\n");
    
    fibChildPID = fork();

    if(fibChildPID == 0){
    
      // inside fib child
      printf("FIB PROC STARTS\n");

      printf("Input Number: %d\n",y);
      
      printf("Fibonnaci Number f(%d) is %d\n", y, fib(y));
      

      printf("FIB PROC EXITS\n");
      exit(EXIT_SUCCESS);
    } else {
      printf("FIB PROC CREATED\n");
    }
  }

  printf("MAIN PROC WAITS\n");
  pid = wait(&status);
  pid = wait(&status);

  printf("MAIN PROC EXITS\n");
  exit(EXIT_SUCCESS);
}

