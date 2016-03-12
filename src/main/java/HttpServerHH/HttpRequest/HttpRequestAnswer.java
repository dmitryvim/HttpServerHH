package HttpServerHH.HttpRequest;

import HttpServerHH.FileReader.FileReader;
import HttpServerHH.HttpHeader.HttpHeader;
import HttpServerHH.HttpHeader.HttpHeaderWriter;
import HttpServerHH.HttpServer.ServerSettings;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HttpRequestAnswer {
    private SocketChannel socketChannel;
    private FileReader fileReader;
    private HttpHeaderWriter httpHeader;
    private int defaultBufferSize = 1024;
    private byte[] byteHtml;
    private ServerSettings settings;

    public HttpRequestAnswer setSettings(ServerSettings settings) {
        this.settings = settings;

        httpHeader.setVersion(settings.getHttpVersion());
        return this;
    }

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
        httpHeader.addParameter("Server", settings.getServerName() + " " + settings.getServerVersion());
        httpHeader.addParameter("Content-type:", "text/html; charset=utf-8");
        httpHeader.addParameter("Connection", "close");
    }

    public HttpRequestAnswer setPath(String path) {
        System.out.println("answer prepare. Path: " + path);
        fileReader = FileReader.createFileReader(path);

        if (fileReader.exists()) {
            readFile();
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

    private void readFile() {
        System.out.println("Try to read file: " + fileReader.getPath());
        byteHtml = fileReader.read();
        httpHeader.setContentLength(byteHtml.length);
    }


    public void make() {
        try {
            socketChannel.write(ByteBuffer.wrap(httpHeader.getBytes()));
            socketChannel.write(ByteBuffer.wrap(byteHtml));
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Socket channel write exception.\n" + e);
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
