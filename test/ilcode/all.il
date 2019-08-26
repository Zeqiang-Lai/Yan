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
@tmp16: INT = 10 [constant]
@tmp24: INT = 0 [constant]
@tmp28: INT = 0 [constant]
@tmp33: INT = 2 [constant]
@tmp39: INT = 0 [constant]
@tmp47: INT = 0 [constant]
@tmp50: INT = 1 [constant]
@tmp53: INT = 0 [constant]
@tmp56: INT = 10 [constant]
c: INT = @tmp58

.Text
assign @tmp2, a
add a, b, @tmp4
sub @tmp4, @tmp5, @tmp6
div @tmp6, @tmp7, @tmp8
multi @tmp8, @tmp9, @tmp10
multi a, b, @tmp11
param a 
param b 
call add, 2, @tmp12
add @tmp11, @tmp12, @tmp13
add @tmp10, @tmp13, @tmp14
assign @tmp14, b
i2b a, @tmp15
i2b @tmp16, @tmp17
equ @tmp15, @tmp17, @tmp18
jf @tmp18, @label1
add a, b, @tmp19
print @tmp19 
label @label1 
i2b a, @tmp20
i2b b, @tmp21
nequ @tmp20, @tmp21, @tmp22
jf @tmp22, @label3
print a 
label @label3 
i2b a, @tmp23
i2b @tmp24, @tmp25
equ @tmp23, @tmp25, @tmp26
i2b b, @tmp27
i2b @tmp28, @tmp29
g @tmp27, @tmp29, @tmp30
and @tmp26, @tmp30, @tmp31
i2b b, @tmp32
neg @tmp33, @tmp34
i2b @tmp34, @tmp35
equ @tmp32, @tmp35, @tmp36
or @tmp31, @tmp36, @tmp37
jf @tmp37, @label5
label @label5 
i2b a, @tmp38
i2b @tmp39, @tmp40
equ @tmp38, @tmp40, @tmp41
not @tmp41, @tmp42
jf @tmp42, @label7
label @label7 
i2b a, @tmp43
i2b b, @tmp44
g @tmp43, @tmp44, @tmp45
jf @tmp45, @label9
print a 
label @label11 
i2b a, @tmp46
i2b @tmp47, @tmp48
g @tmp46, @tmp48, @tmp49
jf @tmp49, @label12
sub a, @tmp50, @tmp51
assign @tmp51, a
jmp @label11 
jmp @label11 
label @label12 
jmp @label10
label @label9 
print b 
label @label13 
i2b b, @tmp52
i2b @tmp53, @tmp54
g @tmp52, @tmp54, @tmp55
jf @tmp55, @label14
multi b, @tmp56, @tmp57
assign @tmp57, b
jmp @label14 
jmp @label13 
label @label14 
label @label10 
param a 
param b 
call add, 2, @tmp58

[End Function main]

