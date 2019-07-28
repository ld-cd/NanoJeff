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
    $dumpfile ("NanoJeff.vcd");
    $dumpvars;
    mem[0] = 8'b01010000;
    mem[1] = 8'b01010101;
    mem[2] = 8'b01011010;
    mem[3] = 8'b01011111;
    mem[4] = 8'b10101111;
    mem[5] = 8'b10111111;
    mem[6] = 8'b01101100;
    mem[7] = 8'b01010000;
    mem[8] = 8'b10011100;
    mem[9] = 8'b10100001;
    mem[10] = 8'b01100100;
    mem[11] = 8'b10011100;
    mem[12] = 8'b01100001;
    mem[13] = 8'b11110001;
  end
endmodule
