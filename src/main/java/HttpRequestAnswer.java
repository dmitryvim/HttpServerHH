import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class HttpRequestAnswer {
    private SocketChannel socketChannel;
    private FileReader fileReader;
    private HttpHeaderWriter httpHeader;
    private int defaultBufferSize = 1024;
    private byte[] byteHtml;

    final String HTML_PAGE_BAD_REQUEST = "<html>\n" +
            "<head><title>400 Bad Request</title></head>\n" +
            "<body bgcolor=\"white\">\n" +
            "<center><h1>400 Bad Request</h1></center>\n" +
            "<hr><center>mhServer</center>\n" +
            "</body>\n" +
            "</html>";
    final String HTML_PAGE_NOT_FOUND = "<html>\n" +
            "<head><title>404 Code not found</title></head>\n" +
            "<body bgcolor=\"white\">\n" +
            "<center><h1>404 Code not found</h1></center>\n" +
            "<hr><center>mhServer</center>\n" +
            "</body>\n" +
            "</html>";

    private HttpRequestAnswer(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        httpHeader = HttpHeaderWriter.createHttpHeaderWriter(HttpHeader.HTTP_CODE_SUCCESS);
        byteHtml = null;
    }

    public static HttpRequestAnswer createHttpRequestAnswer(SocketChannel socketChannel) {
        return new HttpRequestAnswer(socketChannel);
    }

    public HttpRequestAnswer setBadAnswer() {
        byteHtml = HTML_PAGE_BAD_REQUEST.getBytes();
        httpHeader.setCode(HttpHeader.HTTP_CODE_BAD_REQUEST);
        httpHeader.setStatus(HttpHeader.HTTP_STATUS_BAD_REQUEST);
        setStandartHeaderParametres();
        return this;
    }

    private void setStandartHeaderParametres() {
        httpHeader.setContentLength(byteHtml.length);
        httpHeader.addParameter("Server", "mhServer 0.0.1");
        httpHeader.addParameter("Content-type:", "text/html; charset=utf-8");
        httpHeader.addParameter("Connection", "close");
    }

    public HttpRequestAnswer setPath(String path) {
        System.out.println("Path " + path);
        fileReader = FileReader.createFileReader(path);

        if (!fileReader.exist()) {
            getBytePage();
            httpHeader.setCode(HttpHeader.HTTP_CODE_BAD_REQUEST);
            httpHeader.setStatus(HttpHeader.HTTP_STATUS_SUCCESS);
        } else {
            byteHtml = HTML_PAGE_NOT_FOUND.getBytes();
            httpHeader.setCode(HttpHeader.HTTP_CODE_NOT_FOUND);
            httpHeader.setStatus(HttpHeader.HTTP_STATUS_NOT_FOUND);
            setStandartHeaderParametres();
        }
        setStandartHeaderParametres();

        return this;
    }

    private void getBytePage() {
        try {
            byteHtml = fileReader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpHeader.setContentLength(byteHtml.length);
    }


    public void make() {
        try {
            socketChannel.write(ByteBuffer.wrap(httpHeader.getBytes()));
            socketChannel.write(ByteBuffer.wrap(byteHtml));
            socketChannel.close();
        } catch (InterruptedIOException e) {
            System.out.println("HttpAnswerAnswer InterruptedIOException [path: " + fileReader.getPath() + "]\n");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new RuntimeException("Socket channel write exception.\n");
        }
    }

    public byte[] getPageBytes() {
        return byteHtml;
    }

    public HttpRequestAnswer setPageBytes(byte[] pageBytes) {
        this.byteHtml = pageBytes;
        return this;
    }
}
