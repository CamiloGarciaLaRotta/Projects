#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>     // included only for exit codes

int main()
{
        // Open output file
        int fd = open("output.txt", O_WRONLY | O_CREAT, 0644);
        if (0 > fd)
        {
            perror("Error opening output.txt");
            return(EXIT_FAILURE);
        }
        
        // Duplicate stdout to output file
        int err = dup2(fd,STDOUT_FILENO);
        if (0 > err) 
        {
            perror("Error duplicating output.txt to STDOUT");
            return(EXIT_FAILURE);
        }

        close(fd);

	printf("A simple program output.");
        
        fflush(stdout);

	return 0;
}
