package HttpServerHH.HttpRequest;

import HttpServerHH.HttpHeader.HttpHeaderParser;
import HttpServerHH.HttpServer.ServerSettings;
import HttpServerHH.FileReader.ServerFileCash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HttpRequestHandler extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestHandler.class);

    private SocketChannel socketChannel;
    private HttpHeaderParser httpHeader;
    private ServerSettings settings;

    private HttpRequestHandler() {
        super();
    }

    static public HttpRequestHandler createHttpRequestHandler() {
        return new HttpRequestHandler();
    }

    public HttpRequestHandler setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        return this;
    }

    public HttpRequestHandler setSettings(ServerSettings settings) {
        this.settings = settings;
        return this;
    }

    private HttpRequestHandler readHeader() {
        httpHeader = HttpHeaderParser.createHttpHeaderReader(readHeaderText());
        LOGGER.trace("Header read");
        return this;
    }

    private String readHeaderText() {
        final int bufferSize = 255;
        int count;
        char prevSymbol = ' ';
        char symbol;
        StringBuilder requestHeaderText = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);

        do {
            count = readBuffer(byteBuffer);
            byteBuffer.rewind();

            if (count > 0) {
                count = addBufferToHeaderText(count, prevSymbol, requestHeaderText, byteBuffer);
                byteBuffer.rewind();
            }
        } while (count == bufferSize);
        return requestHeaderText.toString();
    }

    private int readBuffer(ByteBuffer byteBuffer) {
        int count;
        try {
            count = socketChannel.read(byteBuffer);
        } catch (IOException e) {
            throw new RuntimeException("ByteBuffer read error: " + e);
        }
        return count;
    }

    private int addBufferToHeaderText(int count, char prevSymbol, StringBuilder requestHeaderText, ByteBuffer byteBuffer) {
        char symbol;
        StringBuffer stringBuffer = new StringBuffer(count);
        for (int i = 0; i < count; i++) {
            symbol = ((char) byteBuffer.get());
            if (checkDoubleBackslashN(prevSymbol, symbol)) {
                count = 0;
            }
            stringBuffer.append(symbol);
        }
        requestHeaderText.append(stringBuffer.toString());
        return count;
    }

    private boolean checkDoubleBackslashN(char prevSymbol, char symbol) {
        return symbol == '\n' && prevSymbol == '\n';
    }

    private void writeAnswer() {
        HttpRequestAnswer answer = HttpRequestAnswer
                .createHttpRequestAnswer(socketChannel)
                .setSettings(settings);

        answer.setPath(getPath());
        answer.make();
    }

    private String getPath() {
        String path = settings.getHomeDirectory() + httpHeader.getPath();
        if (isDirectory(path)) {
            path = addIndex(path);
        }
        return path;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
        try {
            readHeader();
            writeAnswer();
        } catch (RuntimeException e) {
            HttpRequestAnswer
                    .createHttpRequestAnswer(socketChannel)
                    .setSettings(settings)
                    .setBadAnswer()
                    .make();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        socketChannel.close();
        LOGGER.trace("close socket channel");
        super.finalize();
    }

    public boolean isDirectory(String uri) {
        return uri.endsWith("/");
    }

    public String addIndex(String uri) {
        return uri + settings.getIndexFile();
    }


}
