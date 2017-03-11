// CAMILO GARCIA LA ROTTA
// ID 260657037
// Q1
//
// NOTE: NO SPECIFICATION WAS GIVEN AS TO IF THE INPUT.TXT
//       WAS RECIEVED VIA COMMANDLINE OR IF IT WAS HARDCODED.
//       I'LL PROCEED WITH THE ASSUMPTION OF THE LATTER.

#include <stdio.h>
#include <stdlib.h>
#include <string.h> // for strerror
#include <errno.h>  // for error handling during fopen
#include <ctype.h>  // for isalpha

// Global variables
// file pointer for I/O
FILE* fp = NULL;

const char* filename = "input.txt";

// calculate summation of all non-null integers in array
int getSum(int values[], int maxIndex) {
  int sum=0;
  
  int i;
  for(i=0;i<maxIndex;i++){
    sum += values[i];
  }

  return sum;
}

// calculate fibonnaci sequence of given index
int getFib(int n) {
  int f[n+1];
  f[1] = f[2] = 1;
  for (int i = 3; i <= n; i++)
    f[i] = f[i-1] + f[i-2];
  return f[n];
}

// Program entry point
int main(int argc, char** argv) {
  
  // attempt to open file
  if((fp=fopen(filename, "r")) == NULL) {
    // unable to open file
    printf("Unable to open %s, \n%s\n", filename, strerror(errno));
    return EXIT_FAILURE;
  }

  // buffer to hold read csv
  char line[255];
  int values[255];
  int valueIndex = 0;
  char* token;
  

  // read line
  if(fgets(line, 255, fp) != NULL) {    
    // get first token
    token = strtok(line, " ");
    
    // store numerical value
    while(token != NULL) {
      values[valueIndex] = atoi(token);
      valueIndex++;
      token = strtok(NULL, " ");
    }

    // calculate summation
    printf("The sum of numbers is %d\n", getSum(values, valueIndex));

    // calculate fibonnaci sequence
    int i;
    for(i=0;i<valueIndex;i++) {
      printf("Fib(%d) is %d\n", values[i], getFib(values[i]));
    }
  }
  
  fclose(fp);

  return EXIT_SUCCESS;
}


