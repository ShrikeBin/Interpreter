import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import vvpl.ast.Declaration;
import vvpl.ast.visitors.ASTPrinter;
import vvpl.parse.Parser;
import vvpl.scan.Scanner;
import vvpl.scan.Token;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sandra Greiner
 * @version Compiler Construction FT 2025
 */
public class ParserTest {

    private static String inputFile;
    /** input file contents as a string of bytes (all in one line) */
    private static String sampleInputString;
    /** path to the file which contains the expected output */
    private static String expectedFile;

    @BeforeAll
    public static void prepareFiles() {
        inputFile = "src/test/resources/sample-input.in";
        expectedFile = "src/test/resources/sample-ast-expected.out";
        try {
            sampleInputString = new String(Files.readAllBytes(Paths.get(inputFile)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * this is one proposed way to print the AST
     * implement an AST-Visitor which prints relevant AST nodes
     * you can find the relevant/expected AST nodes that should be printed in the
     * file
     * sample-ast-expected.out in the test resources
     * ---
     * modified by: Nel Skowronek
     * to match AST implementation
     */
    protected String getASTString(List<Declaration> program) {
        ASTPrinter printer = new ASTPrinter();
        return printer.print(program);
    }

    @Test
    public void testEquivalenceOfEachLine() {
        Scanner scanner = new Scanner(sampleInputString);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Declaration> program = parser.parse();

        String fileActual = getASTString(program);
        List<String> fileActualLines = Arrays.asList(fileActual.split(System.lineSeparator()));

        try {
            List<String> fileExpectedLines = Files.readAllLines(Paths.get(expectedFile));
            for (int i = 0; i < fileExpectedLines.size(); i++) {
                String actualLine = fileActualLines.get(i);
                String expectedLine = fileExpectedLines.get(i);
                assertTrue(actualLine.equals(expectedLine),
                        "line " + i + " of source and target mismatch." + System.lineSeparator() +
                                " Expected the content: " + expectedLine + System.lineSeparator() +
                                "got: " +
                                actualLine + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}