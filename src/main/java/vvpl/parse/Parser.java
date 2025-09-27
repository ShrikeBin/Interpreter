package vvpl.parse;

import java.util.ArrayList;
import java.util.List;

import vvpl.ast.Stmt;
import vvpl.scan.Token;

/**
 * @author Sandra Greiner
 * @version CompilerConstruction FT 2025
 */

public class Parser {

    private List<Token> tokens;
   
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
       
        return statements;
    }

}