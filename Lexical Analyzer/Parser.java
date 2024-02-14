//----------------------------------------------
// Names:      Quinn Trate & Vy Dinh
// Date:       February 5, 2024
// Class:      CMPSC 470 Section 1: Compilers
// Instructor: Dr. Hyuntae Na
// Purpose:    Parser Class that gets Lexemes
//             for the Lexical Analyzer. 
//             Gives Success or Error
//----------------------------------------------

import java.util.*;

public class Parser
{
    public static final int OP         = 10;    // +  -  *  /
    public static final int RELOP      = 11;    // <  >  <=  >=  ...
    public static final int LPAREN     = 12;    // (
    public static final int RPAREN     = 13;    // )
    public static final int SEMI       = 14;    // ;
    public static final int COMMA      = 15;    // ,
    public static final int INT        = 16;    // int
    public static final int NUM        = 17;    // number
    public static final int ID         = 18;    // identifier
    public static final int PRINT      = 19;    // print
    public static final int FUNC       = 20;    // function
    public static final int BEGIN      = 21;    // begin
    public static final int END        = 22;    // end
    public static final int VOID       = 23;    // void
    public static final int TYPEOF     = 24;    // type of
    public static final int VAR        = 25;    // var
    public static final int WHILE      = 26;    // while
    public static final int THEN       = 27;    // then
    public static final int IF         = 28;    // if
    public static final int ELSE       = 29;    // else
    public static final int ASSIGN     = 30;    // assign

    Compiler         compiler;
    Lexer            lexer;     // lexer.yylex() returns token-name
    public ParserVal yylval;    // yylval contains token-attribute

    // Hashmap for Tokens
    HashMap<Integer, Integer> symbols = new HashMap<Integer, Integer>();

    public Parser(java.io.Reader r, Compiler compiler) throws Exception
    {
        this.compiler = compiler;
        this.lexer    = new Lexer(r, this);
        
        // Fill Hashmap with Tokens
        symbols.put(10, OP);
        symbols.put(11, RELOP);
        symbols.put(12, LPAREN);
        symbols.put(13, RPAREN);
        symbols.put(14, SEMI);
        symbols.put(15, COMMA);
        symbols.put(16, INT);
        symbols.put(17, NUM);
        symbols.put(18, ID);
        symbols.put(19, PRINT);
        symbols.put(20, FUNC);
        symbols.put(21, BEGIN);
        symbols.put(22, END);
        symbols.put(23, VOID);
        symbols.put(24, TYPEOF);
        symbols.put(25, VAR);
        symbols.put(26, WHILE);
        symbols.put(27, THEN);
        symbols.put(28, IF);
        symbols.put(29, ELSE);
        symbols.put(30, ASSIGN);
    }

    // 1. parser call lexer.yylex that should return (token-name, token-attribute)
    // 2. lexer
    //    a. assign token-attribute to yyparser.yylval
    //       token attribute can be lexeme, line number, colume, etc.
    //    b. return token-id defined in Parser as a token-name
    // 3. parser print the token on console
    //    if there was an error (-1) in lexer, then print error message
    // 4. repeat until EOF (0) is reached
    public int yyparse() throws Exception
    {
        while ( true )
        {
            int token = lexer.yylex();  // get next token-name
            Object attr = yylval.obj;   // get      token-attribute
            String tokenname = "";
            
            // Switch Case for Tokena
            switch(token)
            {
                case 10:
                    tokenname = "OP";
                    break;
                case 11:
                    tokenname = "RELOP";
                    break;
                case 12:
                    tokenname = "LPAREN";
                    break;
                case 13:
                    tokenname = "RPAREN";
                    break;
                case 14:
                    tokenname = "SEMI";
                    break;
                case 15:
                    tokenname = "COMMA";
                    break;
                case 16:
                    tokenname = "INT";
                    break;
                case 17:
                    tokenname = "NUM";
                    break;
                case 18:
                    tokenname = "ID";
                    break;
                case 19:
                    tokenname = "PRINT";
                    break;
                case 20:
                    tokenname = "FUNC";
                    break;
                case 21:
                    tokenname = "BEGIN";
                    break;
                case 22:
                    tokenname = "END";
                    break;
                case 23:
                    tokenname = "VOID";
                    break;
                case 24:
                    tokenname = "TYPEOF";
                    break;
                case 25:
                    tokenname = "VAR";
                    break;
                case 26:
                    tokenname = "WHILE";
                    break;
                case 27:
                    tokenname = "THEN";
                    break;
                case 28:
                    tokenname = "IF";
                    break;
                case 29:
                    tokenname = "ELSE";
                    break;
                case 30:
                    tokenname = "ASSIGN";
                    break;
            }

            if(token == 0)
            {
                // EOF is Reached
                System.out.println("Success!");
                return 0;
            }
            if(token == -1)
            {
                // Lexical Error is Found
                System.out.println("Error! There is a lexical error at " + lexer.lineno + ":" + lexer.column + ".");
                return -1;
            }

            System.out.println("<" + tokenname + ", token-attr:\"" + attr + "\", " + lexer.lineno + ":" + lexer.column + ">");
        }
    }
}
