NanoJeff.v: *.scala
	sbt run

test: NanoJeff.v test.v
	iverilog NanoJeff.v test.v && vvp a.out

clean:
	rm -f NanoJeff.v a.out *.log NanoJeff.vcd
