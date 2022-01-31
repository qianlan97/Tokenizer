import javax.swing.plaf.TreeUI;
import java.util.Arrays;

public class Lexer
{
    private static final char EOF        =  0;

    private Parser         yyparser; // parent parser object
    private java.io.Reader reader;   // input stream
    public int             lineno;   // line number
    public int             column;   // column
    private char[] buffer;
    private boolean newStart = true;

    public Lexer(java.io.Reader reader, Parser yyparser) throws Exception
    {
        this.reader   = reader;
        this.yyparser = yyparser;
        lineno = 1;
        column = 0;
    }

    public char NextChar() throws Exception
    {
        // http://tutorials.jenkov.com/java-io/readers-writers.html
        int data = reader.read();
        if(data == -1)
        {
            return EOF;
        }
        return (char)data;
    }

    public int Fail()
    {
        return -1;
    }

    public char[] readAllInput() {
        int index = 0;
        char[] tempBuffer = new char[4096];
        while (true) {
            try {
                tempBuffer[index] = NextChar();
                if (tempBuffer[index] != EOF) {
                    index++;
                } else {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tempBuffer;
    }

    public char[] popChar(char[] buffer, int i) {
        char[] temp = Arrays.copyOfRange(buffer, i, buffer.length);
        return temp;
    }

//    public int countSize(char[] buffer) {
//        int count = 0;
//        for (int i = 0; i < 40960; i++) {
//            if (buffer[i] != 0) {
//                count++;
//            } else {
//                break;
//            }
//        }
//        return count;
//    }

    // * If yylex reach to the end of file, return  0
    // * If there is an lexical error found, return -1
    // * If a proper lexeme is determined, return token <token-id, token-attribute> as follows:
    //   1. set token-attribute into yyparser.yylval
    //   2. return token-id defined in Parser
    //   token attribute can be lexeme, line number, colume, etc.
    public int yylex() throws Exception
    {
        int state = 0;
        int index = 0;
        char c;
        if (newStart) {
            buffer = readAllInput();
            newStart = false;
        }


        while(true)
        {
            //After each iteration
            //1. update line & column number
            //2. update the buffer (some cases index too)
            c = buffer[index];
            System.out.println("char is:" + c);
            //put these here cuz it does not affect the loop, we just pop it
            //if there're space or return needed for check in pairs, we call specified functions
            if (c == '\n') {
                lineno += 1;
                column = 0;
                buffer = popChar(buffer,1);
                continue;
            }
            if (Character.isWhitespace(c)) {
                column += 1;
                buffer = popChar(buffer,1);
                continue;
            }

            switch(state)
            {
                case 0:
                    if(c == '+' || c == '-' || c == '*' || c == '/') {
                        column += 1;
                        buffer = popChar(buffer, 1);
                        //index = 0;
//                        state = 100;
//                        continue;
                        yyparser.yylval = new ParserVal((Object)c);   // set token-attribute to yyparser.yylval
                        return Parser.OP;                             // return token-name
                    }
                    if(c == EOF) { state=999; continue; }
                    // return Fail();
//                case 100:

                case 999:
                    return EOF;                                     // return end-of-file symbol
                case 1000:
                    return -1;                                      // lexer error found
            }
        }
    }
}
