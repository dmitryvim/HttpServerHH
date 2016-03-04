import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HttpServer extends Thread{
    private int port;
    private InetSocketAddress inetSocketAddress;
    private ServerSocketChannel serverSocketChannel;

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public String getIndexFilename() {
        return indexFilename;
    }

    private String homeDirectory;
    private String indexFilename = "index.html";
    private int timeout = 2000;
    private volatile boolean active = true;



    public static HttpServer build() {
        return new HttpServer();
    }

    private HttpServer() {
        super();
    }

    public HttpServer setInetPort(int port) {
        this.port = port;
        inetSocketAddress = new InetSocketAddress(port);
        return this;
    }

    public HttpServer setInetSocketAdress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
        this.port = inetSocketAddress.getPort();
        return this;
    }

    public HttpServer setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
        return this;
    }

    public HttpServer setIndexFilename(String indexFilename) {
        this.indexFilename = indexFilename;
        return this;
    }

    public HttpServer setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public void run() {
        while (active) {
            System.out.println("Waiting for connections");
            try (SocketChannel socketChannel = serverSocketChannel.accept()){
                if (socketChannel == null) {
                    Thread.sleep(timeout);
                } else {
                    System.out.println("Incoming connection from: " + socketChannel.socket().getRemoteSocketAddress());

                    HttpRequestHandler requestHandler = new HttpRequestHandler(socketChannel).readHeader();
                    requestHandler
                            .setHttpServer(this)
                            .run();

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread sleeping exception:" + e);
            } catch (IOException e) {
                throw new RuntimeException("Server socket accept exception:" + e);
            }

        }
    }

    @Override
    public synchronized void start() {

        try {
            active = true;
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(inetSocketAddress);
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            throw new RuntimeException("Start server error:" + e);
        }

        super.start();
    }

    public void stopServer() {
        active = false;
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Server socket close error:" + e);
        }
    }


}
