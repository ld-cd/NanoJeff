package nanoJeff

import spinal.core._
import spinal.lib._

class NanoJeff extends Component {
  val io = new Bundle{
    val addr1, addr2, wData = out Bits(8 bits)
    val rData1, rData2 = in Bits(8 bits)
    val wEn = out Bool
  }

  val alu = new NanoALU
  val ctrl = new NanoCtrl
  val iDecode = new NanoIDecode
  val immDecode = new NanoImmDecode
  val regfile = new NanoReg

  val programCounter = RegInit(U"x00")

  val pcp, pcm = UInt(8 bits)

  val op = Bits(4 bits)

  io.addr1 := programCounter.asBits
  io.addr2 := regfile.io.r2.asBits
  io.wData := regfile.io.r2.asBits
  io.wEn := ctrl.io.mWEn
  when(io.wEn){
    io.addr2 := regfile.io.r1.asBits
  }

  pcp := programCounter + 1
  pcm := programCounter - 1

  iDecode.io.instr := io.rData1
  op := iDecode.io.op

  ctrl.io.op := op

  immDecode.io.op := op
  immDecode.io.instr := io.rData1

  regfile.io.r1Sel := iDecode.io.r1Sel
  regfile.io.r2Sel := iDecode.io.r2Sel
  regfile.io.wEn := ctrl.io.rWEn
  regfile.io.wData := alu.io.result
  when(op === B"1100"){
    regfile.io.wData := pcp
  }.elsewhen(op === B"1000"){
    regfile.io.wData := io.rData2.asUInt
  }.elsewhen(op === B"1010"){
    regfile.io.wData := B(8 bits, (7 downto 4) -> regfile.io.r1.asBits(7 downto 4)
    , (3 downto 0) -> immDecode.io.imm.asBits(3 downto 0)).asUInt
  }.elsewhen(op === B"1011"){
    regfile.io.wData := B(8 bits, (3 downto 0) -> regfile.io.r1.asBits(3 downto 0)
    , (7 downto 4) -> immDecode.io.imm.asBits(3 downto 0)).asUInt
  }

  alu.io.aluSel := ctrl.io.aluSel
  alu.io.a := regfile.io.r1
  when((op === B"1101") || (op === B"1110")){
    alu.io.a := pcp
  }.elsewhen(op === B"1111"){
    alu.io.a := pcm
  }
  alu.io.b := regfile.io.r2
  when(immDecode.io.isImm){
    alu.io.b := immDecode.io.imm
  }

  programCounter := pcp
  when(op === B"1100"){
    programCounter := regfile.io.r2
  }.elsewhen((op === B"1101") || (op(3 downto 1) === B"111")){
    programCounter := alu.io.result
  }
}

object Main {
  def main(args: Array[String]){
    SpinalVerilog(new NanoJeff)
  }
}
