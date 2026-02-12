import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class filehandler {

    private final Path dataDirectory;

    public filehandler() {
        this("data");
    }

    public filehandler(String directoryName) {
        this.dataDirectory = Paths.get(directoryName);
    }

    public List<String> listFiles() throws IOException {
        if (!Files.exists(dataDirectory) || !Files.isDirectory(dataDirectory)) {
            throw new IOException("Data directory not found.");
        }

        List<String> fileNames = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDirectory, "*.txt")) {
            for (Path entry : stream) {
                fileNames.add(entry.getFileName().toString());
            }
        }

        Collections.sort(fileNames);
        return fileNames;
    }

    public String readFile(String fileName) throws IOException {
        Path filePath = dataDirectory.resolve(fileName);

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new IOException("File not found: " + fileName);
        }

        return Files.readString(filePath);
    }
}
