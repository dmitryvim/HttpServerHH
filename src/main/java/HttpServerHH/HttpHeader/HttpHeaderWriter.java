package HttpServerHH.HttpHeader;

public class HttpHeaderWriter extends HttpHeader {
    final static private int HTTP_CODE_SUCCESS = 200;
    final static private int HTTP_CODE_BAD_REQUEST = 400;
    final static private int HTTP_CODE_NOT_FOUND = 404;
    final static private int HTTP_CODE_METHOD_NOT_ALLOWED = 405;

    final static private String HTTP_STATUS_SUCCESS = "OK";
    final static private String HTTP_STATUS_NOT_FOUND = "Not Found";
    final static private String HTTP_STATUS_BAD_REQUEST = "Bad Request";
    final static private String HTTP_STATUS_METHOD_NOT_ALLOWED = "Not Allowed";

    private String status = HTTP_STATUS_SUCCESS;
    private int code = HTTP_CODE_SUCCESS;
    private String version;

    private HttpHeaderWriter() {
        super();
    }

    public static HttpHeaderWriter createHttpHeaderWriter() {
        return new HttpHeaderWriter();
    }

    public void setSuccess() {
        setStatus(HTTP_STATUS_SUCCESS);
        setCode(HTTP_CODE_SUCCESS);
    }

    public void setNotFound() {
        setStatus(HTTP_STATUS_NOT_FOUND);
        setCode(HTTP_CODE_NOT_FOUND);
    }

    public void setBadRequest() {
        setStatus(HTTP_STATUS_BAD_REQUEST);
        setCode(HTTP_CODE_BAD_REQUEST);
    }

    public void setNotAllowed() {
        setStatus(HTTP_STATUS_METHOD_NOT_ALLOWED);
        setCode(HTTP_CODE_METHOD_NOT_ALLOWED);
    }

    public HttpHeaderWriter setStatus(String status) {
        this.status = status;
        return this;
    }

    public HttpHeaderWriter setCode(int code) {
        this.code = code;
        return this;
    }

    public HttpHeaderWriter setHttpVersion(String version) {
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
