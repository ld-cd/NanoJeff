module test;

  reg [7:0] mem [255:0];

  wire [7:0] iaddr;
  wire [7:0] daddr;
  wire [7:0] wdata;
  wire [7:0] data;
  wire [7:0] inst;

  reg clk = 0;
  reg reset = 1;
  wire wen;

  wire led;

  always #5 clk = ~clk;
  always #5 reset = 0;

  NanoJeff n (iaddr, daddr, wdata, inst, data, wen, clk, reset);

  assign data = mem[daddr];
  assign inst = mem[iaddr];

  always @(posedge clk) begin
    if (wen == 1) begin
      mem[daddr] = wdata;
    end
  end

  initial begin
    $dumpfile("NanoJeff.vcd");
    $dumpvars;
    $readmemb("tests/increment_mem.b", mem);
  end
endmodule
