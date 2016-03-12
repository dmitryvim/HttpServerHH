package HttpServerHH.HttpRequest;

import HttpServerHH.HttpHeader.HttpHeaderParser;
import HttpServerHH.HttpServer.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class HttpRequestManager extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestManager.class);

    private SocketChannel socketChannel;
    private HttpHeaderParser httpHeader;
    private ServerSettings settings;

    private HttpRequestManager() {
        super();
    }

    static public HttpRequestManager createHttpRequestManager() {
        return new HttpRequestManager();
    }

    public HttpRequestManager setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        return this;
    }

    public HttpRequestManager setSettings(ServerSettings settings) {
        this.settings = settings;
        return this;
    }

    @Override
    public void run() {
        try {
            readHeader();
            writeAnswer();
        } catch (RuntimeException e) {
            LOGGER.error("request exception", e);
            sendBadRequest();
        } finally {
            try {
                LOGGER.trace("socket channel closed");
                socketChannel.close();
            } catch (IOException e) {
                LOGGER.error("Cannot close socket channel", e);
            }
        }
    }

    private void readHeader() {
        httpHeader = HttpRequestReader.readHttpHeader(socketChannel);
    }

    private void writeAnswer() {
        HttpRequestAnswer
                .createHttpRequestAnswer(socketChannel)
                .setSettings(settings)
                .setPath(getPath())
                .make();
    }

    private String getPath() {
        return settings.getHomeDirectory() + httpHeader.getPath();
    }



    private void sendBadRequest() {
        try {
            HttpRequestAnswer
                    .createHttpRequestAnswer(socketChannel)
                    .setSettings(settings)
                    .setBadAnswer()
                    .make();
        } catch (RuntimeException e) {
            LOGGER.error("Cannot send bad request", e);
        }
    }
}
