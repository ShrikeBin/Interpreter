package vvpl.ast.function;

import vvpl.scan.Token;
import vvpl.ast.*;

public class Param 
{
    public final Token name;  // Identifier
    public final Type type;  // Type annotation
    
    public Param(Token name, Type type) 
    { 
        this.name = name;
        this.type = type; 
    }
}
