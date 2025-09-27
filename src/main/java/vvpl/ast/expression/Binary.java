package vvpl.ast.expression;

import vvpl.ast.visitors.Visitor;
import vvpl.scan.Token;
import vvpl.ast.*;

public class Binary 
{
    public final Expression left; 
    public final Token operator; 
    public final Expression right;

    public Binary(Expression left, Token operator, Expression right) 
    { 
        this.left = left; 
        this.operator = operator; 
        this.right = right; 
    }

    public <T> T accept(Visitor<T> visitor) 
    { 
        return visitor.visitBinaryExpr(this); 
    }  
}
