package nanoJeff

import spinal.core._
import spinal.lib._
import NanoISA._

class NanoCtrl extends Component{

  val io = new Bundle{
    val r1, r2 = in Bits(2 bits)
    val op = in(NanoOp())
    val aluSel = out Bits(3 bits)
    val mWEn = out Bool
    val rWEn = out Bool
  }

  io.rWEn := False
  io.mWEn := False
  io.aluSel := B"100"

  when(NanoISA.isArith(io.op)){
    io.rWEn := True
    io.aluSel := io.op.asBits(2 downto 0)
  }.elsewhen(io.op === NanoOp.LW || io.op === NanoOp.JR || NanoISA.isImmLoad(io.op)){
    io.rWEn := True
  }.elsewhen(io.op === NanoOp.SW){
    io.mWEn := True
  }.elsewhen(io.op === NanoOp.BZ || NanoISA.isImmJump(io.op)){
    io.aluSel := B"110"
  }
}
