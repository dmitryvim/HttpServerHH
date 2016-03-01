import sun.tools.jconsole.HTMLPane;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by mhty on 26.02.16.
 */
public class HttpRequestHandler {
    private SocketChannel socketChannel;
    public HttpHeaderClient httpHeader;

    public HttpRequestHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public HttpRequestHandler readHeader() {
        final int bufferSize = 200;
        int count;
        String HttpRequest;
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);


        do {

            try {
                count = socketChannel.read(byteBuffer);
            } catch (IOException e) {
                throw new RuntimeException("ByteBuffer read error: " + e);
            }
            byteBuffer.rewind();
            StringBuffer stringBuffer = new StringBuffer(count);
            for (int i = 0; i < count; i++) {
                stringBuffer.append((char)byteBuffer.get());
            }
            HttpRequest = stringBuffer.toString();
            byteBuffer.rewind();
        } while (count == bufferSize);

        System.out.println("\n\n---\n" + HttpRequest + "\n---\n\n");
        httpHeader = HttpHeaderClient.createHttpHeaderReader(HttpRequest);
        System.out.println(httpHeader.getHttpHeader());
        return this;
    }

}
