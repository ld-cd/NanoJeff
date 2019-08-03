# Zero all Registers
xor r0 r0
xor r1 r1
xor r2 r2
xor r3 r3

# Load our write address
li 15
lui 15
add r3 r0
xor r0 r0

# Zero our base memory location
sw r3 r0

# Load one to allow us to increment our register
li 1
add r1 r0

# Enter our main loop
:loop
	sw r3 r0
	add r0 r1
	j loop
