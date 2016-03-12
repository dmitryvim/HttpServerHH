package HttpServerHH.HttpRequest;

import HttpServerHH.FileReader.FileReader;
import HttpServerHH.HttpHeader.HttpHeaderParser;
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
    private HttpHeaderParser requestHeader;
    private ByteBuffer byteHtml;
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

    public HttpRequestAnswer setBadAnswer() {
        setFileReader(settings.getPathBadRequest());
        httpHeader.setBadRequest();
        return this;
    }

    public HttpRequestAnswer setNotAllowed() {
        setFileReader(settings.getPathNotAllowed());
        httpHeader.setNotAllowed();
        return this;
    }

    public HttpRequestAnswer setNotModified() {
        httpHeader.setNotModified();
        return this;
    }

    public void make() {
        setFileReader(requestHeader.getPath());
        checkCharset();
        setHtmlBytes();
        setHeader();

        try {
            socketChannel.write(ByteBuffer.wrap(httpHeader.getBytes()));
            socketChannel.write(byteHtml);
        } catch (IOException e) {
            LOGGER.error("Socket channel write exception.", e);
        }
    }


    private void checkCharset() {
        final String KEY_ACCEPT_CHARSET = "Accept-charset";
        if (requestHeader.hasParameter(KEY_ACCEPT_CHARSET)) {
            fileReader.setCharset(requestHeader.getParameter(KEY_ACCEPT_CHARSET));
        }
    }

    private void setFileReader(String path) {
        fileReader = FileReader.createFileReader(settings.getHomeDirectory() + path);
        fileReader.setDefaultPath(settings.getPathNotFound());
        fileReader.setIndexFile(settings.getIndexFile());
    }


    private void setHeader() {
        httpHeader.setHttpVersion(settings.getHttpVersion());
        httpHeader.setContentLength(byteHtml.remaining());
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

    public HttpRequestAnswer setRequestHeader(HttpHeaderParser requestHeader) {
        this.requestHeader = requestHeader;
        return this;
    }
}
