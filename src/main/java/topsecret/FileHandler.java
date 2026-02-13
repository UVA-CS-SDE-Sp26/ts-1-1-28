package topsecret;

import java.util.*;
import java.io.IOException;
import java.nio.file.*;


public class FileHandler {

    // Stores the path to the data directory
    private final Path dataDirectory;

    // Default constructor uses "data" folder
    public FileHandler() {
        this("data");
    }

    // Constructor that allows custom directory (important for testing)
    public FileHandler(String directoryName) {
        this.dataDirectory = Paths.get(directoryName);
    }

    // Returns sorted list of .txt file names
    public List<String> listFiles() throws IOException {

        // Check directory exists
        if (!Files.exists(dataDirectory) || !Files.isDirectory(dataDirectory)) {
            throw new IOException("Data directory not found.");
        }

        List<String> fileNames = new ArrayList<>();

        // Read all .txt files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDirectory, "*.cip")) {
            for (Path entry : stream) {
                fileNames.add(entry.getFileName().toString());
            }
        }

        // Sort alphabetically
        Collections.sort(fileNames);
        return fileNames;
    }

    // Reads contents of specified file
    public String readFile(String fileName) throws IOException {
        Path filePath = dataDirectory.resolve(fileName);

        // Check file exists
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new IOException("File not found: " + fileName);
        }

        // Return file contents
        return Files.readString(filePath);
    }
}
