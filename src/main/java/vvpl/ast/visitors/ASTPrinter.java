package vvpl.ast.visitors;

import java.util.List;

import vvpl.ast.*;
import vvpl.ast.expression.*;
import vvpl.ast.statement.*;
import vvpl.ast.function.*;
import vvpl.ast.variable.VarDecl;
import vvpl.scan.Token;

/**
 * @author Nel Skowronek
 * @version CompilerConstruction FT 2025
 */

public class ASTPrinter implements Visitor<Void>
{
    private final StringBuilder builder = new StringBuilder();
    private final StringBuilder prefix = new StringBuilder();

    public String print(List<Declaration> program) {
        builder.append("PROGRAM\n");
        for (int i = 0; i < program.size(); i++) {
            printDeclaration(program.get(i), i == program.size() - 1);
        }
        return builder.toString();
    }

    @Override
    public Void visitVarDecl(VarDecl decl) {
        builder.append("VAR\n");
        if (decl.initializer != null) {
            printToken(decl.name, false);
            printDeclaration(decl.initializer, true);
        } else {
            printToken(decl.name, true);
        }
        return null;
    }

    @Override
    public Void visitFuncDecl(FuncDecl decl) {
        builder.append("FUN\n");
        printToken(decl.name, false);
        for (Param param : decl.params) {
            printDeclaration(param, false);
        }
        if (decl.type != null) {
            printToken(decl.type, false);
        }
        printDeclaration(decl.body, true);
        return null;
    }

    @Override
    public Void visitParamDecl(Param decl) {
        builder.append("PARAM\n");
        printToken(decl.name, false);
        printToken(decl.type, true);
        return null;
    }

    @Override
    public Void visitExprStmt(Expr stmt) {
        builder.append("EXPRESSION\n");
        printDeclaration(stmt.expr, true);
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        builder.append("IF\n");
        printDeclaration(stmt.condition, false);
        if (stmt.elseBranch != null) {
            printDeclaration(stmt.thenBranch, false);
            printDeclaration(stmt.elseBranch, true);
        } else {
            printDeclaration(stmt.thenBranch, true);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        builder.append("PRINT\n");
        printDeclaration(stmt.expression, true);
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        builder.append("BLOCK\n");
        for (int i = 0; i < stmt.statements.size(); i++) {
            printDeclaration(stmt.statements.get(i), i == stmt.statements.size() - 1);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        builder.append("WHILE\n");
        printDeclaration(stmt.condition, false);
        printDeclaration(stmt.body, true);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        builder.append("RETURN\n");
        printDeclaration(stmt.value, true);
        return null;
    }

    @Override
    public Void visitAssignExpr(Assignment expr) {
        builder.append("ASSIGN\n");
        printToken(expr.ID, false);
        printDeclaration(expr.value, true);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Binary expr) {
        builder.append("BINARY\n");
        printDeclaration(expr.left, false);
        printToken(expr.operator, false);
        printDeclaration(expr.right, true);
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) {
        builder.append("LOGICAL\n");
        printDeclaration(expr.left, false);
        printToken(expr.operator, false);
        printDeclaration(expr.right, true);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) {
        builder.append("UNARY\n");
        printToken(expr.operator, false);
        printDeclaration(expr.right, true);
        return null;
    }

    @Override
    public Void visitCastExpr(Cast expr) {
        builder.append("CAST\n");
        printToken(expr.type, false);
        printDeclaration(expr.value, true);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) {
        builder.append("LITERAL\n");
        printToken(expr.value, true);
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) {
        builder.append("VARIABLE\n");
        printToken(expr.name, true);
        return null;
    }

    @Override
    public Void visitCallExpr(Call expr) {
        builder.append("CALL\n");
        printDeclaration(expr.ID, expr.args.size() == 0);
        for (int i = 0; i < expr.args.size(); ++i) {
            printDeclaration(expr.args.get(i), i == expr.args.size() - 1);
        }
        return null;
    }

    private void printToken(Token token, boolean isLast) {
        builder.append(prefix)
               .append(isLast ? "└── " : "├── ")
               .append(token.lexeme)
               .append("\n");
    }

    private void printDeclaration(Declaration decl, boolean isLast) {
        builder.append(prefix);
        if (isLast) {
            builder.append("└── ");
            prefix.append("    ");
        } else {
            builder.append("├── ");
            prefix.append("│   ");
        }
        decl.accept(this);
        prefix.setLength(prefix.length() - 4);
    }
}
