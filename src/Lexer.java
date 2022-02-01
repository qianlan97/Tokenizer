import javax.swing.plaf.TreeUI;
import java.util.Arrays;

public class Lexer
{
    private static final char EOF =  0;

    private Parser         yyparser; // parent parser object
    private java.io.Reader reader;   // input stream
    public int             lineno;   // line number
    public int             column;   // column
    public int             realColumn;
    private char[] buffer;
    private boolean newStart = true;

    public Lexer(java.io.Reader reader, Parser yyparser) throws Exception
    {
        this.reader   = reader;
        this.yyparser = yyparser;
        lineno = 1;
        column = 0;
        realColumn = 0;
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
        //remove the element in the buffer at index i
        char[] temp = new char[buffer.length-1];
        if (i >= 0) {
            System.arraycopy(buffer, 0, temp, 0, i);
        }
        if (temp.length - i >= 0) {
            System.arraycopy(buffer, i + 1, temp, i, temp.length - i);
        }
        return temp;
    }


    // * If yylex reach to the end of file, return  0
    // * If there is an lexical error found, return -1
    // * If a proper lexeme is determined, return token <token-id, token-attribute> as follows:
    //   1. set token-attribute into yyparser.yylval
    //   2. return token-id defined in Parser
    //   token attribute can be lexeme, line number, colume, etc.
    public int yylex() throws Exception
    {
        column = realColumn;
        char[] tempAttri = new char[100];
        int state = 0;
        int index = 0;
        char c;
        if (newStart) {
            buffer = readAllInput();
            newStart = false;
        }


        while(true)
        {
            c = buffer[index];
            //put these here cuz it does not affect the loop, we just pop it
            //if there's space or newline needed for check in pairs, we call specified functions
            if (c == '\n') {
                lineno += 1;
                column = 0;
                realColumn = 0;
                buffer = popChar(buffer,index);
                continue;
            }
//            else if (c == '\r') {
//                lineno += 1;
//                column = 0;
//                realColumn = 0;
//                if (buffer[index+1] == '\n') {
//                    buffer = popChar(buffer,index);
//                    buffer = popChar(buffer,index);
//                } else {
//                    buffer = popChar(buffer,index);
//                }
//            }
            if (Character.isWhitespace(c)) {
                column += 1;
                realColumn +=1;
                buffer = popChar(buffer,index);
                continue;
            }
            switch(state)
            {
                //After each iteration
                //1. update line & column number
                //2. update the buffer (some cases index too)
                //3. update state
                case 0:
                    if(c == EOF) { state=999; continue; }
                    if(c == '+' || c == '*' || c == '/') {
                        realColumn = column +1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        //index = 0;
                        yyparser.yylval = new ParserVal((Object)c);   // set token-attribute to yyparser.yylval
                        return Parser.OP;                             // return token-name
                    }
                    if(c == '=') {
                        realColumn = column +1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        //index = 0;
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.RELOP;
                    }
                    if(c == '(') {
                        realColumn = column +1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        //index = 0;
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.LPAREN;
                    }
                    if(c == ')') {
                        realColumn = column +1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        //index = 0;
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.RPAREN;
                    }
                    if(c == ';') {
                        realColumn = column +1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        //index = 0;
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.SEMI;
                    }
                    if(c == ',') {
                        realColumn = column +1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        //index = 0;
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.COMMA;
                    }
                    if(c == '-') {
                        realColumn = column +1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 1;
                        if (buffer[index] == '\n') {
                            System.out.println("new line here");
                            lineno -=1;
                        }
                        continue;
                    }
                    if (c == '<') {
                        realColumn = column +1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 2;
                        if (buffer[index] == '\n') {
                            System.out.println("new line here");
                            lineno -=1;
                        }
                        continue;
                    }
                    if (c == '>') {
                        realColumn = column +1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 3;
                        if (buffer[index] == '\n') {
                            System.out.println("new line here");
                            lineno -=1;
                        }
                        continue;
                    }
                    if (c == '!') {
                        realColumn = column +1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 4;
                        if (buffer[index] == '\n') {
                            lineno -=1;
                        }
                        continue;
                    }
                    if (c == ':') {
                        realColumn = column +1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 5;
                        if (buffer[index] == '\n') {
                            lineno -=1;
                        }
                        continue;
                    }
                case 1:
                    if (c == '>') {
                        column = realColumn;
                        realColumn +=1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        String arrtistr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object)arrtistr);
                        return Parser.FUNCRET;
                    } else {
                        c = buffer[index-1];
                        buffer = popChar(buffer,0);
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.OP;
                    }
                case 2:
                    if (c == '=') {
                        column = realColumn;
                        realColumn += 1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        String arrtistr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object) arrtistr);
                        return Parser.RELOP;
                    } else if (c == '-') {
                        column = realColumn;
                        realColumn += 1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        String arrtistr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object) arrtistr);
                        return Parser.ASSIGN;
                    } else {
                        c = buffer[index-1];
                        buffer = popChar(buffer,0);
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.RELOP;
                    }
                case 3:
                    if (c == '=') {
                        column = realColumn;
                        realColumn +=1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        String arrtistr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object)arrtistr);
                    } else {
                        c = buffer[index-1];
                        buffer = popChar(buffer,0);
                        yyparser.yylval = new ParserVal((Object)c);
                    }
                    return Parser.RELOP;
                case 4:
                    if (c == '=') {
                        column = realColumn;
                        realColumn +=1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        String arrtistr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object)arrtistr);
                        return Parser.RELOP;
                    }
                case 5:
                    if (c == ':') {
                        column = realColumn;
                        realColumn +=1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        String arrtistr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object)arrtistr);
                        return Parser.TYPEOF;
                    }
//                case 7:
//                    if (c == '=') {
//                        column = realColumn;
//                        realColumn +=1;
//                        buffer = popChar(buffer,0);
//                        buffer = popChar(buffer,0);
//                        tempAttri[index] = c;
//                        String arrtistr = new String(tempAttri);
//                        yyparser.yylval = new ParserVal((Object)arrtistr);
//                        return Parser.RELOP;
//                    } else if (c == '-') {
//
//                    }
//                    if (c == '>') {
//                        column = realColumn;
//                        realColumn +=1;
//                        buffer = popChar(buffer, 0);
//                        buffer = popChar(buffer, 0);
//                        tempAttri[index] = c;
//                        String arrtistr = new String(tempAttri);
//                        yyparser.yylval = new ParserVal((Object)arrtistr);
//                        return Parser.FUNCRET;
//                    } else {
//                        state = 1;
//                        index -= 1;
//                        realColumn -=1;
//                        continue;
//                    }
                case 999:
                    return EOF;                                     // return end-of-file symbol
                case 1000:
                    return -1;                                      // lexer error found
            }
        }
    }
}
