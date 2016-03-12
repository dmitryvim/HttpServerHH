package HttpServerHH.HttpRequest;

import HttpServerHH.FileReader.FileReader;
import HttpServerHH.HttpHeader.HttpHeaderWriter;
import HttpServerHH.HttpServer.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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

    public HttpRequestAnswer setNotAllowed() {
        setPath(settings.getPathNotAllowed());
        httpHeader.setNotAllowed();
        return this;
    }

    public HttpRequestAnswer setNotModified() {
        httpHeader.setNotModified();
        return this;
    }

    public void make() {
        setHtmlBytes();
        setHeader();

        try {
            socketChannel.write(ByteBuffer.wrap(httpHeader.getBytes()));
            socketChannel.write(ByteBuffer.wrap(byteHtml));
        } catch (IOException e) {
            LOGGER.error("Socket channel write exception.", e);
        }
    }

    private void setHeader() {
        httpHeader.setHttpVersion(settings.getHttpVersion());
        httpHeader.setContentLength(byteHtml.length);
        httpHeader.addParameter("Server", settings.getServerName() + " " + settings.getServerVersion());
        httpHeader.addParameter("Content-type", fileReader.getContentType());
        httpHeader.addParameter("Connection", "close");
        httpHeader.addParameter("Cache-Control", "max-age=3600");
        httpHeader.addParameter("Last-Modified", fileReader.getLastModified());
        httpHeader.addParameter("Date", getNowString());
    }

    private String getNowString() {
        final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone(ZoneId.of("GMT")));
        return format.format(Calendar.getInstance().getTimeInMillis());
    }

    private void setHtmlBytes() {
        byteHtml = fileReader.read();
        if (fileReader.notFound()) {
            httpHeader.setNotFound();
        }
    }
}
