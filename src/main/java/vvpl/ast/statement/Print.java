package vvpl.ast.statement;

import vvpl.ast.visitors.Visitor;
import vvpl.ast.*;

public class Print extends Statement
{
    public final Expression expression;

    public Print(Expression expression) 
    { 
        this.expression = expression; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitPrintStmt(this); 
    }
}
