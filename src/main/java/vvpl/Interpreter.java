package vvpl;

import java.util.List;
import java.util.ArrayList;
import java.lang.RuntimeException;

import vvpl.ast.Declaration;
import vvpl.ast.Statement;
import vvpl.ast.Expression;

import vvpl.ast.expression.*;
import vvpl.ast.function.*;
import vvpl.ast.statement.*;
import vvpl.ast.variable.*;

import vvpl.ast.visitors.Visitor;
import vvpl.interprete.Environment;
import vvpl.interprete.Function;


public class Interpreter implements Visitor<Object>
{
    private static class SyntaxError extends RuntimeException{SyntaxError(String msg){super(msg);}}

    private Environment env = new Environment(null);

    public void interpret(List<Declaration> program)
    {
        // ===== Global Function Scope =====
        for (Declaration decl : program) 
        {
            if (decl instanceof FuncDecl) 
            {
                FuncDecl funcDecl = (FuncDecl) decl;
                Function function = new Function(funcDecl.name.lexeme, funcDecl.params, funcDecl.type.lexeme, funcDecl.body);
                env.put(funcDecl.name.lexeme, function);
            }
        }

        // ===== Actuall Interpretation =====
        for(Declaration decl : program)
        {
            decl.accept(this);
        }
    }

    private Object evaluate(Expression expr) 
    {
        return expr.accept(this);
    }

    private void execute(Statement stmt) 
    {
        stmt.accept(this);
    }

    @Override
    public Void visitFuncDecl(FuncDecl decl)
    {
        Function function = new Function(decl.name.lexeme, decl.params, decl.type.lexeme, decl.body);
        env.put(decl.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl decl)
    {
        Object value = null;
        if (decl.initializer != null) 
        {
            value = evaluate(decl.initializer);
            // TODO remove this helper function -> make an inline check
            if (!typeMatch(value, decl.type.lexeme)) 
            {
                throw new SyntaxError("Type mismatch for variable '" +
                                    decl.name.lexeme + "': expected " +
                                    decl.type.lexeme + ", got " +
                                    value.getClass().getSimpleName());
            }
        } 
        else 
        {
            throw new SyntaxError("Uninitialized variable: " + decl.name.lexeme);
        }

        env.set(decl.name.lexeme, value);

        return null;
    }

    @Override
    public Object visitAssignExpr(Assignment expr) 
    { 
        Object value = evaluate(expr.value);
        String name = expr.ID.lexeme;

        Object variable = env.get(name);
        if(variable instanceof Function)
        {
            throw new SyntaxError("Cannot assign value to function: " + name);
        }
        if(!typeMatch(value, variable.getClass().getSimpleName().toLowerCase()))
        {
            throw new SyntaxError("Type mismatch in assignment to '" + name + 
                "': expected " + variable.getClass().getSimpleName().toLowerCase() +
                ", got " + value.getClass().getSimpleName().toLowerCase());
        }
        env.set(name, value);

        return value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) 
    { 
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (left == null || right == null)
            throw new SyntaxError("Something brokie and we null");

        if (!left.getClass().equals(right.getClass())) 
        {
            throw new SyntaxError("Type mismatch in logical expression: l: " 
                + left.getClass().getSimpleName() +
                ", r: " + right.getClass().getSimpleName());
        }

        if(left instanceof Boolean)
        {
            switch (expr.operator.type) 
            {
                case AND:
                    return (Boolean)left && (Boolean)right;
                case OR:
                    return (Boolean)left || (Boolean)right;
                case EQUALS:
                    return left.equals(right);
                case NOT_EQUALS:
                    return !left.equals(right);
                default:
                    throw new SyntaxError("Unknown/Invalid logical operator for Booleans: " + expr.operator.lexeme);
            }
        }
        else if (left instanceof Double)
        {
            switch (expr.operator.type) 
            {
                case EQUALS:
                    return left.equals(right);
                case NOT_EQUALS:
                    return !left.equals(right);
                case GREATER:
                    return (Double)left > (Double)right;
                case GREATER_EQUAL:
                    return (Double)left >= (Double)right;
                case LESS:
                    return (Double)left < (Double)right;
                case LESS_EQUAL:
                    return (Double)left <= (Double)right;
                default:
                    throw new SyntaxError("Unknown/Invalid logical operator for Numbers: " + expr.operator.lexeme);
            }
        }
        else
        {
            throw new SyntaxError("Unsupported type for logical expression: " 
                + left.getClass().getSimpleName());
        }
    }

    @Override
    public Object visitUnaryExpr(Unary expr) 
    { 
        Object right = evaluate(expr.right);
        switch(expr.operator.type)
        {
            case NOT:
                if(!(right instanceof Boolean))
                {
                    throw new SyntaxError("Logical NOT requires a boolean operand.");
                }
                return !(Boolean)right;
            case MINUS:
                if(!(right instanceof Double))
                {
                    throw new SyntaxError("Unary minus requires a numeric operand.");
                }
                else
                {
                    return - (Double)right;
                }
            default:
                throw new SyntaxError("Unknown unary operator: " + expr.operator.lexeme);
        }
    }

    @Override
    public Object visitCallExpr(Call expr) 
    { 
        String ID = expr.ID.lexeme;
        Object callee = env.get(ID);

        if(callee instanceof Function)
        {
            Function function = (Function) callee;
            List<Object> arguments = new ArrayList<>();
            for(Expression arg : expr.args)
            {   
                arguments.add(evaluate(arg));
            }
            Object result = function.call(arguments, env);
            return result;
        }
        else
        {
            throw new SyntaxError("Attempted to call a non-function: " + ID);
        }
    }

    @Override
    public Object visitBinaryExpr(Binary expr) 
    { 
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if(!(left instanceof Double) || !(right instanceof Double)) 
        {
            throw new SyntaxError("Binary operations require numeric operands.");
        }

        switch (expr.operator.type) 
        {
            case ADD:
                return (Double)left + (Double)right;
            case SUB:
                return (Double)left - (Double)right;
            case MULT:  
                return (Double)left * (Double)right;
            case DIV:
                if ((Double)right == 0) 
                {
                    throw new SyntaxError("Division by zero idiot.");
                }
                return (Double)left / (Double)right;
            default:
                throw new SyntaxError("Unknown binary operator: " + expr.operator.lexeme);
        }
    }

    @Override
    public Object visitLiteralExpr(Literal expr) 
    { 
        return expr.value; 
    }

    @Override
    public Object visitVariableExpr(Variable expr) 
    { 
        return env.get(expr.name.lexeme);
    }

    @Override
    public Object visitCastExpr(Cast expr) 
    { 
        Object casted = evaluate(expr.value);
        String targetType = expr.type.lexeme;
        String castedType = casted.getClass().getSimpleName().toLowerCase();

        if(castedType.equals("integer")||castedType.equals("double"))
        {
            castedType = "number";
        }

        if(castedType.equals(targetType))
        {
            return casted;
        }

        // ==== Valid casts ====
        // • Number → String
        // • String → Number (if the String is a valid number)
        // • Number → Boolean (every number other than 13 is false, 13 is true)
        // • Boolean → String
        
        if(castedType.equals("number"))
        {
            if(targetType.equals("string"))
            {
                return casted.toString();
            }
            else if(targetType.equals("bool"))
            {
                return ((Double)casted == 13.0);
            }
            else
            {
                throw new SyntaxError("Invalid cast from Number to " + targetType);
            }
        }
        else if(castedType.equals("string"))
        {
            if(targetType.equals("number"))
            {
                try
                {
                    return Double.parseDouble((String)casted);
                }
                catch(NumberFormatException e)
                {
                    throw new SyntaxError("Invalid cast from String to Number: " + casted);
                }
            }
            else
            {
                throw new SyntaxError("Invalid cast from String to " + targetType);
            }
        }
        else if(castedType.equals("bool"))
        {
            if(targetType.equals("string"))
            {
                return casted.toString();
            }
            else
            {
                throw new SyntaxError("Invalid cast from String to " + targetType);
            }
        }
        else
        {
            throw new SyntaxError("Unsupported cast from " + castedType + " to " + targetType);
        }
    }

    @Override
    public Void visitExprStmt(Expr stmt) 
    { 
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) 
    { 
        Object prinObject = evaluate(stmt.expression);
        if(prinObject == null)
        {
            System.out.println("null");
        }
        else if(prinObject instanceof String)
        {
            System.out.println((String)prinObject);
        }
        else
        {
            throw new SyntaxError("Print statement can only print strings. (or nulls)");
        }
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) 
    { 
        Object condition = evaluate(stmt.condition);
        if(!(condition instanceof Boolean))
        {
            throw new SyntaxError("Condition must be a Boolean");
        }
        if((Boolean)condition)
        {
            execute(stmt.thenBranch);
        }
        else
        {
            if(stmt.elseBranch != null)
            {
                execute(stmt.elseBranch);
            }
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) 
    { 
        Object condition = evaluate(stmt.condition);
        if(!(condition instanceof Boolean))
        {
            throw new SyntaxError("Condition must be a Boolean");
        }
        while((Boolean)condition) //TODO -- sanity check
        {
            execute(stmt.body);
        }
        return null;
    }

    //TODO -- check env handling
    @Override
    public Void visitBlockStmt(Block stmt) 
    { 
        Environment previousEnv = this.env;
        this.env = new Environment(previousEnv);

        for(Declaration decl : stmt.statements)
        {
            decl.accept(this);
        }

        this.env = previousEnv;
        return null;
    }

    // ==== TODO - check these ====
    //   Valid program as it should be supported when you work in groups.
    // • check that variables are declared only once in a scope and initialized
    // • the global scope allows for out-of-order definitions of functions and their calls
    // • check that function statements are only introduced globally (not in Blocks)
    // • check that a return statement occurs only inside a function statement
    // • check that the return statement is the last executed statement in branches
    //   of function bodies; i.e., check that there are no other statements after the
    //   return in a branch.

    // ==== Those are deemed unneccessary and  ====
    // ==== are sentenced to return null; jail ====
    @Override
    public Void visitReturnStmt(Return stmt) 
    { 
        return null; 
    }

    @Override
    public Void visitParamDecl(Param decl) 
    { 
        return null; 
    }

    private boolean typeMatch(Object value, String expectedType) 
    {
        if (value == null) return false;

        switch (expectedType) 
        {
            // TBH nie pamiętam jak wgle nazywaliśmy typy
            case "int":
                return value instanceof Integer;
            case "float":
                return value instanceof Double;
            case "string":
                return value instanceof String;
            case "bool":
                return value instanceof Boolean;
            case "function":
                return value instanceof Function;
            default:
                return true;
        }
    }
}
