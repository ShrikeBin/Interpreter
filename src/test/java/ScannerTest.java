import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import vvpl.scan.Scanner;
import vvpl.scan.Token;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Sandra Greiner
 * @version CompilerConstruction FT 2025
 */

public class ScannerTest {

    private static String inputFile1;
    private static String inputFile2;
    private static String inputByteString1;
    private static String inputByteString2;
    private static String expectedFile1;
    private static String expectedFile2;
   

    @BeforeAll
    public static void prepareFiles() {
        inputFile1  =    "src/test/resources/test1.in";
        inputFile2  =    "src/test/resources/test2.in";
        expectedFile1  = "src/test/resources/test1.scan";
        expectedFile2  = "src/test/resources/test2.scan";
        try {
            inputByteString1 = new String(Files.readAllBytes(Paths.get(inputFile1)));
            inputByteString2 = new String(Files.readAllBytes(Paths.get(inputFile2)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test1() {
        Scanner lexer = new Scanner(inputByteString1);
        List<Token> tokens = lexer.scanTokens();

        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder.append(token + System.lineSeparator());
            if (token.lexeme.equals(";")) builder.append(System.lineSeparator());
        }

        String fileActual = builder.toString();
        List<String> fileActualLines = Arrays.asList(fileActual.split("\\R"));

        try {
            List<String> fileExpectedLines = Files.readAllLines(Paths.get(expectedFile1));
            for (int i = 0; i < fileExpectedLines.size(); i++) {
                String actualLine = fileActualLines.get(i);
                String expectedLine = fileExpectedLines.get(i);
                assertTrue(actualLine.equals(expectedLine), 
                "line " + i + " of source and target mismatch. " + System.lineSeparator() + 
                "expected: " + expectedLine + System.lineSeparator() + 
                " got: " + actualLine);            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test2() {
        Scanner lexer = new Scanner(inputByteString2);
        List<Token> tokens = lexer.scanTokens();

        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder.append(token + System.lineSeparator());
            //if (token.lexeme.equals(";")) builder.append(System.lineSeparator());
        }

        String fileActual = builder.toString();
        List<String> fileActualLines = Arrays.asList(fileActual.split("\\R"));

        try {
            List<String> fileExpectedLines = Files.readAllLines(Paths.get(expectedFile2));
            for (int i = 0; i < fileExpectedLines.size(); i++) {
                String actualLine = fileActualLines.get(i);
                String expectedLine = fileExpectedLines.get(i);
                assertTrue(actualLine.equals(expectedLine), 
                "line " + i + " of source and target mismatch. " + System.lineSeparator() + 
                "expected: " + expectedLine + System.lineSeparator() + 
                " got: " + actualLine);            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}