	.section	__TEXT,__text,regular,pure_instructions
	.build_version macos, 10, 14	sdk_version 10, 14
	.intel_syntax noprefix
	.section	__TEXT,__literal4,4byte_literals
	.p2align	2               ## -- Begin function main
LCPI0_0:
	.long	1092616192              ## float 10
	.section	__TEXT,__text,regular,pure_instructions
	.globl	_main
	.p2align	4, 0x90
_main:                                  ## @main
	.cfi_startproc
## %bb.0:
	push	rbp
	.cfi_def_cfa_offset 16
	.cfi_offset rbp, -16
	mov	rbp, rsp
	.cfi_def_cfa_register rbp
	xor	eax, eax
	movss	xmm0, dword ptr [rip + LCPI0_0] ## xmm0 = mem[0],zero,zero,zero
	mov	dword ptr [rbp - 4], 0
	mov	dword ptr [rbp - 8], 10
	movss	dword ptr [rbp - 12], xmm0
	pop	rbp
	ret
	.cfi_endproc
                                        ## -- End function

.subsections_via_symbols
