public class Parser
{
    public static final int OP         = 10;    // "+", "-", "*", "/"
    public static final int RELOP      = 11;    // "<", ">", "=", "!=", "<=", ">="
    public static final int TYPEOF     = 12;    // "::"
    public static final int ASSIGN     = 13;    // "<-"
    public static final int LPAREN     = 14;    // "("
    public static final int RPAREN     = 15;    // ")"
    public static final int SEMI       = 16;    // ";"
    public static final int COMMA      = 17;    // ","
    public static final int FUNCRET    = 18;    // "->"
    public static final int NUM        = 19;    // number
    public static final int ID         = 20;    // identifier
    public static final int BEGIN      = 21;    // "begin"
    public static final int END        = 22;    // "end"
    public static final int INT        = 23;    // "int"
    public static final int PRINT      = 24;    // "print"
    public static final int VAR        = 25;    // "var"
    public static final int FUNC       = 26;    // "func"
    public static final int IF         = 27;    // "if"
    public static final int THEN       = 28;    // "then"
    public static final int ELSE       = 29;    // "else"
    public static final int WHILE      = 30;    // "while"
    public static final int VOID       = 31;    // "void"

    Compiler         compiler;
    Lexer            lexer;     // lexer.yylex() returns token-name
    public ParserVal yylval;    // yylval contains token-attribute

    public Parser(java.io.Reader r, Compiler compiler) throws Exception
    {
        this.compiler = compiler;
        this.lexer    = new Lexer(r, this);
    }

    public String type(int i) {
//        if (i == 10) {
//            return "OP";
         switch (i) {
             case 10: return "OP";
             case 11: return "RELOP";
             case 12: return "TYPEOF";
             case 13: return "ASSIGN";
             case 14: return "LPAREN";
             case 15: return "RPAREN";
             case 16: return "SEMI";
             case 17: return "COMMA";
             case 18: return "FUNCRET";
             case 19: return "NUM";
             case 20: return "ID";
             case 21: return "BEGIN";
             case 22: return "END";
             case 23: return "INT";
             case 24: return "PRINT";
             case 25: return "VAR";
             case 26: return "FUNC";
             case 27: return "IF";
             case 28: return "THEN";
             case 29: return "ELSE";
             case 30: return "WHILE";
             case 31: return "VOID";
             default: return "error";
        }
    }

    public int yyparse() throws Exception
    {
        while ( true )
        {
            int token = lexer.yylex();  // get next token-name
//            System.out.println("token names is : " + token);
            Object attr = yylval.obj;   // get      token-attribute
            String tokenname = type(token);

            if(token == 0)
            {
                // EOF is reached
                System.out.println("Success!");
                return 0;
            }
            if(token == -1)
            {
                // lexical error is found
                System.out.println("Error! There is a lexical error at line " + lexer.lineno + " and column " + lexer.column + ".");
                return -1;
            }

            System.out.println("<" + tokenname + ", token-attr:\"" + attr + "\", lineno:" + lexer.lineno + ", column:" + lexer.column + ">");
        }
    }
}
