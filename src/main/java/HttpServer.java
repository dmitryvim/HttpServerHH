import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HttpServer extends Thread{
    private int port;
    private InetSocketAddress inetSocketAddress;
    private ServerSocketChannel serverSocketChannel;
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
                if (socketChannel == null) {        //в неблокирующем режиме метод accept() возвращает null если нет новых подключений
                    Thread.sleep(timeout);
                } else {
                    System.out.println("Incoming connection from: " + socketChannel.socket().getRemoteSocketAddress());

                    HttpRequestHandler requestHandler = new HttpRequestHandler(socketChannel).readHeader();
                    try{
                        requestHandler.httpHeader.checkPathIsFolder(indexFilename);
                        if (!FileReader.createFileReader(homeDirectory + requestHandler.httpHeader.getPath()).exist()) {
                            HttpRequestAnswer
                                    .createHttpRequestAnswer(socketChannel)
                                    .setCodeNotFound()
                                    .make();
                        } else {
                            HttpRequestAnswer
                                    .createHttpRequestAnswer(socketChannel)
                                    .setPath(homeDirectory + requestHandler.httpHeader.getPath())
                                    .make();
                        }
                    } catch (RuntimeException e) {
                        HttpRequestAnswer
                                .createHttpRequestAnswer(socketChannel)
                                .setBadAnswer()
                                .make();
                    }


                }
            } catch (IOException e) {
                throw new RuntimeException("Server socket channel accept exception:" + e);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread sleeping exception:" + e);
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
