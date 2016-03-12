package HttpServerHH.HttpRequest;

import HttpServerHH.FileReader.FileReader;
import HttpServerHH.HttpHeader.HttpHeader;
import HttpServerHH.HttpHeader.HttpHeaderWriter;
import HttpServerHH.HttpServer.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HttpRequestAnswer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestAnswer.class);

    private SocketChannel socketChannel;
    private FileReader fileReader;
    private HttpHeaderWriter httpHeader;
    private byte[] byteHtml;
    private ServerSettings settings;

    public static HttpRequestAnswer createHttpRequestAnswer(SocketChannel socketChannel) {
        return new HttpRequestAnswer(socketChannel);
    }

    private HttpRequestAnswer(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        httpHeader = HttpHeaderWriter.createHttpHeaderWriter();
        httpHeader.setSuccess();
        byteHtml = null;
    }

    public HttpRequestAnswer setSettings(ServerSettings settings) {
        this.settings = settings;
        return this;
    }

    public HttpRequestAnswer setPath(String path) {
        fileReader = FileReader.createFileReader(path);
        fileReader.setDefaultPath(settings.getPathNotFound());
        fileReader.setIndexFile(settings.getIndexFile());
        return this;
    }

    public HttpRequestAnswer setBadAnswer() {
        setPath(settings.getPathBadRequest());
        httpHeader.setBadRequest();
        return this;
    }

    public void make() {
        setHtmlBytes();
        setHeader();

        try {
            socketChannel.write(ByteBuffer.wrap(httpHeader.getBytes()));
            socketChannel.write(ByteBuffer.wrap(byteHtml));
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Socket channel write exception.\n" + e);
        }
    }

    private void setHeader() {
        httpHeader.setHttpVersion(settings.getHttpVersion());
        httpHeader.setContentLength(byteHtml.length);
        httpHeader.addParameter("Server", settings.getServerName() + " " + settings.getServerVersion());
        httpHeader.addParameter("Content-type", "text/html; charset=utf-8");
        httpHeader.addParameter("Connection", "close");
    }

    private void setHtmlBytes() {
        byteHtml = fileReader.read();
        if (fileReader.notFound()) {
            httpHeader.setNotFound();
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
