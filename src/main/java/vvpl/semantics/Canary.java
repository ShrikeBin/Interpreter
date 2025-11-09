package vvpl.semantics;

import java.util.*;
import vvpl.ast.*;
import vvpl.ast.expression.*;
import vvpl.ast.function.*;
import vvpl.ast.statement.*;
import vvpl.ast.variable.*;
import vvpl.ast.visitors.Visitor;
import vvpl.errors.*;
import vvpl.scan.*;

public class Canary implements Visitor<String> 
{
    private final TypeTable env = new TypeTable(null);
    private boolean inFunction = false;
    private boolean allowNestedFunctions = false;
    private boolean allowVariableRedeclaration = false;

    public void check(List<Declaration> program) throws SyntaxError, ScopeError, TypeError 
    {
        for (Declaration decl : program) 
        {
            if (decl instanceof FuncDecl f)
            {
                if (env.get(f.name.lexeme) != null)
                {
                    throw new ScopeError("Duplicate function: " + f.name.lexeme);
                }
                String retType = (f.type == null) ? "void" : f.type.lexeme.toLowerCase();
                env.put(f.name.lexeme, retType);
            }
        }

        for (Declaration decl : program) 
        {
            if (!(decl instanceof FuncDecl)) 
            {
                decl.accept(this);
            }
        }
    }

    @Override
    public String visitFuncDecl(FuncDecl func) 
    {
        if (!allowNestedFunctions)
        {
            throw new SyntaxError("Nested functions not allowed: " + func.name.lexeme);
        }

        String retType = (func.type == null) ? "void" : func.type.lexeme.toLowerCase();
        TypeTable local = new TypeTable(env);

        for (Param p : func.params)
        {
            local.put(p.name.lexeme, p.type.lexeme.toLowerCase());
        }

        if(!(func.body instanceof Block))
        {
            throw new SyntaxError("Function body must be a block: " + func.name.lexeme);
        }

        //TODO here we should check for return statements matching the function type, and if the last statement return;
        return null;
    }

    @Override
    public String visitVarDecl(VarDecl decl) 
    {
        String type = getTypeString(decl.type.type);
        if (!allowVariableRedeclaration && env.get(decl.name.lexeme) != null)
        {
            throw new ScopeError("Redeclaration of variable: " + decl.name.lexeme);
        }
        if (decl.initializer != null) 
        {
            String rhsType = decl.initializer.accept(this);
            if (!typeCompatible(rhsType, type))
            {
                throw new TypeError("Type mismatch in variable '" + decl.name.lexeme +
                    "': expected " + type + ", got " + rhsType);
            }
        }
        env.put(decl.name.lexeme, type);
        return null;
    }

    @Override
    public String visitBinaryExpr(Binary expr) 
    {
        String left = expr.left.accept(this);
        String right = expr.right.accept(this);
        if (!left.equals("number") || !right.equals("number"))
        {
            throw new TypeError("Binary operation requires numeric operands.");
        }
        return "number";
    }

    @Override
    public String visitLiteralExpr(Literal expr) 
    {
        switch (expr.value.type) 
        {
            case NUMBER:
                return "number";
            case STRING:
                return "string";
            case TRUE:
            case FALSE:
                return "boolean";
            default:
                return "unknown";
        }
    }

    @Override
    public String visitVariableExpr(Variable expr) 
    {
        String type = env.get(expr.name.lexeme);
        if (type == null) 
        {
            throw new ScopeError("Undeclared variable: " + expr.name.lexeme);
        }
        return type;
    }

    @Override
    public String visitAssignExpr(Assignment expr) 
    {
        String name = expr.ID.lexeme;
        String varType = env.get(name);

        if (varType == null)
        {
            throw new ScopeError("Undeclared variable: " + name);
        }

        String valueType = expr.value.accept(this);

        if (!typeCompatible(valueType, varType))
        {
            throw new TypeError("Type mismatch in assignment to '" + name +
                "': expected " + varType + ", got " + valueType);
        }
        return varType;
    }

    @Override
    public String visitLogicalExpr(Logical expr) 
    {
        String left = expr.left.accept(this);
        String right = expr.right.accept(this);
        if (!left.equals(right))
        {
            throw new TypeError("Logical operands must be same type.");
        }
        return "boolean";
    }

    @Override
    public String visitIfStmt(If stmt) 
    {
        String condType = stmt.condition.accept(this);

        if (!condType.equals("boolean"))
        {
            throw new TypeError("If condition must be boolean.");
        }

        stmt.thenBranch.accept(this);

        if (stmt.elseBranch != null)
        {
            stmt.elseBranch.accept(this);
        }
        return null;
    }

    @Override
    public String visitWhileStmt(While stmt) 
    {
        String condType = stmt.condition.accept(this);

        if (!condType.equals("boolean"))
        {
            throw new TypeError("While condition must be boolean.");
        }

        stmt.body.accept(this);

        return null;
    }

    @Override
    public String visitBlockStmt(Block stmt) 
    {
        //TODO Create a new scope and fix stuff here
        return null;
    }

    @Override
    public String visitReturnStmt(Return stmt) 
    {
        if (!inFunction)
        {
            throw new SyntaxError("Return outside of function");
        }
        if (stmt.value != null) 
        {
            stmt.value.accept(this);
        }
        return null;
    }

    // TODO Check those if need to be like this
    @Override 
    public String visitCallExpr(Call expr) 
    { 
        //TODO i think we can check for function existence and argument types here
        return "unknown"; 
    }
    @Override 
    public String visitUnaryExpr(Unary expr) 
    { 
        //TODO check operand type here
        return "unknown";
    }
    @Override 
    public String visitCastExpr(Cast expr) 
    { 
        return expr.type.lexeme.toLowerCase(); 
    }
    @Override 
    public String visitExprStmt(Expr stmt) 
    { 
        stmt.expr.accept(this); 
        return null; 
    }
    @Override 
    public String visitPrintStmt(Print stmt) 
    { 
        //TODO check expression type here, if its string or not?
        stmt.expression.accept(this); 
        return null; 
    }
    @Override 
    public String visitParamDecl(Param decl) 
    { 
        //TODO dunno about this
        return null; 
    }

    private boolean typeCompatible(String got, String expected) 
    {
        if (expected.equals("number"))
        {
            return got.equals("number") || got.equals("integer") || got.equals("double");
        }
        return got.equals(expected);
    }

    private String getTypeString(TokenType type) 
    {
        switch (type) 
        {
            case NUMBER_TYPE:
                return "number";
            case STRING_TYPE:
                return "string";
            case BOOL_TYPE:
                return "boolean";
            default:
                return "unknown";
        }
    }
}
