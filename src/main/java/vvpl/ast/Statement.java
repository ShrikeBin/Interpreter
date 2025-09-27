package vvpl.ast;

import vvpl.ast.visitors.Visitor;

public abstract class Statement 
{
    public abstract <T> T accept(Visitor<T> visitor);
}
