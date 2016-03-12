package HttpServerHH.HttpHeader;

public class HttpHeaderWriter extends HttpHeader {

    private String status = HTTP_STATUS_SUCCESS;
    private int code = HTTP_CODE_SUCCESS;
    private String version;

    private HttpHeaderWriter() {
        super();
    }

    public static HttpHeaderWriter createHttpHeaderWriter(int code) {
        return new HttpHeaderWriter();
    }

    public HttpHeaderWriter setStatus(String status) {
        this.status = status;
        return this;
    }

    public HttpHeaderWriter setCode(int code) {
        this.code = code;
        return this;
    }

    public HttpHeaderWriter setVersion(String version) {
        this.version = version;
        return this;
    }

    public HttpHeaderWriter setContentLength(int size) {
        addParameter("Content-length", String.valueOf(size));
        return this;
    }


    @Override
    public void parseFirstLine(String line) {
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
