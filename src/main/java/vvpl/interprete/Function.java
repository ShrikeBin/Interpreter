package vvpl.interprete;

import java.util.List;

import vvpl.ast.Statement;
import vvpl.ast.function.Param;

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
    }

    public Object call(List<Object> args, Environment callerEnv)
    {
        // create new environment for function scope
        Environment functionEnv = new Environment(null);

        // bind parameters to arguments
        for (int i = 0; i < params.size(); i++) 
        {
            // TODO type checking for params (if type of args[i] matches params[i].type)
            String paramName = params.get(i).name.lexeme;
            Object argValue = args.get(i);
            functionEnv.put(paramName, argValue);
        }

        // Object returnValue = this.body.run(functionEnv);
        Object returnValue = null;
        return returnValue;
    }
}
