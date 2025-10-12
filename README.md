# Interpreter

## Team 12 members:

- *Nel Skowronek*
- *Jan Ryszkiewicz*

## Responsibilities:

- Nel: Parser, Printer
- Jan: Scanner, AST Classes, Tests

## Changes made in tests:

### Scanner tests:

- `IDENTIFIER` -> `ID`
- `PLUS` -> `ADD`

More intuitive and easier to change one test than whole code.

### Parser tests:

- `BinaryExpr` -> `LogicalExpr`

For all comparison operators, because they all return boolean values.

- `LiteralExpr ... cast_to <type>` -> `CastExpr ... <type> ... LiteralExpr`

For easier eval of AST tree later.