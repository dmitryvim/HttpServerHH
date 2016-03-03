import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HttpRequestHandler {
    private SocketChannel socketChannel;
    public HttpHeaderReader httpHeader;

    public HttpRequestHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
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

        httpHeader = HttpHeaderReader.createHttpHeaderReader(httpRequest.toString());
        System.out.println(httpHeader.getHttpHeader());
        return this;
    }

}
