package vvpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import vvpl.ast.Declaration;
import vvpl.ast.visitors.ASTPrinter;
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
		for (Token token : tokens) {
			System.out.println(token);
		}

		Parser parser = new Parser(tokens);
		List<Declaration> program = parser.parse();

		// print the tree
		ASTPrinter printer = new ASTPrinter();
		System.out.println(printer.print(program));

		//TODO do not interpret if there were errors during parsing/Scanning
		Interpreter interpreter = new Interpreter();
		try 
		{
			interpreter.interpret(program);
		}
		catch (RuntimeException error) 
		{
			System.err.println("Error occured: ");
			System.err.println(error.getMessage());
			System.err.println();
		}
	}
}
