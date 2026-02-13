package topsecret;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserInterfaceTest {
    //
    // Based on code from
    // https://www.geeksforgeeks.org/advance-java/unit-testing-of-system-out-println-with-junit/
    //
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // Redirect System.out to a ByteArrayOutputStream
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void restoreSystemOut() {
        // Restore the original System.out after each test
        System.setOut(originalOut);
    }

    @Test
    void testZeroArgs() {
        // Call the main method of TopSecret with no arguments
        UserInterface.main(new String[]{});

        // Define the expected output
        String expectedOutput = "Listing files:";

        // Assert that the output contains expected output
        boolean containsExpected = outputStream.toString().contains(expectedOutput);
        assertTrue(containsExpected);
    }

    @Test
    void testOneArg() {
        // Call the main method of TopSecret with one argument
        UserInterface.main(new String[]{"1"});

        // Define the expected output
        String expectedOutput = "Displaying file contents:";

        // Assert that the output contains expected output
        boolean containsExpected = outputStream.toString().contains(expectedOutput);
        assertTrue(containsExpected);
    }

     @Test
    void testTwoArgs() {
        // Call the main method of TopSecret with two arguments
        UserInterface.main(new String[]{"1", "cipher"});

        // Define the expected output
        String expectedOutput = "Displaying file contents with custom cipher:";

        // Assert that the output contains expected output
        boolean containsExpected = outputStream.toString().contains(expectedOutput);
        assertTrue(containsExpected);
    }

    @Test
    void testHelpArg() {
        // Call the main method of TopSecret with help argument
        UserInterface.main(new String[]{"-h"});

        // Define the expected output
        String expectedOutput = "Help Menu:";

        // Assert that the output contains expected output
        boolean containsExpected = outputStream.toString().contains(expectedOutput);
        assertTrue(containsExpected);
    }

     @Test
    void testStringInsteadOfInt() {
        // Call the main method of TopSecret with a non-integer argument
        UserInterface.main(new String[]{"notANumber"});

        // Define the expected output
        String expectedOutput = "First argument must be a number representing the file index. Use -h or --help for usage information.";

        // Assert that the output contains expected output
        boolean containsExpected = outputStream.toString().contains(expectedOutput);
        assertTrue(containsExpected);
    }

    @Test
    void testTooManyArgs() {
        // Call the main method of TopSecret with too many arguments
        UserInterface.main(new String[]{"1", "cipher", "extraArg"});

        // Define the expected output
        String expectedOutput = "Too many arguments provided. Use -h or --help for usage information.";

        // Assert that the output contains expected output
        boolean containsExpected = outputStream.toString().contains(expectedOutput);
        assertTrue(containsExpected);
    }


}