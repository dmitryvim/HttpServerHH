import javafx.beans.binding.StringBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by mhty on 26.02.16.
 */
public abstract class HttpHeader {
    final static public int HTTP_CODE_SUCCESS = 200;
    final static public int HTTP_CODE_BAD_REQUEST = 400;
    final static public int HTTP_CODE_NOT_FOUND = 404;
    final static public int HTTP_CODE_METHOD_NOT_ALLOWED = 405;

    final static public String HTTP_STATUS_SUCCESS = "OK";
    final static public String HTTP_STATUS_NOT_FOUND = "Not Found";
    final static public String HTTP_STATUS_BAD_REQUEST = "Bad Request";
    final static public String HTTP_STATUS_METHOD_NOT_ALLOWED = "Not Allowed";

    private HashMap<String, String> parameters;

    protected HttpHeader() {
        parameters = new HashMap<>();

    }

    abstract protected void parseFirstLine(String line);

    public HttpHeader addParameter(String line) {
        String[] parsedParameter = line.split(": ", 2);
        if (parsedParameter.length < 2) {
            throw new RuntimeException("Read http request parameter exeption.\"" + line + "\"\n");
        }
        addParameter(parsedParameter[0], parsedParameter[1]);
        return this;
    }

    public HttpHeader addParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public void parseString(String headerString) {
        String[] parsedHeader = headerString.split("[\\n\\r]");

        parseFirstLine(parsedHeader[0]);
        for (int i = 1; i < parsedHeader.length; i++) {
            if (!parsedHeader[i].equals("")) {
                addParameter(parsedHeader[i]);
            }
        }
    }

    public Set<Map.Entry<String, String>> getParametersEntrySet() {
        return parameters.entrySet();
    }

    abstract protected StringBuilder getHttpHeaderFirstLine();

    public String getHttpHeader() {
        StringBuilder httpHeader = getHttpHeaderFirstLine();
        for (Map.Entry<String, String> entry : getParametersEntrySet()) {
            httpHeader
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }
        httpHeader.append("\n");
        return httpHeader.toString();
    }

    public byte[] getBytes() {
        return getHttpHeader().getBytes();
    }

}
