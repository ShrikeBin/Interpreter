package vvpl.scan;

import vvpl.ErrorHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a hand-written scanner with error handling and character-wise reading. 
 * 
 * @author Compiler Construction
 * @version Efterar 2025
 * 
 */
public class Scanner 
{
	// Keyword-map
	private static final Map<String, TokenType> keywords;
	static 
	{
		keywords = new HashMap<>();

		// Declarations
		keywords.put("variable",         TokenType.VAR);
		keywords.put("has_type",         TokenType.TYPE_DEF);
		keywords.put("is",               TokenType.ASSIGN);
		keywords.put("function",         TokenType.FUNCTION);
		keywords.put("return",           TokenType.RETURN);

		// Control flow
		keywords.put("if",               TokenType.IF);
		keywords.put("else",             TokenType.ELSE);
		keywords.put("loop_while",       TokenType.WHILE);
		keywords.put("writeToConsole",   TokenType.PRINT);

		// Boolean
		keywords.put("true",             TokenType.TRUE);
		keywords.put("false",            TokenType.FALSE);

		// Logical 
		keywords.put("OR",               TokenType.OR);
		keywords.put("AND",              TokenType.AND);
		keywords.put("NOT",              TokenType.NOT);
		keywords.put("EQUALS",           TokenType.EQUALS);
		keywords.put("NOT_EQUALS",       TokenType.NOT_EQUALS);
		keywords.put("GREATER",          TokenType.GREATER);
		keywords.put("GREATER_EQUAL",    TokenType.GREATER_EQUAL);
		keywords.put("LESS",             TokenType.LESS);
		keywords.put("LESS_EQUAL",       TokenType.LESS_EQUAL);

		// Arithmetic
		keywords.put("add",              TokenType.ADD);
		keywords.put("subtract",         TokenType.SUB);
		keywords.put("multiply",         TokenType.MULT);
		keywords.put("divide",           TokenType.DIV);

		// Cast & Types
		keywords.put("Number",      	 TokenType.NUMBER_TYPE);
		keywords.put("String",      	 TokenType.STRING_TYPE);
		keywords.put("Bool",     		 TokenType.BOOL_TYPE);
		keywords.put("cast_to",          TokenType.CAST);
	}

	// In and output
	private final String source;
	private final List<Token> tokens = new LinkedList<>();

	// Scan state
	private int start = 0;
	private int current = 0;
	private int line = 1;

	public Scanner(String source) 
	{
		this.source = source;
	}

	// Scan tokens
	public List<Token> scanTokens() 
	{
		while (!isAtEnd()) 
		{
			start = current;
			scanToken();
		}

		addToken(TokenType.EOF, "", null);
		return tokens;
	}

	// Scan token
	private void scanToken() 
	{
		char c = advance();
		switch (c) 
		{
			// Single characters
			case '(': addToken(TokenType.LEFT_PAREN, "("); break;
			case ')': addToken(TokenType.RIGHT_PAREN, ")"); break;
			case '{': addToken(TokenType.LEFT_BRACE, "{"); break;
			case '}': addToken(TokenType.RIGHT_BRACE, "}"); break;
			case ',': addToken(TokenType.COMMA, ","); break;
			case ';': addToken(TokenType.SEMICOLON, ";"); break;

			// Multi characters and arithmetic
			case '!': if (match('=')) addToken(TokenType.NOT_EQUALS, "!="); else addToken(TokenType.NOT,"!"); break;
			case '=': if (match('=')) addToken(TokenType.EQUALS, "=="); else addToken(TokenType.ASSIGN, "="); break;
			case '<': if (match('=')) addToken(TokenType.LESS_EQUAL, "<="); else addToken(TokenType.LESS, "<"); break;
			case '>': if (match('=')) addToken(TokenType.GREATER_EQUAL, ">="); else addToken(TokenType.GREATER, ">"); break;
			case '+': addToken(TokenType.PLUS, "+"); break;
			case '-': addToken(TokenType.MINUS, "-"); break;
			case '*': addToken(TokenType.STAR, "*"); break;
			case '/': addToken(TokenType.SLASH, "/"); break;

			// comments
			case '#':
				while (peek() != '\n' && !isAtEnd()) 
				{
					advance();
				}
				break;
			
			// whitespaces
			case ' ':
			case '\r':
			case '\t':
				break;

			case '\n':
				line++;
				break;

			// literals
			case '"':
				string();
				break;

			default:
				identifier(c);
				break;
		}
	}

	private void identifier(char c) 
	{
		if(isAlpha(c))
		{
			while (isAlphaNumeric(peek())) advance();
	
			String text = source.substring(start, current);
	
			TokenType type = keywords.get(text);
			if (type == null) type = TokenType.ID;
			
			addToken(type, text);
		}
		else if(isDigit(c))
		{
			number();
		}
		else
		{
			ErrorHandler.error(line, "Unexpected character: " + c);
		}
	}

	private void number() 
	{
		boolean isFloat = false;

		while (isDigit(peek())) advance();

		// Look for floating point part
		if (peek() == '.' && isDigit(peekFurther())) 
		{
			advance(); // consume '.'
			isFloat = true;
			while (isDigit(peek())) advance();
		}

		String value = source.substring(start, current);

		if(isFloat)
		{
			addToken(TokenType.NUMBER, value, Double.parseDouble(value));
			return;
		}
		else
		{
			// This can be later changed to integer if needed
			addToken(TokenType.NUMBER, value, Double.parseDouble(value));
		}
	}

	private void string() 
	{
		Boolean nextLine = false;
		while (peek() != '"' && !isAtEnd()) 
		{

			if (peek() == '\n') 
			{
				line++;

				if(nextLine)
				{
					ErrorHandler.error(line, "Strings cannot span more than 2 lines.");
					return;
				}
				else
				{
					nextLine = true;
				}
			}		
			advance();
		}

		if (isAtEnd()) 
		{
			ErrorHandler.error(line, "Unterminated string.");
			return;
		}

		advance(); // consume the closing "

		String value = source.substring(start + 1, current - 1);

		addToken(TokenType.STRING, "\"" + value + "\"", value);
	}

	private char advance() 
	{
		return source.charAt(current++);
	}

	private boolean match(char expected) 
	{
		if (isAtEnd())
		{
			return false;
		}

		if (source.charAt(current) != expected)
		{
			return false;
		}

		current++;

		return true;
	}

	private char peek() 
	{
		if (isAtEnd())
		{
			return '\0';
		}

		return source.charAt(current);
	}

	private char peekFurther()
	{
		if(isAtEnd() || current + 1 >= source.length())
		{
			return '\0';
		}

		return source.charAt(current + 1);
	}

	private boolean isAtEnd() 
	{
		return current >= source.length();
	}

	private void addToken(TokenType type, String lexeme, Object literal) 
	{
		tokens.add(new Token(type, lexeme, literal, line));
	}

	private void addToken(TokenType type, String lexeme) 
	{
		addToken(type, lexeme, null);
	}

	private boolean isDigit(char c) 
	{
    	return c >= '0' && c <= '9';
	}

	private boolean isAlpha(char c) 
	{
		return (c >= 'a' && c <= 'z') ||(c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isAlphaNumeric(char c) 
	{
		return isAlpha(c) || isDigit(c);
	}
}
