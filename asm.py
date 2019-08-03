#!/usr/bin/env python3

import argparse
from enum import Enum
from functools import reduce

class OutputFormat(Enum):
    COE = 1
    HEX = 2
    BINARY = 3

class Ops(Enum):
    SL = 0
    SR = 1
    NOT = 2
    AND = 3
    OR = 4
    XOR = 5
    ADD = 6
    SUB = 7
    LW = 8
    SW = 9
    LI = 10
    LUI = 11
    JR = 12
    BZ = 13
    JI = 14
    JIR = 15
    J = 17

class Register(Enum):
    R0 = 0
    R1 = 1
    R2 = 2
    R3 = 3

class Label:
    def __init__(self, name, pc):
        self.name = name
        self.pc = pc

class Instruction:
    def __init__(self, op, r1 = None, r2 = None, imm = None):
        assert type(op) == Ops, "op must be of type Ops"
        if op == Ops.J:
            assert imm != 0, "J cannot Jump to itself"
            assert abs(imm) <= 16, "J Cannot Jump more than 16 instructions in either direction"
            if imm < 0:
                op = Ops.JIR
            if imm > 0:
                op = Ops.JI
            imm = abs(imm) - 1
        if op.value <= 9 or op.value == 12:
            assert type(r1) == Register and type(r2) == Register and imm == None, "{:s} is an R-type insutruction".format(op.name)
        if op.value in [10, 11, 14, 15]:
            assert r1 == None and r2 == None and type(imm) == int, "{:s} is an I-type insutruction".format(op.name)
            assert imm < 16 and imm >= 0, "I type immediates have a range of 0 to 15"
        if op.value == 13:
            assert type(r1) == Register and r2 == None and type(imm) == int, "{:s} is an LI-type insutruction".format(op.name)
            assert imm < 4 and imm >= 0, "LI type immediates have a range of 0, to 3"
        self.op = op
        if imm:
            self.imm = imm
        if r1:
            self.r1 = r1
        if r2:
            self.r2 = r2

    def __str__(self):
        op = self.op.name + " "
        r1 = self.r1.name + " " if self.r1 else ""
        r2 = self.r2.name if self.r2 else ""
        imm = str(self.imm) if self.imm else ""
        return op + r1 + r2 + imm
    
    def toInt(self):
        return self.op.value << 4 | (self.r1.value << 2 if hasattr(self, "r1") else 0) | (self.imm if hasattr(self, "imm") else 0) | (self.r2.value if hasattr(self, "r2") else 0)

def lintLines(lines):
    return [k.strip() for k in [i.upper() for i in lines if i.strip()] if k[0] != "#"]

def getTags(lines):
    pc = 0
    tags = {}
    for line in lines:
        if line[0] == ":":
            assert len(line) > 1, "Tag Identifier without named tag (appropriate format :Tag)"
            assert all([not i.isspace() for i in line]), "{:s} tag contains whitespace".format(line)
            assert line[1] != "R" and (not line[1].isdigit()), "Tags may not begin with R or numeric digits"
            tags[line[1:]] = pc
        else:
            pc += 1
    return tags

def filterTags(lines):
    return [line for line in lines if line[0] != ":"]

def resolveImmediate(token, pc, tags):
    if (token[0].isdigit() or token[0] == "-") and (token[1:].isdigit() or not token[1:]):
        return int(token)
    else:
        assert token in tags, "{:s} is not a valid token".format(token)
        return tags[token] - pc

def genInstruction(line, pc, tags):
    regs = {i.name : i for i in Register}
    ops = {i.name : i for i in Ops}

    tokens = line.split()
    assert tokens[0] in ops, "{:s} is not a valid op".format(tokens[0])
    assert len(tokens) == 2 or len(tokens) == 3, "{:s} is not a valid instruction".format(line)
    op = ops[tokens[0]]
    if len(tokens) == 3:
        if tokens[2][0] == "R":
            return Instruction(op, r1=regs[tokens[1]], r2=regs[tokens[2]])
        else:
            imm = resolveImmediate(tokens[2], pc, tags)
            if tokens[2] in tags:
                imm -= 1
            return Instruction(op, r1=regs[tokens[1]], imm=imm)
    if len(tokens) == 2:
        imm = resolveImmediate(tokens[1], pc, tags)
        if tokens[1] in tags:
            if op == Ops.JI:
                imm -= 1
            if op == Ops.JIR:
                imm = abs(imm) - 1
        return Instruction(op, imm=imm)

def getHex(i):
    return format(i, "02x")

def getBinary(i):
    return format(i, "08b")

makeHex = lambda insts: [getHex(i.toInt()) for i in insts]
makeBinary = lambda insts: [getBinary(i.toInt()) for i in insts]

def makeCoe(insts):
    bins = reduce((lambda a, b: a + " " + b), makeBinary(insts))
    return ["memory_initialization_radix=2;", "memory_initialization_vector=" + bins + ";"]

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Assemble assembly for the NanoJeff Processor")
    parser.add_argument("-f", help="Select output format (COE, HEX, BINARY)", type=str, default="HEX", dest="format")
    parser.add_argument(metavar="INPUT.S", type=str, dest="input")
    parser.add_argument(metavar="OUTPUT", type=str, dest="output")
    args = parser.parse_args()
    assert args.format in [i.name for i in OutputFormat]

    lines=[]
    with open(args.input, "r") as f:
        lines = [line for line in f]
    assert lines, "File empty or read failed"

    lines = lintLines(lines)
    tags = getTags(lines)
    lines=filterTags(lines)

    insts = []
    pc = 0
    for line in lines:
        insts.append(genInstruction(line, pc, tags))
        pc += 1
    assert insts, "No instructions decoded"

    output = []
    outFormat = {i.name : i for i in OutputFormat}[args.format]
    if outFormat == OutputFormat.HEX:
        output = makeHex(insts)
    elif outFormat == OutputFormat.BINARY:
        output = makeBinary(insts)
    elif outFormat == OutputFormat.COE:
        output = makeCoe(insts)
    assert output, "No output generated"

    with open(args.output, "w") as f:
        for i in output:
            f.write(i + "\n")
