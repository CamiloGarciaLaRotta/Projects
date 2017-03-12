// CAMILO GARCIA LA ROTTA
// 260657037
// Q2

#include <stdio.h>  
#include <stdlib.h> 
#include <string.h> // JUST FOR STRLEN I PROMISE

// Helper methods
void trimNewLine(char* s) {
  size_t len = strlen(s);
  if(len > 0 && s[len-1] == '\n'){
    s[len-1] = '\0';
  }
}

void my_strncpy(char* s, char* t, int n) {
  // pointers
  char* ps = s;
  char* pt = t;
  
  // copy n first digits of t
  int i;
  for(i = 0;i<n;i++) {
    *ps = *pt;
    
    ps++;
    pt++;
  }
  
  // end the string afterwards
  *ps='\0';
}

void my_strncat(char* s, char* t, int n) {
  // pointers
  char* ps = s;
  ps += strlen(s);
  char* pt = t;

  // concat n first digits of t
  int i;
  for(i=0;i<n;i++) {
    *ps = *pt;

    ps++;
    pt++;
  }

  // end the string afterwards
  *ps = '\0';
}

char my_strncmp(char* s, char* t, int n) {
  // pointers
  char* ps = s;
  char* pt = t;

  // find index of first differing char
  // between s and t
  int i=0;
  while(i < n && *ps == *pt) {
    i++;
    ps++;
    pt++;
  }

  //compare differing char
  if(*ps < *pt) return '<';  
  if(*ps > *pt) return '>';
  else return '=';
}

// Program entry point
int main(int argc, char** argv) {
  
  //string buffers
  char s[255];
  char t[255];
  int n;

  // prompt user for input

  printf("\nEnter the first string: ");
  fgets(s,sizeof(s),stdin);

  printf("\nEnter the second string: ");
  fgets(t,sizeof(t),stdin);
  
  printf("\nEnter the number: ");
  scanf("%d", &n);
 
  // trim \n and save backup
  trimNewLine(s);
  char s_backup[255];
  strcpy(s_backup,s);

  trimNewLine(t);
  char t_backup[255];
  strcpy(t_backup,t);
 
  // calculate results
  my_strncpy(s,t,n);
  printf("\nstrncpy is \"%s\"", s);
 
  strcpy(s, s_backup);
  strcpy(t, t_backup);

  my_strncat(s,t,n);
  printf("\nstrncat is \"%s\"", s);

  printf("\nstrncmp is \"%s\" %c \"%s\"", s_backup, my_strncmp(s_backup,t_backup,n), t_backup);
  
  return EXIT_SUCCESS;
}
