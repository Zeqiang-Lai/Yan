[Begin Function @global]
Signature: func @global()

.Data

.Text

[End Function @global]

[Begin Function add]
Signature: func add(INT ,INT) -> INT

.Data
x: INT [parameter]
y: INT [parameter]

.Text
add x, y, @tmp1
ret @tmp1 

[End Function add]

[Begin Function main]
Signature: func main()

.Data
a: INT = null
@tmp2: INT = 10 [constant]
@tmp3: INT = 10 [constant]
b: INT = @tmp3
@tmp5: INT = 10 [constant]
@tmp7: INT = 2 [constant]
@tmp9: INT = 3 [constant]
@tmp15: INT = 10 [constant]
@tmp19: INT = 0 [constant]
@tmp21: INT = 0 [constant]
@tmp24: INT = 2 [constant]
@tmp28: INT = 0 [constant]
@tmp32: INT = 0 [constant]
@tmp34: INT = 1 [constant]
@tmp36: INT = 0 [constant]
@tmp38: INT = 10 [constant]
c: INT = @tmp40

.Text
assign a
assign @tmp2, a
assign @tmp3, b
add a, b, @tmp4
sub @tmp4, @tmp5, @tmp6
div @tmp6, @tmp7, @tmp8
multi @tmp8, @tmp9, @tmp10
multi a, b, @tmp11
param a 
param b 
call add, @tmp12
add @tmp11, @tmp12, @tmp13
add @tmp10, @tmp13, @tmp14
assign @tmp14, b
equ a, @tmp15, @tmp16
jf @tmp16, @label1
add a, b, @tmp17
print @tmp17 
label @label1 
nequ a, b, @tmp18
jf @tmp18, @label3
print a 
label @label3 
equ a, @tmp19, @tmp20
g b, @tmp21, @tmp22
and @tmp20, @tmp22, @tmp23
neg @tmp24, @tmp25
equ b, @tmp25, @tmp26
or @tmp23, @tmp26, @tmp27
jf @tmp27, @label5
label @label5 
equ a, @tmp28, @tmp29
not @tmp29, @tmp30
jf @tmp30, @label7
label @label7 
g a, b, @tmp31
jf @tmp31, @label9
print a 
label @label11 
g a, @tmp32, @tmp33
jf @tmp33, @label12
sub a, @tmp34, @tmp35
assign @tmp35, a
jmp @label11 
jmp @label11 
label @label12 
jmp @label10
label @label9 
print b 
label @label13 
g b, @tmp36, @tmp37
jf @tmp37, @label14
multi b, @tmp38, @tmp39
assign @tmp39, b
jmp @label14 
jmp @label13 
label @label14 
label @label10 
param a 
param b 
call add, @tmp40
assign @tmp40, c

[End Function main]

