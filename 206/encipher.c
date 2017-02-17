#include <stdio.h>  
#include <stdlib.h> 
#include <string.h> // for strerror
#include <errno.h>  // for error handling during fopen
#include <ctype.h>  //for isaplha

//// Global Variables

// file to encrypt/decrypt
FILE* file = NULL;

// lookup alphabet
const char ALPHABET[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

//// Helper Methods

// encrypt a given character by a given key
char cypher(char c, int key) {
  // to distinguish if char is upper or lower

  // normalize character to upper case with value [0-25]
  // then map onto ALPHABET
  char cyphered_c = ALPHABET[(toupper(c) + 'Z' + 1 - key)%26];
  
  //printf("%c - %d", cyphered_c, cyphered_c);

  // revert character to its original case (upper/lower)
  int padding = (isupper(c)) ? 0 : 32;

  return cyphered_c + padding;
}

//// Program Entry Point
int main(int argc, char** argv) {
  

  // verify argument count
  if (argc != 3) {
    printf("Syntax: ./encipher.sh FILE KEY \n");
    return EXIT_FAILURE;
  }
  
  char* file_name = argv[1];
  int key = atoi(argv[2]); // NOTE THIS IS THE ONLY LINE THAT CHANGES BETWEEN CIPHER AND DECIPHER
                           // BECAUSE 2 DIFFERENT FILES ARE ASKED (ENCIPHER/DECIPHER) I SUBMITTED
                           // 2 FILES WITH ONLY THIS LINE CHANGED. I DIDNT KNOW IF I COULD SUBMIT
                           // ANOTHER HEADER FILE CONTAINING THE COMMON FUNCTIONALITIES
  // attempt to open file
  if((file=fopen(file_name, "r+")) == NULL) {
    // unable to find file
    printf("Unable to open %s \n%s\n", file_name, strerror(errno));
    return EXIT_FAILURE;
  }
  
  // buffer to store char and cyphered char
  char c;
  char cyphered_c;

  // loop until EOF
  while((c=fgetc(file)) != EOF) {
    
    // only treat alphabetic chars
    if (isalpha(c)) {
      // calculate cyphered char
      cyphered_c = cypher(c, key);
      
      // back stream position by 1
      fseek(file, -1, SEEK_CUR); 
      
      // write char 
      fputc(cyphered_c, file);
    }
  }

  // verify if any error happened during loop
  if (ferror(file)) {
    printf("Error while reading file\n");
    return EXIT_FAILURE;
  }

  // close file
  fclose(file);

  return EXIT_SUCCESS;
}
