package vvpl.ast;

import vvpl.ast.visitors.Visitor;

public abstract class Expression 
{
    public abstract <T> T accept(Visitor<T> visitor);
}
