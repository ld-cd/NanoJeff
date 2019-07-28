package nanoJeff

import spinal.core._
import spinal.lib._

class NanoCtrl extends Component{
  val io = new Bundle{
    val r1, r2 = in Bits(2 bits)
    val op = in Bits(4 bits)
    val aluSel = out Bits(3 bits)
    val mWEn = out Bool
    val rWEn = out Bool
  }

  io.rWEn := False
  io.mWEn := False
  io.aluSel := B"100"

  when(io.op(3) === False){
    io.rWEn := True
    io.aluSel := io.op(2 downto 0)
  }.elsewhen((io.op === B"1000") || (io.op === B"1100")
    || (io.op(3 downto 1) === B"101")){
    io.rWEn := True
  }.elsewhen(io.op === B"1001"){
    io.mWEn := True
  }.elsewhen((io.op === B"1101") || (io.op === B"1110")){
    io.aluSel := B"110"
  }.elsewhen(io.op === B"1111"){
    io.aluSel := B"111"
  }
}
