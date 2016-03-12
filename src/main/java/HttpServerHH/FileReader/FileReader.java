package HttpServerHH.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


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

    public String getLastModified() {
        pathCheck();
        long time;
        try {
            time = Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            LOGGER.error("Cannot get last modified time", e);
            time = Calendar.getInstance().getTimeInMillis();
        }
        time = time - 60 * 1000;
        final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone(ZoneId.of("GMT")));

        String result = format.format(time);
        LOGGER.trace("Server time, last modified \n\tpath: {} \n\ttime: {}", path.toString(), result);
        return result;
    }

    public String getContentType() {
        pathCheck();
        String pathString = path.toString();
        String extension = pathString.substring(pathString.lastIndexOf('.') + 1);

        if (extension.equals("jpg")) {
            return "image/jpeg";
        }
        if (extension.equals("js")) {
            return "application/javascript";
        }
        return "text/html; charset=utf-8";
    }
}
