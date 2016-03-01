import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by mhty on 26.02.16.
 */
public class HttpRequestAnswer {
    private SocketChannel socketChannel;
    private String path;
    private HttpHeaderServer httpHeader;

    private HttpRequestAnswer(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        httpHeader = HttpHeaderServer.createHttpHeaderWriter(HttpHeader.HTTP_CODE_SUCCESS);
    }

    public static HttpRequestAnswer createHttpRequestAnswer(SocketChannel socketChannel) {
        return new HttpRequestAnswer(socketChannel);
    }

    public void setBadAnswer() {
//        HTTP/1.1 400 Bad Request
//        Server: nginx/1.6.2
//        Date: Sat, 27 Feb 2016 14:14:16 GMT
//        Content-Type: text/html; charset=utf-8
//        Content-Length: 172
//        Connection: close

//        <html>
//        <head><title>400 Bad Request</title></head>
//        <body bgcolor="white">
//        <center><h1>400 Bad Request</h1></center>
//        <hr><center>nginx/1.6.2</center>
//        </body>
//        </html>

        String html = "<html>\n" +
                "<head><title>400 Bad Request</title></head>\n" +
                "<body bgcolor=\"white\">\n" +
                "<center><h1>400 Bad Request</h1></center>\n" +
                "<hr><center>mhServer</center>\n" +
                "</body>\n" +
                "</html>";

        httpHeader.setCode(httpHeader.HTTP_CODE_BAD_REQUEST);
        httpHeader.setStatus("Bad Request");
        httpHeader.setContentLength(html.length());
        httpHeader.addParameter("Server", "mhServer 0.0.1");
        httpHeader.addParameter("Content-type:", "text/html; charset=utf-8");
        httpHeader.addParameter("Connection", "close");

        try {
            socketChannel.write(ByteBuffer.wrap(httpHeader.getBytes()));
            socketChannel.write(ByteBuffer.wrap(html.getBytes()));
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Socket channel write exception.\n");
        }


    }

    public HttpRequestAnswer setPath(String path) {
        this.path = path;
        return this;
    }

    public void make() {
        setContentType();
        setConnectionAlive();
    }

    private void setContentType() {
        httpHeader.addParameter("Content-Type", "text/html");
    }

    private void setConnectionAlive() {
        httpHeader.addParameter("Connection", "keep-alive");
    }


}
