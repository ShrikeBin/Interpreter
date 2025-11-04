package vvpl.interpret;

import java.util.List;

import vvpl.ast.Statement;
import vvpl.ast.function.Param;
import vvpl.ast.statement.Block;
import vvpl.errors.SyntaxError;
import vvpl.errors.TypeError;
import vvpl.scan.TokenType;

public class Function 
{
    private final String name;
    private final List<Param> params;
    private final String type;
    private final Statement body;

    public Function(String name, List<Param> params, String type, Statement body) 
    { 
        this.name = name; 
        this.params = params;
        this.type = type;
        this.body = body;

        if(!(body instanceof Block))
        {
            throw new SyntaxError(name + " Function body must be a block statement.");
        }
    }

    public Object call(Interpreter interpreter, List<Object> args) 
    {
        // create new environment for function scope
        Environment functionEnv = new Environment(null);

        //TODO check if this doesnt violate some stuff
        for (Function func : interpreter.env.getFunctions()) 
        {
            functionEnv.put(func.name, func);
        }

        // ==== Check and Bind parameters ====
        for (int i = 0; i < params.size(); i++) 
        {
            String paramName = params.get(i).name.lexeme;
            String paramType = getTypeString(params.get(i).type.type);
            Object argValue = args.get(i);
            if(!typeMatch(argValue, paramType))
            {
                throw new TypeError("Incorrect function parameter type for: " + paramName);
            }
            functionEnv.put(paramName, argValue);
        }

        boolean prev = interpreter.inFunction;
        Environment prevEnv = interpreter.env;
        interpreter.env = functionEnv;
        interpreter.inFunction = true;

        //TODO what about Void functions?
        //TODO how to check i freturn is correct type?

        try
        {
            interpreter.visitBlockStmt((Block) body);
        } 
        catch (Returnable ret) 
        {
            return ret.value;
        }
        finally
        {
            interpreter.env = prevEnv;
            interpreter.inFunction = prev;
        }
        return null;
    }

    private String getTypeString(TokenType type)
    {
        switch(type)
        {
            case NUMBER_TYPE:
                return "number";
            case STRING_TYPE:
                return "string";
            case BOOL_TYPE:
                return "boolean";
            case FUNCTION:
                return "function";
            default:
                return "unknown";
        }
    }

     private boolean typeMatch(Object value, String expectedType) 
    {
        if (value == null) return false;

        switch (expectedType) 
        {
            case "number":
                return value instanceof Double || value instanceof Integer;
            case "integer":
                return value instanceof Integer;
            case "double":
                return value instanceof Double;
            case "string":
                return value instanceof String;
            case "boolean":
                return value instanceof Boolean;
            case "function":
                return value instanceof Function;
            default:
                return false;
        }
    }
}
