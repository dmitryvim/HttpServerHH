/**
 * Created by mhty on 26.02.16.
 */
public class HttpHeaderServer extends HttpHeader{

    private String status = HTTP_STATUS_SUCCESS;
    private int code = HTTP_CODE_SUCCESS;
    private String version = "HTTP/1.1";

    private HttpHeaderServer() {
        super();
    }

    public static HttpHeaderServer createHttpHeaderWriter(int code) {
        HttpHeaderServer httpHeader = new HttpHeaderServer();
        return httpHeader;
    }

    public HttpHeaderServer setStatus(String status) {
        this.status = status;
        return this;
    }

    public HttpHeaderServer setCode(int code) {
        this.code = code;
        return this;
    }

    public HttpHeaderServer setVersion(String version) {
        this.version = version;
        return this;
    }

    public HttpHeaderServer setContentLength(int size) {
        addParameter("Content-length", String.valueOf(size));
        return this;
    }


    @Override
    protected void parseFirstLine(String line) {
        String[] parsedParameter = line.split(" ", 3);
        if (parsedParameter.length < 3) {
            throw new RuntimeException("Read first line http request exeption. \"" + line + "\"\n");
        }
        version = parsedParameter[0];
        code = Integer.parseInt(parsedParameter[1]);
        status = parsedParameter[2];
    }

    @Override
    protected StringBuilder getHttpHeaderFirstLine() {
        StringBuilder httpHeader = new StringBuilder();
        httpHeader
                .append(version)
                .append(" ")
                .append(code)
                .append(" ")
                .append(status)
                .append("\r\n");
        return httpHeader;
    }



}
