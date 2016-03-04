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
    };

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
        StringBuilder httpRequest = new StringBuilder();
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
                httpRequest.append(stringBuffer.toString());
                byteBuffer.rewind();
            }
        } while (count == bufferSize);

        httpHeader = HttpHeaderParser.createHttpHeaderReader(httpRequest.toString());
        return this;
    }

    private void writeAnswer() {
        HttpRequestAnswer answer = HttpRequestAnswer.createHttpRequestAnswer(socketChannel);
        if (httpServer.getCash().checkPage(httpHeader.getPath())) {
            answer
                    .setPageBytes(httpServer.getCash().getPage(httpHeader.getPath()))
                    .make();
        } else {
            answer.setPath(httpServer.getHomeDirectory() + httpHeader.getPath());
            httpServer.getCash().addPage(httpHeader.getPath(), answer.getPageBytes());
            answer.make();
        }
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
}
