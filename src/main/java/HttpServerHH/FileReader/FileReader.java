package HttpServerHH.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReader.class);

    private Path path;
    private Path defaultPath;
    private String indexFile;
    private boolean flagNotFound = false;

    private FileReader(String filename) {
        this.path = Paths.get(filename);
        flagNotFound = false;
    }

    public static FileReader createFileReader(String filename) {
        return new FileReader(filename);
    }

    public byte[] read(){
        pathCheck();
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file " + path);
        }
    }

    private void pathCheck() {
        if (Files.notExists(path)) {
            LOGGER.warn("Path {}  doesn't exists, read from default path {}", path.toString(), defaultPath.toString());
            path = defaultPath;
            flagNotFound = true;
        }
        if (Files.isDirectory(path)) {
            LOGGER.warn("Path {} is directory, used index file {}", path.toString(), indexFile);
            path = Paths.get(path.toString(), indexFile);
        }
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = Paths.get(defaultPath);
        if (Files.notExists(this.defaultPath)) {
            LOGGER.error("Default oath doesn't exist {}", defaultPath);
            throw new RuntimeException("Default oath doesn't exist " + defaultPath);
        }
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }

    public boolean notFound() {
        return flagNotFound;
    }
}
