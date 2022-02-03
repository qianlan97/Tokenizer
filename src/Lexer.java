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
    boolean addLine = false;

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
                char temp = NextChar();
                if (temp != '\r') {
                    tempBuffer[index] = temp;
                } else {
                    continue;
                }
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

    public char[] cleanArray(char[] buffer) {
        int size = 0;
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != '\u0000') {
                size +=1;
            } else {
                break;
            }
        }
        char[] temp = new char[size];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = buffer[i];
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
        char[] tempAttri = new char[100];
        int state = 0;
        int index = 0;
        char c;

        column = realColumn;
        if (newStart) {
            buffer = readAllInput();
            newStart = false;
        }
        if (addLine) {
            column = 0;
            realColumn = 0;
            addLine = false;
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
            if (c == ' ' || c == '\t') {
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
                        realColumn +=1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        yyparser.yylval = new ParserVal((Object)c);   // set token-attribute to yyparser.yylval
                        return Parser.OP;                             // return token-name
                    }
                    if(c == '=') {
                        realColumn +=1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.RELOP;
                    }
                    if(c == '(') {
                        realColumn +=1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.LPAREN;
                    }
                    if(c == ')') {
                        realColumn +=1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.RPAREN;
                    }
                    if(c == ';') {
                        realColumn +=1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.SEMI;
                    }
                    if(c == ',') {
                        realColumn +=1;
                        column = realColumn;
                        buffer = popChar(buffer, 0);
                        yyparser.yylval = new ParserVal((Object)c);
                        return Parser.COMMA;
                    }
                    if(c == '-') {
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 1;
                        if (buffer[index] != '>') {
                            column = realColumn;
                            buffer = popChar(buffer,0);
                            yyparser.yylval = new ParserVal((Object)c);
                            return Parser.OP;
                        }
                        continue;
                    }
                    if (c == '<') {
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 2;
                        if (buffer[index] != '=' && buffer[index] != '-') {
                            column = realColumn;
                            buffer = popChar(buffer,0);
                            yyparser.yylval = new ParserVal((Object)c);
                            return Parser.RELOP;
                        }
                        continue;
                    }
                    if (c == '>') {
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 3;
                        if (buffer[index] != '=') {
                            column = realColumn;
                            buffer = popChar(buffer,0);
                            yyparser.yylval = new ParserVal((Object)c);
                            return Parser.RELOP;
                        }
                        continue;
                    }
                    if (c == '!') {
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 4;
                        if (buffer[index] == '\n') {
                            lineno -=1;
                        }
                        continue;
                    }
                    if (c == ':') {
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 5;
                        if (buffer[index] == '\n') {
                            lineno -=1;
                        }
                        continue;
                    }
                    if (Character.isDigit(c)) {
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 6;
                        if (buffer[index] == ' ' || buffer[index] == '\t' || (!Character.isDigit(buffer[index]) && buffer[index] != '.') || buffer[index] == '\n') {
                            if (buffer[index] == '\n') {
                                addLine = true;
                            }
                            column = realColumn;
                            buffer = popChar(buffer, 0);
                            yyparser.yylval = new ParserVal((Object)c);
                            return Parser.NUM;
                        }
                        continue;
                    }
                    if (Character.isLetter(c)) {
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 8;
                        if (buffer[index] == ' ' || buffer[index] == '\t' || (!Character.isLetter(buffer[index]) && buffer[index] != '_') || buffer[index] == '\n') {
                            if (buffer[index] == '\n') {
                                addLine = true;
                            }
                            column = realColumn;
                            buffer = popChar(buffer, 0);
                            yyparser.yylval = new ParserVal((Object)c);
                            return Parser.ID;
                        }
                        continue;
                    }
                    if (c == '.' || c == '_') {
                        column +=1;
                        state = 1000;
                        continue;
                    }
                case 1:
                    if (c == '>') {
                        column = realColumn;
                        realColumn +=1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        tempAttri = cleanArray(tempAttri);
                        String attriStr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object)attriStr);
                        return Parser.FUNCRET;
                    }
                case 2:
                    if (c == '=') {
                        column = realColumn;
                        realColumn += 1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        tempAttri = cleanArray(tempAttri);
                        String attriStr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object) attriStr);
                        return Parser.RELOP;
                    } else if (c == '-') {
                        column = realColumn;
                        realColumn += 1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        tempAttri = cleanArray(tempAttri);
                        String attriStr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object) attriStr);
                        return Parser.ASSIGN;
                    } else {
                        column +=1;
                        return Fail();
                    }
                case 3:
                    if (c == '=') {
                        column = realColumn;
                        realColumn +=1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        tempAttri = cleanArray(tempAttri);
                        String attriStr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object)attriStr);
                        return Parser.RELOP;
                    }
                case 4:
                    if (c == '=') {
                        column = realColumn;
                        realColumn +=1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        tempAttri = cleanArray(tempAttri);
                        String attriStr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object)attriStr);
                        return Parser.RELOP;
                    }
                case 5:
                    if (c == ':') {
                        column = realColumn;
                        realColumn +=1;
                        buffer = popChar(buffer, 0);
                        buffer = popChar(buffer, 0);
                        tempAttri[index] = c;
                        tempAttri = cleanArray(tempAttri);
                        String attriStr = new String(tempAttri);
                        yyparser.yylval = new ParserVal((Object)attriStr);
                        return Parser.TYPEOF;
                    }
                case 6:
                    if (Character.isDigit(c)){
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 6;
                        if (buffer[index] == ' ' || buffer[index] == '\t' || (!Character.isDigit(buffer[index]) && buffer[index] != '.') || buffer[index] == '\n') {
                            if (buffer[index] == '\n') {
                                addLine = true;
                            }
                            for (int k = 0; k < index; k++) {
                                buffer = popChar(buffer,0);
                            }
                            tempAttri = cleanArray(tempAttri);
                            String attriStr = new String(tempAttri);
                            double arrtiNum = Double.parseDouble(attriStr);
                            column +=1;
                            yyparser.yylval = new ParserVal((Object)arrtiNum);
                            return Parser.NUM;
                        }
                        continue;
                    } else if (c == '.') {
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        state = 7;
                        if (buffer[index] == ' ' || buffer[index] == '\t' || !Character.isDigit(buffer[index]) || buffer[index] == '\n' || buffer[index] == '.') {
                            if (buffer[index] == '\n') {
                                addLine = true;
                            }
                            for (int k = 0; k < index; k++) {
                                buffer = popChar(buffer,0);
                            }
                            tempAttri = cleanArray(tempAttri);
                            String attriStr = new String(tempAttri);
                            double arrtiNum = Double.parseDouble(attriStr);
                            column +=1;
                            yyparser.yylval = new ParserVal((Object)arrtiNum);
                            return Parser.NUM;
                        }
                        continue;
                    } else {
                        column +=1;
                        return Fail();
                    }
                case 7:
                    if (Character.isDigit(c)){
                        realColumn +=1;
                        tempAttri[index] = c;
                        index += 1;
                        if (buffer[index] == ' ' || buffer[index] == '\t' || !Character.isDigit(buffer[index]) || buffer[index] == '\n' || buffer[index] == '.') {
                            if (buffer[index] == '\n') {
                                addLine = true;
                            }
                            for (int k = 0; k < index; k++) {
                                buffer = popChar(buffer,0);
                            }
                            tempAttri = cleanArray(tempAttri);
                            String attriStr = new String(tempAttri);
                            double arrtiInt = Double.parseDouble(attriStr);
                            column +=1;
                            yyparser.yylval = new ParserVal((Object)arrtiInt);
                            return Parser.NUM;
                        }
                        continue;
                    } else {
                        column +=1;
                        return Fail();
                    }
                case 8:
                    if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
                        realColumn += 1;
                        tempAttri[index] = c;
                        index += 1;
                        if (buffer[index] == ' ' || buffer[index] == '\t' || (!Character.isLetter(buffer[index]) && buffer[index] != '_' && !Character.isDigit(buffer[index])) || buffer[index] == '\n') {
                            if (buffer[index] == '\n') {
                                addLine = true;
                            }
                            for (int k = 0; k < index; k++) {
                                buffer = popChar(buffer, 0);
                            }
                            tempAttri = cleanArray(tempAttri);
                            String attriStr = new String(tempAttri);
                            column += 1;
                            switch (attriStr) {
                                case "begin":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.BEGIN;
                                case "end":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.END;
                                case "int":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.INT;
                                case "print":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.PRINT;
                                case "var":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.VAR;
                                case "func":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.FUNC;
                                case "if":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.IF;
                                case "then":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.THEN;
                                case "else":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.ELSE;
                                case "while":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.WHILE;
                                case "void":
                                    yyparser.yylval = new ParserVal((Object) attriStr);
                                    return Parser.VOID;
                            }
                            yyparser.yylval = new ParserVal((Object) attriStr);
                            return Parser.ID;
                        }
                        continue;
                    }
                case 999:
                    return EOF;                                     // return end-of-file symbol
                case 1000:
                    return -1;                                      // lexer error found
            }
        }
    }
}
