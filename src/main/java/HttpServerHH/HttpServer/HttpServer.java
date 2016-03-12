package HttpServerHH.HttpServer;

import HttpServerHH.HttpRequest.HttpRequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer extends Thread{
    private ServerSettings settings;

    private ServerSocketChannel serverSocketChannel;
    private volatile boolean active = true;
    private ExecutorService executorService;
    private ServerFileCash cash;

    public ServerFileCash getCash() {
        return cash;
    }

    public static HttpServer build(ServerSettings settings) {
        HttpServer httpServer = new HttpServer();
        httpServer.settings = settings;
        return httpServer;
    }

    private HttpServer() {
        super();
        this.executorService = Executors.newFixedThreadPool(4);
        this.cash = new ServerFileCash();
    }

    public ServerSettings getSettings() {
        return settings;
    }

    @Override
    public void run() {
        init();

        while (active) {
            System.out.println("Waiting for connections " + Thread.currentThread().getName());
            try (final SocketChannel socketChannel = serverSocketChannel.accept()){
                if (socketChannel != null) {
                    System.out.println("Incoming connection from: " + socketChannel.socket().getRemoteSocketAddress());
//                    executorService.submit((Runnable) () -> {
//                        HttpServerHH.HttpRequest.HttpRequestHandler requestHandler = HttpServerHH.HttpRequest.HttpRequestHandler.createHttpRequestHandler().setSocketChannel(socketChannel).setHttpServer(this);
//                        requestHandler.run();
//                    });

                    HttpRequestHandler
                            .createHttpRequestHandler()
                            .setSocketChannel(socketChannel)
                            .setSettings(settings)
                            .setFileCash(cash)
                            .run();

//                    HttpRequestHandler requestHandler = HttpRequestHandler
//                            .createHttpRequestHandler()
//                            .setSocketChannel(socketChannel)
//                            .setHttpServer(this);
//                    requestHandler.run();

                }
            } catch (IOException e) {
                throw new RuntimeException("Server socket accept exception:" + e);
            }
        }
    }

    private void init() {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(settings.getPort());

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(inetSocketAddress);
            serverSocketChannel.configureBlocking(true);

            cash.setTimeout(settings.getCashTimeout());

            active = true;
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
