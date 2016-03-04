import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer extends Thread{
    private int port;
    private InetSocketAddress inetSocketAddress;
    private ServerSocketChannel serverSocketChannel;
    private String homeDirectory;
    private String indexFilename = "index.html";
    private int timeout = 2000;
    private volatile boolean active = true;
    private ExecutorService executorService;
    private FileCash cash;

    public FileCash getCash() {
        return cash;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public String getIndexFilename() {
        return indexFilename;
    }

    public static HttpServer build() {
        return new HttpServer();
    }

    private HttpServer() {
        super();
        executorService = Executors.newFixedThreadPool(4);
        cash = new FileCash();
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
        init();

        while (active) {
            System.out.println("Waiting for connections " + Thread.currentThread().getName());
            try (final SocketChannel socketChannel = serverSocketChannel.accept()){
                if (socketChannel == null) {
                    Thread.sleep(timeout);
                } else {
                    System.out.println("Incoming connection from: " + socketChannel.socket().getRemoteSocketAddress());
//                    executorService.submit((Runnable) () -> {
//                        HttpRequestHandler requestHandler = HttpRequestHandler.createHttpRequestHandler().setSocketChannel(socketChannel).setHttpServer(this);
//                        requestHandler.run();
//                    });
                    HttpRequestHandler requestHandler = HttpRequestHandler.createHttpRequestHandler().setSocketChannel(socketChannel).setHttpServer(this);
                    requestHandler.run();

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread sleeping exception:" + e);
            } catch (IOException e) {
                throw new RuntimeException("Server socket accept exception:" + e);
            }

        }
    }

    private void init() {
        try {
            active = true;
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(inetSocketAddress);
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            throw new RuntimeException("Start server error:" + e);
        }
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
