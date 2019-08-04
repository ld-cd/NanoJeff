# NanoJeff
NanoJeff is an exceedingly small 8 bit RISC processor meant for FPGAs implemented in SpinalHDL. It has 16 instructions which operate on four 8-bit registers.

The processer itself is currently not pipelined to save on area, but is simple enough that it would be trivial to accomplish without much cost

## Instruction set
The instruction format is incredibly simple: The highest 4 bits always contain the opcode, the lower 4 bits meaning is dependent on instruction type. For R type instructions the highest 2 of these bits represent the destination register (a), and the lower two represent the source register (b). For I type instructions these bits represent the immediate. In SI type instructions (currently the only SI instruction is bz) the highest two represent the a register and the lower to represent the small immediate.

The four registers R0-R3 (or x0-x3) are encoded as 00-11 respectivly.

### Example Encoding
```
R-type:
    add     r1  r2
    0110    01  10

I-type:
    ji      13
    1110    1101

SI-type:
    bz      r2  1
    1101    10  01
```

### Instruction Table
`PC` indicates the program counter, `M(r)` indicates the memory address which is pointed to by R, `?` is the C ternary operator, and `x0` indicates the 0th register.
| Mnemonic | Opcode | RTL Description                      | Type |
|----------|--------|--------------------------------------|------|
| sl       | 0b0000 | a := a << b                          | R    |
| sr       | 0b0001 | a := a >> b                          | R    |
| not      | 0b0010 | a := ~b                              | R    |
| and      | 0b0011 | a := a & b                           | R    |
| or       | 0b0100 | a := a \| b                          | R    |
| xor      | 0b0101 | a := a ^ b                           | R    |
| add      | 0b0110 | a := a + b                           | R    |
| sub      | 0b0111 | a := a - b                           | R    |
| lw       | 0b1000 | a := M(b)                            | R    |
| sw       | 0b1001 | M(a) := b                            | R    |
| li       | 0b1010 | x0[3:0] := I[3:0]                    | I    |
| lui      | 0b1011 | x0[7:4] := I[3:0]                    | I    |
| jr       | 0b1100 | PC := b, a := PC + 1                 | R    |
| bz       | 0b1101 | PC := a ? (PC + 1):(PC + 1 + I[1:0]) | SI   |
| ji       | 0b1110 | PC := PC + 1 + I[3:0]                | I    |
| jir      | 0b1111 | PC := PC - 1 - I[3:0]                | I    |

## Assembler
This repo contains a simple assembler that transforms mnemonic representation of assembly language into ascii Binary or Hex for verilog testbenches, and COE files for Vivado designs.

The mnemonic representation is relitively simple, it supports all 16 of the processors instructions along with one psuedo intruction, J which is transformed into ji or jir depending on context. At this time the language is case insensitive, all immediates must be written in decimal, and only one instruction is permited per line. Instructions arguments should be seperated by spaces, and registers must take the r0-r3 format. Comments are permitted, but must be proceaded by "#".

The language also supports a tagging system for easy loop writing tags are denoted with the syntax `:tagname`, and can be reffered to from immediate instructions. They must exist on their own line and cannot begin with the letter R.

An example assembly program might look like the following:

```assembly
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
```

### Invocation
Invoking the assembler is as simple as executing ./asm.py infile.s output.hex. By default the output will be ascii hex, but others may be specified with the -f flag

```
usage: asm.py [-h] [-f FORMAT] INPUT.S OUTPUT

Assemble assembly for the NanoJeff Processor

positional arguments:
  INPUT.S
  OUTPUT

optional arguments:
  -h, --help  show this help message and exit
  -f FORMAT   Select output format (COE, HEX, BINARY)
```

## Building
With sbt and scala installed on your system, simply executing `make` or `sbt run` in the top level directory will build NanoJeff.v which contains everything required to add NanoJeff to a design. By default the design is built for dual ported memory. The io_addr1 and io_rData1 signals feed the instruction pipeline, and the io_addr2 io_rData2 io_wData and io_wEn signals control the second port which is accessed by load and store instructions. Because of this implementation the instructions may exist in a ROM seperate from the data accesed by loads and stores.

### Running Simulations
With Icarus Verilog installed simply running make test will load the example program in `tests/increment_mem.s` into memory using the test.v test bench and begin execution. Execution may be halted at any time by pressing Ctrl+C and issuing the finish command. If this is not done the program will execute forever. The output of the simulation is the `NanoJeff.vcd` file which may be opened and explored in gtkwave or the wave viewer of your choice.