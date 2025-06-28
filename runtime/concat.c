#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>

char* concat(int n, ...) {
    va_list args;
    va_start(args, n);
    
    int total_len = 0;
    for (int i = 0; i < n; i++) {
        total_len += strlen(va_arg(args, char*));
    }
    va_end(args);
    
    char* result = malloc(total_len + 1);
    result[0] = '\0';

    va_start(args, n);
    if (n > 0) {
        strcpy(result, va_arg(args, char*));
        for (int i = 1; i < n; i++) {
            strcat(result, va_arg(args, char*));
        }
    }
    va_end(args);

    return result;
}