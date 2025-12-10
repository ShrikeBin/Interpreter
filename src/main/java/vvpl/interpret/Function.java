package vvpl.interpret;

import java.util.List;

import vvpl.ast.Statement;
import vvpl.ast.function.Param;
import vvpl.ast.statement.Block;
import vvpl.errors.*;

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
        this.body = body;
        this.type = type;
    }

    public Object call(Interpreter interpreter, List<Object> args, int line) 
    {
        // create new environment for function scope
        Environment functionEnv = new Environment(null);

        for (Function func : interpreter.env.getFunctions()) 
        {
            functionEnv.put(func.name, func);
        } 

        // ==== Check and Bind parameters ====
        for (int i = 0; i < params.size(); i++) 
        {
            String paramName = params.get(i).name.lexeme;
            Object argValue = args.get(i);
            functionEnv.put(paramName, argValue);
        }

        boolean prev = interpreter.inFunction;
        Environment prevEnv = interpreter.env;
        interpreter.env = functionEnv;
        interpreter.inFunction = true;

        try
        {
            interpreter.visitBlockStmt((Block) body);
        } 
        catch (Returnable ret) 
        {
            if(type.equals("void") && ret.value != null)
            {
                throw runtimeError(line, "Function \"" + name + "\" is declared void but returned a value." );
            }
            return ret.value;
        }
        finally
        {
            interpreter.env = prevEnv;
            interpreter.inFunction = prev;
        }

        if(!type.equals("void"))
        {
            throw runtimeError(line, "Function \"" + name + "\" did not return a value." );
        }
        else
        {
            return null;
        }
    }

    private RuntimeError runtimeError(int line, String text)
    {
        ErrorHandler.error(line, text);
        return new RuntimeError(text);
    }
}
