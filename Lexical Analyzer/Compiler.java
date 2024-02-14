//----------------------------------------------
// Names:      Quinn Trate & Vy Dinh
// Date:       February 5, 2024
// Class:      CMPSC 470 Section 1: Compilers
// Instructor: Dr. Hyuntae Na
// Purpose:    Compilers Class that calls
//             Parser for the Lexical Analyzer.
//----------------------------------------------

public class Compiler
{
    Parser parser;

    public Compiler(java.io.Reader r) throws Exception
    {
        parser = new Parser(r, this);
    }
    public void Compile() throws Exception
    {
        parser.yyparse();
    }
}
