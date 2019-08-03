xor r0 r0
xor r1 r1
xor r2 r2
xor r3 r3

li 15
lui 15
add r3 r0
xor r0 r0
sw r3 r0
li 1
add r1 r0

:loop
sw r3 r0
add r0 r1
j loop
