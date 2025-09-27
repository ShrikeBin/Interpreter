package vvpl.ast;

import vvpl.scan.Token;
import vvpl.ast.visitors.*;

public class Type extends Declaration
{
    public final Token name;  // Type name
    public Type(Token name) 
    { 
        this.name = name; 
    }
    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitType(this); 
    }
}
