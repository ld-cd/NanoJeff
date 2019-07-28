package nanoJeff

import spinal.core._
import spinal.lib._

class NanoImmDecode extends Component{
  val io = new Bundle{
    val instr = in Bits(8 bits)
    val op = in Bits(4 bits)
    val imm = out UInt(8 bits)
    val isImm = out Bool()
  }

  io.imm := U"x00"
  io.isImm := False

  when(io.op(3 downto 1) === B"111"){
    io.imm := B(8 bits, (3 downto 0) -> io.instr(3 downto 0), default -> False).asUInt
    io.isImm := True
  }.elsewhen(io.op === B"1010"){
    io.imm := B(8 bits, (3 downto 0) -> io.instr(3 downto 0), default -> False).asUInt
    io.isImm := True
  }.elsewhen(io.op === B"1011"){
    io.imm := B(8 bits, (7 downto 4) -> io.instr(3 downto 0), default -> False).asUInt
    io.isImm := True
  }.elsewhen(io.op === B"1101"){
    io.imm := B(8 bits, (1 downto 0) -> io.instr(1 downto 0), default -> False).asUInt
    io.isImm := True
  }
}
