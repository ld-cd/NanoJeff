package nanoJeff

import spinal.core._
import spinal.lib._

object NanoISA {
  object NanoOp extends SpinalEnum(binarySequential) {
    val SL, SR, NOT, AND, OR, XOR, ADD, SLT, LW, SW, LI, LUI, JR, BZ, JI, JIR = newElement()
  }

  object NanoRegs extends SpinalEnum(binarySequential) {
    val X0, X1, X2, X3  = newElement()
  }


  def isR(inst : NanoOp.C) : Bool = {
    (isArith(inst)
    | inst === NanoOp.LW
    | inst === NanoOp.SW
    | inst === NanoOp.JR)
  }
  def isArith(inst : NanoOp.C) : Bool = {
    !inst.asBits(3)
  }

  def isImmLoad(inst: NanoOp.C) : Bool = {
    inst.asBits(3 downto 1) === B"101"
  }

  def isImmJump(inst: NanoOp.C) : Bool = {
    inst.asBits(3 downto 1) === B"111"
  }

  def isI(inst: NanoOp.C) : Bool = {
    isImmLoad(inst) | isImmJump(inst)
  }
}