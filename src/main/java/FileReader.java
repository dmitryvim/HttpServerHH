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

    public boolean exists() {
        return Files.exists(path) && Files.isRegularFile(path);
    }

    public byte[] read(){
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file " + path);
        }
    }

    public Path getPath() {
        return path;
    }
}
