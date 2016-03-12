package HttpServerHH.HttpRequest;

import HttpServerHH.HttpHeader.HttpHeaderParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by mhty on 12.03.16.
 */

public class HttpRequestReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestReader.class);

    HttpHeaderParser httpHeader;
    SocketChannel socketChannel;

    private HttpRequestReader(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    private void readHeader() {
        httpHeader = HttpHeaderParser.createHttpHeaderReader(readHeaderText());
        LOGGER.trace("Header read\n---\n{}---\n", httpHeader.toString());
    }

    private String readHeaderText() {
        final int bufferSize = 255;
        int count;
        char prevSymbol = ' ';
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
            LOGGER.error("ByteBuffer read error", e);
            throw new RuntimeException("ByteBuffer read error", e);
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

    public static HttpHeaderParser readHttpHeader(SocketChannel socketChannel) {
        HttpRequestReader reader = new HttpRequestReader(socketChannel);
        reader.readHeader();
        return reader.httpHeader;
    }
}
