import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileReader {
    Path path;

    private FileReader(String filename) {
        this.path = Paths.get(filename);
    }

    public static FileReader createFileReader(String filename) {
        return new FileReader(filename);
    }

    public boolean exist() {
        return Files.exists(path);
    }

    public byte[] read() throws IOException {
        return Files.readAllBytes(path);
    }

    public Path getPath() {
        return path;
    }
}
