package vvpl;

public class ErrorHandler 
{
    public static void error(int line, String message) 
    {
        System.err.println("[line " + line + "] Error" + ": " + message);
    }
}
