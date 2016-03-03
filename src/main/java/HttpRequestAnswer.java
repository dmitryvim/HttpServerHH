import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class HttpRequestAnswer {
    private SocketChannel socketChannel;
    private String path;
    private HttpHeaderWriter httpHeader;
    private LinkedList<ByteBuffer> pageBuffers;
    private int defaultBufferSize = 1024;

    private HttpRequestAnswer(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        httpHeader = HttpHeaderWriter.createHttpHeaderWriter(HttpHeader.HTTP_CODE_SUCCESS);
        pageBuffers = new LinkedList<>();
    }

    public static HttpRequestAnswer createHttpRequestAnswer(SocketChannel socketChannel) {
        return new HttpRequestAnswer(socketChannel);
    }

    public HttpRequestAnswer setBadAnswer() {
        String html = "<html>\n" +
                "<head><title>400 Bad Request</title></head>\n" +
                "<body bgcolor=\"white\">\n" +
                "<center><h1>400 Bad Request</h1></center>\n" +
                "<hr><center>mhServer</center>\n" +
                "</body>\n" +
                "</html>";
        pageBuffers.clear();
        pageBuffers.add(ByteBuffer.wrap(html.getBytes()));

        httpHeader.setCode(HttpHeader.HTTP_CODE_BAD_REQUEST);
        httpHeader.setStatus("Bad Request");
        httpHeader.setContentLength(html.length());
        httpHeader.addParameter("Server", "mhServer 0.0.1");
        httpHeader.addParameter("Content-type:", "text/html; charset=utf-8");
        httpHeader.addParameter("Connection", "close");

        return this;
    }

    public HttpRequestAnswer setPath(String path) {
        httpHeader.setCode(HttpHeader.HTTP_CODE_BAD_REQUEST);
        httpHeader.setStatus("Bad Request");
        //httpHeader.setContentLength(html.length());
        httpHeader.addParameter("Server", "mhServer 0.0.1");
        httpHeader.addParameter("Content-type:", "text/html; charset=utf-8");
        httpHeader.addParameter("Connection", "close");

        this.path = path;
        getBytePage();
        return this;
    }

    private void getBytePage() {
        int contentLength = 0;

        try (SeekableByteChannel fileChannel = Files.newByteChannel(Paths.get(path))) {
            int count;
            do {
                pageBuffers.add(ByteBuffer.allocate(defaultBufferSize));
                count = fileChannel.read(pageBuffers.getLast());
                pageBuffers.getLast().rewind();

                contentLength += count;
            } while (count == defaultBufferSize);

        } catch ( IOException e ) {
            throw new RuntimeException("File read exception " + path + "\n");
        }

        httpHeader.setContentLength(contentLength);
    }


    public void make() {
        try {
            socketChannel.write(ByteBuffer.wrap(httpHeader.getBytes()));
            for (ByteBuffer buffer:
                    pageBuffers) {
                buffer.rewind();
                socketChannel.write(buffer);
            }
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Socket channel write exception.\n");
        }
    }

    private void setContentTypeTextHtml() {
        httpHeader.addParameter("Content-Type", "text/html");
    }

    private void setConnectionAlive() {
        httpHeader.addParameter("Connection", "keep-alive");
    }


    public HttpRequestAnswer setCodeNotFound() {
        String html = "<html>\n" +
                "<head><title>404 Code not found</title></head>\n" +
                "<body bgcolor=\"white\">\n" +
                "<center><h1>404 Code not found</h1></center>\n" +
                "<hr><center>mhServer</center>\n" +
                "</body>\n" +
                "</html>";
        pageBuffers.clear();
        pageBuffers.add(ByteBuffer.wrap(html.getBytes()));

        httpHeader.setCode(HttpHeader.HTTP_CODE_NOT_FOUND);
        httpHeader.setStatus("Code note found");
        httpHeader.setContentLength(html.length());
        httpHeader.addParameter("Server", "mhServer 0.0.1");
        httpHeader.addParameter("Content-type:", "text/html; charset=utf-8");
        httpHeader.addParameter("Connection", "close");

        return this;
    }
}
