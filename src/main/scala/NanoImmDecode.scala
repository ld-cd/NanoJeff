package nanoJeff

import spinal.core._
import spinal.lib._
import NanoISA._

class NanoImmDecode extends Component{
  val io = new Bundle{
    val instr = in Bits(8 bits)
    val op = in(NanoOp())
    val imm = out UInt(8 bits)
    val isImm = out Bool()
  }

  io.imm := U"x00"
  io.isImm := False

  when(io.op === NanoOp.JI) {
    io.imm := B(8 bits, (3 downto 0) -> io.instr(3 downto 0), default -> False).asUInt
    io.isImm := True
  }.elsewhen(io.op === NanoOp.JIR) {
    io.imm := 1 + (~B(8 bits, (3 downto 0) -> io.instr(3 downto 0), default -> False)).asUInt
    io.isImm := True
  }.elsewhen(isImmLoad(io.op)){
    io.imm := B(8 bits, (3 downto 0) -> io.instr(3 downto 0), default -> False).asUInt
    io.isImm := True
  }.elsewhen(io.op === NanoOp.BZ){
    io.imm := B(8 bits, (1 downto 0) -> io.instr(1 downto 0), default -> False).asUInt
    io.isImm := True
  }
}
