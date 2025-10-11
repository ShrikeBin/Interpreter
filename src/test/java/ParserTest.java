import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import vvpl.ast.Declaration;
import vvpl.ast.visitors.BoringASTPrinter;
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

    private static String inputFile1;
    private static String inputFile2;
    /** input file contents as a string of bytes (all in one line) */
    private static String sampleInputString1;
    private static String sampleInputString2;
    /** path to the file which contains the expected output */
    private static String expectedFile1;
    private static String expectedFile2;

    @BeforeAll
    public static void prepareFiles() {
        inputFile1 = "src/test/resources/test1.in";
        inputFile2 = "src/test/resources/test2.in";
        expectedFile1 = "src/test/resources/test1.parse";
        expectedFile2 = "src/test/resources/test2.parse";
        try {
            sampleInputString1 = new String(Files.readAllBytes(Paths.get(inputFile1)));
            sampleInputString2 = new String(Files.readAllBytes(Paths.get(inputFile2)));
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
        StringBuilder builder = new StringBuilder();
        BoringASTPrinter printer = new BoringASTPrinter();
        for (Declaration decl : program) {
            builder.append(printer.print(decl));
        }
        return builder.toString();
    }

    protected String getNewASTString(List<Declaration> program) {
        StringBuilder builder = new StringBuilder();
        ASTPrinter printer = new ASTPrinter();
        return printer.print(program);
    }

    @Test
    public void test1() {
        Scanner scanner = new Scanner(sampleInputString1);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Declaration> program = parser.parse();

        String fileActual = getASTString(program);
        List<String> fileActualLines = Arrays.asList(fileActual.split(System.lineSeparator()));

        try {
            List<String> fileExpectedLines = Files.readAllLines(Paths.get(expectedFile1));
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

    @Test
    public void test2() {
        Scanner scanner = new Scanner(sampleInputString2);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Declaration> program = parser.parse();

        String fileActual = getNewASTString(program);
        List<String> fileActualLines = Arrays.asList(fileActual.split(System.lineSeparator()));

        try {
            List<String> fileExpectedLines = Files.readAllLines(Paths.get(expectedFile2));
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