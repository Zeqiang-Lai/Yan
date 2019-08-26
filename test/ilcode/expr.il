[Begin Function @global]
Signature: func @global()

.Data
@tmp1: INT = 3 [constant]
a: INT = @tmp1
@tmp2: INT = 9 [constant]
b: INT = @tmp2
@tmp3: INT = 0 [constant]
c: INT = @tmp3

.Text
add a, b, @tmp4
assign @tmp4, c

[End Function @global]

