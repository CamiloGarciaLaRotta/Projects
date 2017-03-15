// CAMILO GARCIA LA ROTTA
// 260657037
// Q3

#include <stdlib.h>
#include <stdio.h>
#include <time.h> // for extra randomness
#include "lib.h"

void sort(int x) {  
  // generate random numbers
  int numbers[x];
  srand (time (NULL));
  
  printf("Random Numbers Generated:\n");
  int i;
  for(i=0;i<x;i++) {
    numbers[i] = rand() % 100;
    printf("%4d", numbers[i]);
  }
  printf("\n");

  // buuble sort array
  int j;
  for(i=0;i<x-1;i++) {
    for(j=0;j<x-i-1;j++){
      if(numbers[j] > numbers[j+1]) {
        int tmp = numbers[j];
        numbers[j] = numbers[j+1];
        numbers[j+1] = tmp;
      }
    }
  }

  printf("Sorted Sequence:\n");
  for(i=0;i<x;i++) {
    printf("%4d", numbers[i]);
  }
  printf("\n");
}

long fib(long y) {
  if (y <= 2) return 1;
  else return fib(y-1) + fib(y-2);
}
