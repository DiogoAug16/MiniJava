#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char* my_itoa(int value) {
    char buffer[12]; // suporta int 32 bits + \0
    sprintf(buffer, "%d", value);
    char* result = malloc(strlen(buffer) + 1);
    if (result) {
        strcpy(result, buffer);
    }
    return result;
}
