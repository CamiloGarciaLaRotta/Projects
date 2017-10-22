CFLAGS = -ggdb -g  -Wall -Wextra -Wshadow -Werror-implicit-function-declaration -pedantic -pedantic-errors

all: clean compile run

run:
	./a.out

compile: reservation_manager.c
	gcc $(CFLAGS) reservation_manager.c

clean:
	rm a.out
