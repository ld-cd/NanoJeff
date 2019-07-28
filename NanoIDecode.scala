package nanoJeff

import spinal.core._
import spinal.lib._

class NanoIDecode extends Component{
  val io = new Bundle{
    val instr = in Bits(8 bits)
    val r1Sel, r2Sel = out Bits(2 bits)
    val op = out Bits(4 bits)
  }

  io.op := io.instr(7 downto 4)
  io.r1Sel := io.instr(3 downto 2)
  io.r2Sel := io.instr(1 downto 0)

  when(io.op(3 downto 1) === B"101"){
    io.r1Sel := B"00"
  }
}
