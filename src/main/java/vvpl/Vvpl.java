package vvpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat.Style;
import java.util.List;

import vvpl.ast.Declaration;
import vvpl.ast.visitors.ASTPrinter;
import vvpl.errors.*;
import vvpl.interpret.Interpreter;
import vvpl.scan.Scanner;
import vvpl.parse.Parser;
import vvpl.scan.Token;

public class Vvpl 
{
	public static void main(String[] args) throws IOException 
	{
		runFile(args[0]);
	}

	private static void runFile(String path) throws IOException 
	{
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes));
	}

	private static void run(String source) 
	{
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		// print the tokens
		// for (Token token : tokens) {
		// 	System.out.println(token);
		// }

		if(ErrorHandler.errors.size() != 0)
		{
			System.err.println("Errors during Scanning");
			return;
		}

		Parser parser = new Parser(tokens);
		List<Declaration> program = parser.parse();

		// print the tree
		// ASTPrinter printer = new ASTPrinter();
		// System.out.println(printer.print(program));
		if(ErrorHandler.errors.size() != 0)
		{
			System.err.println("Errors during Parsing");
			return;
		}

		Interpreter interpreter = new Interpreter();
		try 
		{
			interpreter.interpret(program);
		}
		catch (ScopeError error) 
		{
			System.err.println("Scope Error: " + error.getMessage());
		}
		catch (TypeError error) 
		{
			System.err.println("Type Error: " + error.getMessage());
		}
		catch (SyntaxError error) 
		{
			System.err.println("Syntax Error: " + error.getMessage());
		}
		catch (RuntimeError error) 
		{
			System.err.println("Runtime Error: " + error.getMessage());
		}
		catch (Exception error) 
		{
			System.err.println("[Unknown Error]:" + error.getMessage());
		}
	}
}
