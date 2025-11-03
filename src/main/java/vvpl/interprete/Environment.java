package vvpl.interprete;

import java.util.HashMap;
import java.util.Map;

public class Environment 
{
    private static class ScopeError extends RuntimeException{ScopeError(String msg){super(msg);}}
    private final Environment parent;
    private final Map<String, Object> scope = new HashMap<>();

    public Environment(Environment parent)
    {
        this.parent = parent;
    }

    public void set(String name, Object obj)
    {
        if(this.scope.get(name) != null)
        {
            this.scope.put(name, obj);
            return;
        }
        else
        {
            throw new ScopeError(name + " is not defined in the current scope.");
        }
    }

    public void put(String name, Object obj)
    {
        if(this.scope.get(name) == null)
        {
            this.scope.put(name, obj);
            return;
        }
        else
        {
            throw new ScopeError(name + " is already defined in the current scope.");
        }
    }

    public Object get(String name)
    {
        Object obj = scope.get(name);
        if(obj == null && parent != null)
        {
            return parent.get(name);
        }
        else
        {
            return obj;
        }
    }
}
