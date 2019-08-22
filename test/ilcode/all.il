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
@const1: INT = 10 [constant]
@const2: INT = 10 [constant]
b: INT = @const2
@const3: INT = 10 [constant]
@const4: INT = 2 [constant]
@const5: INT = 3 [constant]
@const6: INT = 10 [constant]
@const7: INT = 0 [constant]
@const8: INT = 0 [constant]
@const9: INT = 2 [constant]
@const10: INT = 0 [constant]
@const11: INT = 0 [constant]
@const12: INT = 1 [constant]
@const13: INT = 0 [constant]
@const14: INT = 10 [constant]
c: INT = @tmp1

.Text
assign @const1, a
add a, b, @tmp1
sub @tmp1, @const3, @tmp1
div @tmp1, @const4, @tmp1
multi @tmp1, @const5, @tmp1
multi a, b, @tmp2
param a 
param b 
call add, 2, @tmp3
add @tmp2, @tmp3, @tmp2
add @tmp1, @tmp2, @tmp1
assign @tmp1, b
i2b a, @tmp1
i2b @const6, @tmp2
equ @tmp1, @tmp2, @tmp1
jf @tmp1, @label1
add a, b, @tmp1
print @tmp1 
label @label1 
i2b a, @tmp1
i2b b, @tmp2
nequ @tmp1, @tmp2, @tmp1
jf @tmp1, @label3
print a 
label @label3 
i2b a, @tmp1
i2b @const7, @tmp2
equ @tmp1, @tmp2, @tmp1
i2b b, @tmp2
i2b @const8, @tmp3
g @tmp2, @tmp3, @tmp2
and @tmp1, @tmp2, @tmp1
i2b b, @tmp2
neg @const9, @tmp3
i2b @tmp3, @tmp3
equ @tmp2, @tmp3, @tmp2
or @tmp1, @tmp2, @tmp1
jf @tmp1, @label5
label @label5 
i2b a, @tmp1
i2b @const10, @tmp2
equ @tmp1, @tmp2, @tmp1
not @tmp1, @tmp1
jf @tmp1, @label7
label @label7 
i2b a, @tmp1
i2b b, @tmp2
g @tmp1, @tmp2, @tmp1
jf @tmp1, @label9
print a 
label @label11 
i2b a, @tmp1
i2b @const11, @tmp2
g @tmp1, @tmp2, @tmp1
jf @tmp1, @label12
sub a, @const12, @tmp1
assign @tmp1, a
jmp @label11 
jmp @label11 
label @label12 
jmp @label10
label @label9 
print b 
label @label13 
i2b b, @tmp1
i2b @const13, @tmp2
g @tmp1, @tmp2, @tmp1
jf @tmp1, @label14
multi b, @const14, @tmp1
assign @tmp1, b
jmp @label14 
jmp @label13 
label @label14 
label @label10 
param a 
param b 
call add, 2, @tmp1

[End Function main]

