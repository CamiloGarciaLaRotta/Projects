CFLAGS = -g -Wall -Wextra -Wshadow -Werror-implicit-function-declaration

all: clean compile run

run:
	./a.out

compile: tinyshell.c
	gcc $(CFLAGS) tinyshell.c

clean:
	rm a.out
