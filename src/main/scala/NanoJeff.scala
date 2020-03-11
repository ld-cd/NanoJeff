package nanoJeff

import spinal.core._
import spinal.lib._
import NanoISA._

class NanoJeff(haltSupport: Boolean = false, renSupport: Boolean = false) extends Component {

  val io = new Bundle{
    val dAddr = out UInt(8 bits)
    val dRData = in Bits(8 bits)
    val dWData = out Bits(8 bits)
    val dREn = if (renSupport) out(Bool) else null
    val dWEn = out Bool

    val iAddr = out UInt(8 bits)
    val iRData = in Bits(8 bits)
    val halt = if (haltSupport) in(Bool) else null
  }

  val alu = new NanoALU
  val ctrl = new NanoCtrl
  val iDecode = new NanoIDecode
  val immDecode = new NanoImmDecode
  val regfile = new NanoReg

  val programCounter = RegInit(U"x00")

  val pcp, pcm = UInt(8 bits)

  val op = NanoOp()

  val busLogic = new Area {
    io.iAddr := programCounter
    io.dAddr := regfile.io.r2
    io.dWData := regfile.io.r2.asBits
    io.dWEn := ctrl.io.mWEn
    if (renSupport) {
      io.dREn := op === NanoOp.LW
    }
    when(io.dWEn){
      io.dAddr := regfile.io.r1
    }
  }

  pcp := programCounter + 1
  pcm := programCounter - 1

  iDecode.io.instr := io.iRData
  op := iDecode.io.op

  ctrl.io.op := op

  immDecode.io.op := op
  immDecode.io.instr := io.iRData

  val regfileLogic = new Area {
    regfile.io.r1Sel := iDecode.io.r1Sel
    regfile.io.r2Sel := iDecode.io.r2Sel
    regfile.io.wEn := ctrl.io.rWEn
    regfile.io.wData := alu.io.result
    when(op === NanoOp.JR){
      regfile.io.wData := pcp
    }.elsewhen(op === NanoOp.LW){
      regfile.io.wData := io.dRData.asUInt
    }.elsewhen(op === NanoOp.LI){
      regfile.io.wData := B(8 bits, (7 downto 4) -> regfile.io.r1.asBits(7 downto 4)
      , (3 downto 0) -> immDecode.io.imm.asBits(3 downto 0)).asUInt
    }.elsewhen(op === NanoOp.LUI){
      regfile.io.wData := B(8 bits, (3 downto 0) -> regfile.io.r1.asBits(3 downto 0)
      , (7 downto 4) -> immDecode.io.imm.asBits(3 downto 0)).asUInt
    }
  }

  val aluLogic = new Area {
    alu.io.aluSel := ctrl.io.aluSel
    alu.io.a := regfile.io.r1
    when((op === NanoOp.BZ) || op === NanoOp.JI){
      alu.io.a := pcp
    }.elsewhen(op === NanoOp.JIR){
      alu.io.a := pcm
    }
    alu.io.b := regfile.io.r2
    when(immDecode.io.isImm){
      alu.io.b := immDecode.io.imm
    }
  }

  val branchingLogic = new Area {
    programCounter := pcp
    when(op === NanoOp.JR){
      programCounter := regfile.io.r2
    }.elsewhen((op === NanoOp.BZ && regfile.io.r1 === 0) || NanoISA.isImmJump(op)){
      programCounter := alu.io.result
    }
  }

  val haltingLogic = if (haltSupport) new Area {
    when(io.halt){
      regfile.io.wEn := False
      io.dWEn := False
      programCounter := programCounter
    }
  } else null
}

object Main {
  def main(args: Array[String]){
    SpinalVerilog(new NanoJeff)
  }
}
