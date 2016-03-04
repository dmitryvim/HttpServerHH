import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HttpRequestHandler implements Runnable {
    private SocketChannel socketChannel;
    public HttpHeaderParser httpHeader;
    private HttpServer httpServer;

    public HttpRequestHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public HttpRequestHandler setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
        return this;
    }

    public HttpRequestHandler readHeader() {
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
        System.out.println(httpHeader.getHttpHeader());
        return this;
    }

    @Override
    public void run() {
        try {
            httpHeader.checkPathIsFolder(httpServer.getIndexFilename());
            if (!FileReader.createFileReader(httpServer.getHomeDirectory() + httpHeader.getPath()).exist()) {
                HttpRequestAnswer
                        .createHttpRequestAnswer(socketChannel)
                        .setNotFound()
                        .make();
            } else {
                HttpRequestAnswer
                        .createHttpRequestAnswer(socketChannel)
                        .setPath(httpServer.getHomeDirectory() + httpHeader.getPath())
                        .make();
            }
        } catch (RuntimeException e) {
            HttpRequestAnswer
                    .createHttpRequestAnswer(socketChannel)
                    .setBadAnswer()
                    .make();
        }
    }
}
