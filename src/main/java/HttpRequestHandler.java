import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HttpRequestHandler implements Runnable {
    private SocketChannel socketChannel;
    public HttpHeaderParser httpHeader;
    private HttpServer httpServer;

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

    public HttpRequestHandler setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
        return this;
    }

    private HttpRequestHandler readHeader() {
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

        httpHeader = HttpHeaderParser.createHttpHeaderReader(requestHeaderText.toString());

        System.out.println("input Header:\n" + httpHeader);
        return this;
    }

    private void writeAnswer() {
        HttpRequestAnswer answer = HttpRequestAnswer.createHttpRequestAnswer(socketChannel);
        if (httpServer.getCash().checkPage(getPath())) {
            System.out.println("use cash " + getPath() + "\n");
            answer
                    .setPageBytes(httpServer.getCash().getPage(getPath()))
                    .make();
        } else {
            System.out.println("use file " + getPath() + "\n");

            answer.setPath(getPath());
            httpServer.getCash().addPage(getPath(), answer.getPageBytes());
            answer.make();
        }
    }

    private String getPath() {
        String path = httpServer.getHomeDirectory() + httpHeader.getPath();
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
                    .setBadAnswer()
                    .make();
        }
    }



    public boolean isDirectory(String uri) {
        return uri.endsWith("/");
    }

    public String addIndex(String uri) {
        return uri + httpServer.getIndexFilename();
    }
}
