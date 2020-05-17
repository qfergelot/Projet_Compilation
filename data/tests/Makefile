CC = gcc
CFLAGS = -std=c99 -Wall
LDFLAGS = 
SRC = $(wildcard ../*.c)
EXEC = $(SRC:../%.c=%)

all : $(EXEC)

%: ../%.c
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

.PHONY: clean
clean:
	rm -rf $(EXEC)
