import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mhty on 20.02.16.
 */
@Deprecated
public class HttpHeaderHandler {
    private String headerString;
    private boolean parsed;
    private HashMap<String, String> parameters;


    HttpHeaderHandler() {
        headerString = new String();
        parameters = new HashMap<>();
        cleanHeader();
    }

    public static String getHeader200(String line) {
        return getHeader200(line.length());
    }

    public static String getHeader200(int contentLength) {
        return "HTTP/1.1 200 OK\n" +
                "Content-Type: text/html\n" +
                "Content-length:" + contentLength + "\n" +
                "Connection: keep-alive\n\n";
    }

    public void cleanHeader() {
        headerString = "";
        parsed = false;
    }

    public void append(ByteBuffer byteBuffer, int count) {
        parsed = false;
        //
    }

    public void setHeader(String line) {
        headerString = new String(line);
    }

    private void parseHeader() {
        parameters.clear();
        String[] lines = headerString.split("\n");
        String[] firstLine = lines[0].split(" ");
        parameters.put("Method", firstLine[0]);
        parameters.put("Path", firstLine[1]);
        parameters.put("Version", firstLine[2]);
        for (String line :
                lines) {
            String[] parsedLine = line.split(": ", 2);
            if (parsedLine.length > 1) {
                parameters.put(parsedLine[0], parsedLine[1]);
            }

        }
        parsed = true;
    }

    public String getString() {
        return headerString.toString();
    }

    public String getParameter(String key) {
        if (!parsed) {
            parseHeader();
        }
        return parameters.get(key);
    }
    
    public String getParams() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!parsed) {
            parseHeader();
        }
        for (Map.Entry<String, String> entry : parameters.entrySet())
        {
            stringBuilder.append(entry.getKey() + ":" + entry.getValue());
        }
        return stringBuilder.toString();
    }




}
