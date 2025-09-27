package vvpl.ast.expression;

import vvpl.ast.Declaration;
import vvpl.scan.*;

public final class Assignment 
{
    // assignment := ID "is" assignment | logicOr ; <- WHAT TO DO WITH THAT?
    public final Token ID;
    public final Declaration assORlog;

    public Assignment(Token ID, Declaration assORlog) 
    {
        this.ID = ID;
        this.assORlog = assORlog;
    }
}
