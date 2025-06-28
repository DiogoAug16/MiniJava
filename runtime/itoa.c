#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char* my_itoa(int value) {
    char buffer[12];
    sprintf_s(buffer, sizeof(buffer), "%d", value);
    char* result = malloc(strlen(buffer) + 1);
    if (result) {
        strcpy_s(result, strlen(buffer) + 1, buffer);
    }
    return result;
}
