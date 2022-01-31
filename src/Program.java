public class Program {
	public static void main(String[] args) throws Exception
    {
        //  java.io.Reader r = new java.io.StringReader
        //  ("func main :: () -> int\n"
        //  +"begin\n"
        //  +"    var x :: int;"
        //  +"    x <- 1;\n"
        //  +"    x <- x + 1.23;\n"
        //  +"    print x;\n"
        //  +"end\n"
        //  );
        //
        //  args = new String[] { "proj1-minic-tokenizer\\src\\test1.minc" };

        if(args.length <= 0)
            return;
        java.io.Reader r = new java.io.FileReader(args[0]);

        Compiler compiler = new Compiler(r);
        compiler.Compile();
	}
}
