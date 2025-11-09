package vvpl.semantics;

import java.util.HashMap;
import java.util.Map;

import vvpl.errors.*;

public class TypeTable 
{
    private final TypeTable parent;
    public final Map<String, String> scope = new HashMap<>();

    public TypeTable(TypeTable parent)
    {
        this.parent = parent;
    }

    public void set(String name, String type)
    {
        if(this.scope.get(name) != null)
        {
            this.scope.put(name, type);
            return;
        }
        else if(parent != null)
        {
            parent.set(name, type);
            return;
        }
        else
        {
            throw new ScopeError(name + " is not defined in the current scope.");
        }
    }

    public void put(String name, String type)
    {
        if(this.scope.get(name) == null)
        {
            this.scope.put(name, type);
            return;
        }
        else
        {
            throw new ScopeError(name + " is already defined in the current scope.");
        }
    }

    public String get(String name)
    {
        String type = scope.get(name);
        if(type == null && parent != null)
        {
            return parent.get(name);
        }
        else
        {
            return type;
        }
    }
}
