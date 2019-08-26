# Assembly Code Generation

- global variable should be declare in `.data` section.
- use stack and `push`, `pop` command to implement function.

## Data Width



## Calling Convention

`ESP` : top of stack.

`EBP`: next location of return address.

**When a function is called:**

1. caller must push parameters into stack before calling the function. When function returns, caller is responsible to pop parameters.

```assembly
push   dword 1        ; pass 1 as parameter
call   fun
add    esp, 4					; remove parameter from stack
```

2. callee store frame into stack.

```assembly
subprogram_label:
	push  ebp
  mov   ebp, esp
; subprogram code
	pop		ebp 
	ret
```

**Local variables**



## Expression

- add a, b, c

```
// int + float

```

