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
        return switch (i) {
            case 10 -> "OP";
            case 11 -> "RELOP";
            case 12 -> "TYPEOF";
            case 13 -> "ASSIGN";
            case 14 -> "LPAREN";
            case 15 -> "RPAREN";
            case 16 -> "SEMI";
            case 17 -> "COMMA";
            case 18 -> "FUNCRET";
            case 19 -> "NUM";
            case 20 -> "ID";
            case 21 -> "BEGIN";
            case 22 -> "END";
            case 23 -> "INT";
            case 24 -> "PRINT";
            case 25 -> "VAR";
            case 26 -> "FUNC";
            case 27 -> "IF";
            case 28 -> "THEN";
            case 29 -> "ELSE";
            case 30 -> "WHILE";
            case 31 -> "VOID";
            default -> "error";
        };
    }

    public int yyparse() throws Exception
    {
        while ( true )
        {
            int token = lexer.yylex();  // get next token-name
//            System.out.println(token);
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
