CFLAGS = -ggdb  -Wall -Wextra -Wshadow -Werror-implicit-function-declaration -pedantic -pedantic-errors

all: clean compile run

run:
	./a.out

compile: tinyshell.c
	gcc $(CFLAGS) tinyshell.c

clean:
	rm a.out
