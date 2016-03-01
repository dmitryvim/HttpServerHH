/**
 * Created by mhty on 26.02.16.
 */
public class HttpHeaderClient extends HttpHeader {
    final static String METHOD_GET_STRING = "GET";
    final static char folderDelimiter = '/';

    private String method;
    private String path;
    private String version;


    public static HttpHeaderClient createHttpHeaderReader(String headerString) {
        return new HttpHeaderClient(headerString);
    }

    private HttpHeaderClient(String headerString) {
        super();
        parseString(headerString);
    }

    public boolean ifMethodGet() {
        return method.equals(METHOD_GET_STRING);
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return version;
    }

    @Override
    protected void parseFirstLine(String line)  {
        String[] parsedParameter = line.split(" ", 3);
        if (parsedParameter.length < 3) {
            throw new RuntimeException("Read first line http request exeption. \"" + line + "\"\n");
        }
        method = parsedParameter[0];
        path = parsedParameter[1];
        version = parsedParameter[2];
    }

    @Override
    protected StringBuilder getHttpHeaderFirstLine() {
        StringBuilder httpHeader = new StringBuilder();
        httpHeader
                .append(method)
                .append(" ")
                .append(path)
                .append(" ")
                .append(version)
                .append("\r\n");
        return httpHeader;
    }

    public void  checkPathIsFolder(String indexFilename) {
        if(path.charAt(path.length() - 1) == folderDelimiter) {
            path += indexFilename;
        };
    }


}
