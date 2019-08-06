package nanoJeff

import spinal.core._
import spinal.lib._

class NanoALU extends Component {
  val io = new Bundle{
    val aluSel = in Bits(3 bits)
    val a, b = in UInt(8 bits)
    val result = out UInt(8 bits)
  }

  switch(io.aluSel){
    is(B"000"){
      io.result := io.a |<< (io.b.asBits(2 downto 0).asUInt)
    }
    is(B"001"){
      io.result := io.a |>> io.b.asBits(2 downto 0).asUInt
    }
    is(B"010"){
      io.result := ~io.b
    }
    is(B"011"){
      io.result := io.a & io.b
    }
    is(B"100"){
      io.result := io.a | io.b
    }
    is(B"101"){
      io.result := io.a ^ io.b
    }
    is(B"110"){
      io.result := io.a + io.b
    }
    is(B"111"){
      io.result := io.a < io.b
    }
  }
}
