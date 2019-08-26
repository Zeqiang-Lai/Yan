global    start
extern   _printf

section   .data
message  db        "Hello, World", 10      ; note the newline at the end
format: db       "%g", 10, 0
a   dd  10
b   dd  11

section   .text
start:    mov       rax, 0x02000004         ; system call for write
          mov       rdi, 1                  ; file handle 1 is stdout
          mov       rsi, message            ; address of string to output
          mov       rdx, 13                 ; number of bytes
          syscall                           ; invoke operating system to do the write
          lea      rdi, [format]          ; 1st arg to printf
          mov      rax, 1                 ; printf is varargs, there is 1 non-int argument
          call     _printf                ; printf(format, sum/count)
          mov       rax, 0x02000001         ; system call for exit
          xor       rdi, rdi                ; exit code 0
          syscall                           ; invoke operating system to exit