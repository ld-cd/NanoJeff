package nanoJeff

import spinal.core._
import spinal.lib._
import NanoISA._

class NanoIDecode extends Component{

  val io = new Bundle{
    val instr = in Bits(8 bits)
    val r1Sel = out(NanoRegs())
    val r2Sel = out(NanoRegs())
    val op = out(NanoOp())
  }

  io.op.assignFromBits(io.instr(7 downto 4))
  io.r1Sel.assignFromBits(io.instr(3 downto 2))
  io.r2Sel.assignFromBits(io.instr(1 downto 0))

  when(isImmLoad(io.op)){
    io.r1Sel := NanoRegs.X0
  }
}
