package vvpl.ast.statement;

import vvpl.ast.visitors.Visitor;
import vvpl.ast.*;

public final class Return 
{
    public final Expression value;

    public Return(Expression value) 
    { 
        this.value = value; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitReturnStmt(this); 
    }
}
