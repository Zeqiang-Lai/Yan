func-begin power
l1:
< i e t1
false l
* result base t2
= t2    _   result
+ i 1 t3
= t3 _ i
jmp l1
l2:
return result
func-end power

func-begin main
== b 9 t4
false l3
= 10 _ b
l3:
l4:
< a 3 t5
false
+ b 1 t6
= t6 _ b
> 5 3 t7
false
jmp l4
