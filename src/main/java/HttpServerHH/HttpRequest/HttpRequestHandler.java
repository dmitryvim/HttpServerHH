package HttpServerHH.HttpRequest;

import HttpServerHH.HttpHeader.HttpHeaderParser;
import HttpServerHH.HttpServer.ServerSettings;
import HttpServerHH.FileReader.ServerFileCash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HttpRequestHandler extends Thread {
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

        System.out.println("input Header:\n" + httpHeader);
        return this;
    }

    private String readHeaderText() {
        final int bufferSize = 200;
        int count;
        StringBuilder requestHeaderText = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);

        do {
            try {
                count = socketChannel.read(byteBuffer);
            } catch (IOException e) {
                throw new RuntimeException("ByteBuffer read error: " + e);
            }

            byteBuffer.rewind();
            if (count > 0) {
                StringBuffer stringBuffer = new StringBuffer(count);
                for (int i = 0; i < count; i++) {
                    stringBuffer.append((char) byteBuffer.get());
                }
                requestHeaderText.append(stringBuffer.toString());
                byteBuffer.rewind();
            }
        } while (count == bufferSize);
        return requestHeaderText.toString();
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


    public boolean isDirectory(String uri) {
        return uri.endsWith("/");
    }

    public String addIndex(String uri) {
        return uri + settings.getIndexFile();
    }


}
