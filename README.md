# Interpreter

## Team 12 members:

- *Nel Skowronek*
- *Jan Ryszkiewicz*

## Changes made in tests:

### Scanner tests:

- `IDENTIFIER` -> `ID`
- `PLUS` -> `ADD`

### Parser tests:

- `BinaryExpr` -> `LogicalExpr`

for all comparison operators

- `LiteralExpr ... cast_to <type>` -> `CastExpr ... <type> ... LiteralExpr`

for easier eval of AST tree later