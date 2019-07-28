package nanoJeff

import spinal.core._
import spinal.lib._

class NanoReg extends Component{
  val io = new Bundle{
    val r1Sel, r2Sel = in Bits(2 bits)
    val wEn = in Bool
    val wData = in UInt(8 bits)
    val r1, r2 = out UInt(8 bits)
  }

  val regFile = Vec(RegInit(U"00000000"), 4)

  io.r1 := regFile(io.r1Sel.asUInt)
  io.r2 := regFile(io.r2Sel.asUInt)

  when(io.wEn){
    regFile(io.r1Sel.asUInt) := io.wData
  }
}
