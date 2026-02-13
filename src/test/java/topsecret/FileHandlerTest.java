package topsecret;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileHandlerTest {
    private static final String TEST_DIR = "testdata";
    private FileHandler fileHandler;

    @BeforeEach
    void setup() throws IOException {

        // Create test directory
        Files.createDirectories(Paths.get(TEST_DIR));

        // Create test files
        Files.writeString(Paths.get(TEST_DIR, "file1.txt"), "Hello");
        Files.writeString(Paths.get(TEST_DIR, "file2.txt"), "World");

        fileHandler = new FileHandler(TEST_DIR);
    }

    @AfterEach
    void cleanup() throws IOException {

        Files.walk(Paths.get(TEST_DIR))
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException ignored) {}
                });
    }

    @Test
    void testListFiles() throws IOException {

        List<String> files = fileHandler.listFiles();

        assertEquals(2, files.size());
        assertTrue(files.contains("file1.txt"));
        assertTrue(files.contains("file2.txt"));
    }

    @Test
    void testReadFile() throws IOException {

        String content = fileHandler.readFile("file1.txt");

        assertEquals("Hello", content);
    }

    @Test
    void testReadFileNotFound() {

        assertThrows(IOException.class, () -> {
            fileHandler.readFile("missing.txt");
        });
    }

}
