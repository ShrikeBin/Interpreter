package vvpl.errors;

import java.util.ArrayList;

public class ErrorHandler 
{
    public static ArrayList<String> errors = new ArrayList<>();
    public static void error(int line, String message) 
    {
        System.err.println("[line " + line + "] Error" + ": " + message);
        errors.add("[line " + line + "] Error" + ": " + message);
    }
}
