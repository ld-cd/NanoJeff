NanoJeff.v: ./src/main/scala/*.scala
	sbt run

tests/increment_mem.b: tests/increment_mem.s
	python3 ./asm.py -f BINARY tests/increment_mem.s tests/increment_mem.b

test: NanoJeff.v test.v tests/increment_mem.b
	iverilog NanoJeff.v test.v && vvp a.out

clean:
	rm -f NanoJeff.v a.out *.log NanoJeff.vcd tests/*.b
