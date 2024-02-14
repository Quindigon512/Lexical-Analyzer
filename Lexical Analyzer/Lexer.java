//----------------------------------------------
// Names:      Quinn Trate & Vy Dinh
// Date:       February 5, 2024
// Class:      CMPSC 470 Section 1: Compilers
// Instructor: Dr. Hyuntae Na
// Purpose:    Lexer Class that Builds Lexemes
//             for the Lexical Analyzer. Uses    
//             DFA Implamentation.
//----------------------------------------------

import java.nio.Buffer;
import java.util.*;
import java.io.Reader;

public class Lexer
{
    private static final char EOF = 0;

    private Parser         yyparser; // parent parser object
    private Reader         reader;   // input stream
    public int             lineno;   // line number
    public int             column;   // column

    // Hashmap for Keywords
    HashMap<String, Integer> keywords = new HashMap<String, Integer>();

    // Lexical Analyzer Pointers
    int lexBegin = 0;
    int forwardPointer = -1;

    // String for Building the Lexeme
    String lexeme;
    
    // Points for Keeping Track of the Beginning of the Lexeme
    int lbColumn = 1;
    int forwardColumn = 0;
    
    // Arrays for the Double Buffer and Flag
    char[] buffer1;
    char[] buffer2;
    boolean bufferNum;
    

    public Lexer(java.io.Reader reader, Parser yyparser) throws Exception
    {
        this.reader = reader;
        this.yyparser = yyparser;
        lineno = 1;
        column = 1;
        
        // Fill Hashmap with Keywords
        keywords.put("int", Parser.INT);
        keywords.put("print", Parser.PRINT);
        keywords.put("var", Parser.VAR);
        keywords.put("func", Parser.FUNC);
        keywords.put("if", Parser.IF);
        keywords.put("then", Parser.THEN);
        keywords.put("else", Parser.ELSE);
        keywords.put("while", Parser.WHILE);
        keywords.put("void", Parser.VOID);
        keywords.put("begin", Parser.BEGIN);
        keywords.put("end", Parser.END);

        // Initialize Buffers
        buffer1 = new char[10];
        buffer2 = new char[10];

        for (int i = 0; i < 9; i++)
        {
            buffer1[i] = 0;
            buffer2[i] = 0;
        }

        buffer1[9] = EOF;
        buffer2[9] = EOF;

        reader.read(buffer1, 0, 9);

        bufferNum = true;
    }

    public char NextChar() throws Exception
    {
        //http://tutorials.jenkov.com/java-io/readers-writers.html
        //int data = reader.read();

        //if(data == -1)
        //{
        //    return EOF;
        //}
        //return (char)data;
        
        if (bufferNum)
        {
            forwardPointer++;
            // Fill Buffer2 and Swap to it
            if (buffer1[forwardPointer] == EOF && forwardPointer == 9)
            {
                forwardPointer = 0;

                for (int i = 0; i < buffer2.length; i++)
                    buffer2[i] = EOF;

                reader.read(buffer2, 0, 9);
                bufferNum = false;

                forwardColumn++;
                return buffer2[forwardPointer];
            }
            // End of File
            else if (buffer1[forwardPointer] == EOF && forwardPointer != 9)
            {
                return EOF;
            }
            // Middle of Buffer 1
            else
            {
                forwardColumn++;
                return buffer1[forwardPointer];
            }
        }
        else
        {
            forwardPointer++;
            // Fill Buffer1 and Swap to it
            if (buffer2[forwardPointer] == EOF && forwardPointer == 9)
            {
                forwardPointer = 0;

                for (int i = 0; i < buffer1.length; i++) {
                    buffer1[i] = EOF;
                }

                reader.read(buffer1, 0, 9);
                bufferNum = true;

                forwardColumn++;
                return buffer1[forwardPointer];
            }
            // End of File
            else if (buffer2[forwardPointer] == EOF && forwardPointer != 9)
            {
                return EOF;
            }
            // Middle of Buffer 2
            else
            {
                forwardColumn++;
                return buffer2[forwardPointer];
            }
        }
    }

    // Function for Fail Cases
    public int Fail() { return -1; }

    // * If yylex reach to the end of file, return  0
    // * If there is an lexical error found, return -1
    // * If a proper lexeme is determined, return token <token-id, token-attribute> as follows:
    //   1. set token-attribute into yyparser.yylval
    //   2. return token-id defined in Parser
    //   token attribute can be lexeme, line number, colume, etc.
    public int yylex() throws Exception
    {
        // Initializers
        int state = 0;
        lexeme = "";
        char c = 0;
        
        while(true)
        {
            c = NextChar();

            // Switch Cases for DFA
            switch(state)
            {
                case 0:
                    if(c == ';') { state = 1; continue; }
                    if(c == '+') { state = 2; continue; }
                    if(c == '-') { state = 3; continue; }
                    if(c == '*') { state = 4; continue; }
                    if(c == '/') { state = 5; continue; }
                    if(c == '(') { state = 6; continue; }
                    if(c == ')') { state = 7; continue; }
                    if(c == ',') { state = 8; continue; }
                    if(c == '=') { state = 9; continue; }
                    if(c == ':') { state = 10; continue; }
                    if(c == '>') { state = 13; continue; }
                    if(c == '<') { state = 15; continue; }
                    // Tests for Letters
                    if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122))
                    {
                        lexeme += c;
                        state = 18;
                        continue;
                    }
                    // Tests for Numbers
                    if (c >= 48 && c <= 57)
                    {
                        lexeme += c;
                        state = 19;
                        continue;
                    }
                    if ((c == ' ') || (c == '\t') || (c == '\r'))
                    {
                        lexBegin = forwardPointer + 1;
                        lbColumn = forwardColumn + 1;
                        continue;
                    }
                    if (c == '\n')
                    { 
                        lineno++;
                        lexBegin++;
                        column = 1;
                        forwardColumn = 0;
                        lbColumn = 1;
                        continue;
                    }
                    if(c == EOF) { state = 9999; continue; }

                    column = lbColumn;
                    state = 22;
                    continue;
                case 1:
                    // Case for Semicolon
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)";");   // set token-attribute to yyparser.yylval
                    return Parser.SEMI;                             // return token-name
                case 2:
                    // Case for Plus Sign
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"+"); 
                    return Parser.OP;
                case 3:
                    // Case for Minus Sign
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"-");
                    return Parser.OP; 
                case 4:
                    // Case for Multiplication Sign
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"*"); 
                    return Parser.OP; 
                case 5:
                    // Case for Division Sign
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"/"); 
                    return Parser.OP; 
                case 6:
                    // Case for Left Parenthesis
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"(");   
                    return Parser.LPAREN;  
                case 7:
                    // Case for Right Parenthesis
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)")");   
                    return Parser.RPAREN;  
                case 8:
                    // Case for Comma
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)",");   
                    return Parser.COMMA;
                case 9:
                    // Case for Double Equals
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"=");   
                    return Parser.RELOP;
                case 10:
                    // Case for Equals
                    if(c == '=')
                    {
                        state = 11;
                        continue;
                    }
                    // Case for Type of
                    else if (c == ':')
                    {
                        state = 12;
                        continue;
                    }
                    // Go to Fail State since one Colon is a Lexical Error
                    else
                    {
                        state = 22;
                        continue;
                    }
                case 11:
                    // Case for Equals 
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)":="); 
                    return Parser.ASSIGN;
                case 12:
                    // Case for Type of
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"::");   
                    return Parser.TYPEOF;
                case 13:
                    // Case for Greater than or Equals 
                    if(c == '=')
                    {  
                        state = 14;
                        continue; 
                    }
                    // Case for Greater than
                    else
                    {
                        forwardPointer--;
                        forwardColumn--;
                        lexBegin = forwardPointer + 1;
                        column = lbColumn;
                        lbColumn = forwardColumn + 1;
                        yyparser.yylval = new ParserVal((Object)">");   
                        return Parser.RELOP;
                    }
                case 14:
                    // Case for Greater than or Equals
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)">=");   
                    return Parser.RELOP;
                case 15:
                    // Case for not Equals
                    if(c == '>')
                    {
                        state = 16;
                        continue;
                    }
                    // Case for Less than or Equals
                    else if(c == '=')
                    {
                        state = 17;
                        continue;
                    }
                    // Case for Less
                    else
                    {
                        forwardPointer--;
                        forwardColumn--;
                        lexBegin = forwardPointer + 1;
                        column = lbColumn;
                        lbColumn = forwardColumn + 1;
                        yyparser.yylval = new ParserVal((Object)"<");
                        return Parser.RELOP;
                    }
                case 16:
                    // Case for not Equal
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"<>");   
                    return Parser.RELOP;
                case 17:
                    // Case for Less than or Equals 
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object)"<=");   
                    return Parser.RELOP;
                case 18:
                    // Case for Identifiers
                    if((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c >= 48 && c <= 57) || (c == '_'))
                    {
                        // Build Lexeme
                        lexeme += c;
                        continue;
                    }
                    else
                    {
                        forwardPointer--;
                        forwardColumn--;
                        lexBegin = forwardPointer + 1;
                        column = lbColumn;
                    }

                    // Case for Keywords
                    if(keywords.containsKey(lexeme))
                    {
                        lbColumn = forwardColumn + 1;
                        yyparser.yylval = new ParserVal((Object) lexeme);
                        return keywords.get(lexeme);
                    }
                    // Case for other Identifiers
                    else
                    {
                        lbColumn = forwardColumn + 1;
                        yyparser.yylval = new ParserVal((Object) lexeme);
                        return Parser.ID;
                    }
                case 19:
                    // Case for Numbers
                    if(c >= 48 && c <= 57)
                    {
                        // Build Lexeme
                        lexeme += c;
                        continue;
                    }
                    // Case for Decimal
                    if(c == '.')
                    {
                        lexeme += c;
                        state = 20;
                        continue;
                    }
                    
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object) lexeme);
                    return Parser.NUM;
                case 20:
                    // Case for Decimal
                    if(c >= 48 && c <= 57)
                    {
                        // Build Lexeme
                        lexeme += c;
                        state = 21;
                        continue;
                    }
                    // Go to Fail State if Staring with Decimal
                    column = lbColumn;
                    state = 22;
                    continue;
                case 21:
                    // Case for Numbers
                    if(c >= 48 && c <= 57)
                    {
                        lexeme += c;
                        continue;
                    }
                    
                    forwardPointer--;
                    forwardColumn--;
                    lexBegin = forwardPointer + 1;
                    column = lbColumn;
                    lbColumn = forwardColumn + 1;
                    yyparser.yylval = new ParserVal((Object) lexeme);
                    return Parser.NUM;
                case 22:
                    // Fail State
                    yyparser.yylval = new ParserVal(new Object()); 
                    return Fail();
                case 9999:
                    // End of File
                    yyparser.yylval = new ParserVal(new Object()); 
                    return EOF; // return end-of-file symbol
            }
        }
    }
}