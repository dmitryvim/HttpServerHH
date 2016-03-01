import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by mhty on 20.02.16.
 */
public class WebServer{
    private ServerSocketChannel serverSocketChannel;
    SocketChannel socketChannel;
    private Selector selector;
    private int port;
    private boolean isActive;
    private String serverHomePath = "/Users/mhty/IdeaProjects/HttpServer/server-home";


    private static final String GREETING = "Hello I must be going.\r\n";
    private static final ByteBuffer buffer = ByteBuffer.wrap ( (HttpHeaderHandler.getHeader200(GREETING)).getBytes( ));


    public WebServer(int port) {
        this.port = port;
    }

    private void prepare() {
        try{
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void run() throws Exception{
        isActive = true;
        while (isActive) {
            System.out.println ("Waiting for connections");
            socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) {		//в неблокирующем режиме метод accept() возвращает null если нет новых подключений
                Thread.sleep (2000);
            } else {
                System.out.println("Incoming connection from: " + socketChannel.socket().getRemoteSocketAddress());
                final int bufferSize = 200;
                int count;
                String HttpRequest = new String();
                ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);


                do {
                    count = socketChannel.read(byteBuffer);
                    byteBuffer.rewind();
                    StringBuffer stringBuffer = new StringBuffer(count);
                    for (int i = 0; i < count; i++) {
                        stringBuffer.append((char)byteBuffer.get());
                    }
                    HttpRequest = HttpRequest + stringBuffer;
                    byteBuffer.rewind();
                } while (count == bufferSize);

                HttpHeaderHandler headerHandler = new HttpHeaderHandler();
                headerHandler.setHeader(HttpRequest);
                String location = serverHomePath + headerHandler.getParameter("Path");

                if (location.charAt(location.length() - 1) == '/') {
                    location = location + "index.html";
                }


                int contentLength = 0;
                LinkedList<ByteBuffer> pageBuffers = new LinkedList<>();
                try (SeekableByteChannel fileChannel = Files.newByteChannel(Paths.get(location))) {
                    do {
                        pageBuffers.add(ByteBuffer.allocate(200));
                        count = fileChannel.read(pageBuffers.getLast());
                        pageBuffers.getLast().rewind();

                        contentLength += count;
                    } while (count == bufferSize);

                } catch ( IOException e ) {
                    e.printStackTrace();
                }


                socketChannel.write(ByteBuffer.wrap(HttpHeaderHandler.getHeader200(contentLength).getBytes()));
                for (ByteBuffer buffer:
                     pageBuffers) {
                    buffer.rewind();
                    socketChannel.write(buffer);
                }
                socketChannel.close();

                
            }
        }

    }

    public void start() {
        prepare();
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isActive = false;
    }
}
