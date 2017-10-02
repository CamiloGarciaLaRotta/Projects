#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>     // included only for exit codes

int main()
{
    int fd;

    // close STDOUT 
    close(STDOUT_FILENO);

    // open fd pointing towards file, 
    // it will be given the first free index, which was that of STDOUT
    fd = open("redirect.txt", O_WRONLY | O_CREAT, 0644);
    if (fd == -1) { return(EXIT_FAILURE); }

    printf("A simple program output.\n");

    // flush buffer of file descriptor
    if (fsync(fd) == -1) { return(EXIT_FAILURE); }

    return EXIT_SUCCESS;
}
