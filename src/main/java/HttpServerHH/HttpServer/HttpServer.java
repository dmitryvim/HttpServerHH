package HttpServerHH.HttpServer;

import HttpServerHH.HttpRequest.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer extends Thread{
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    ExecutorService service;
    private ServerSettings settings;
    private volatile boolean active = true;

    public static HttpServer build(ServerSettings settings) {
        HttpServer httpServer = new HttpServer();
        httpServer.settings = settings;
        return httpServer;
    }

    private HttpServer() {
        super();
        service = Executors.newFixedThreadPool(4);
    }

    @Override
    public void run() {

        try(final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()){
            initServerSocketChannel(serverSocketChannel);
            while (active) {
                acceptSocketChannel(serverSocketChannel);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot start server socket channel", e);
            throw new RuntimeException("Cannot start server socket channel");
        } finally {
            service.shutdown();
        }

    }

    private void initServerSocketChannel(ServerSocketChannel serverSocketChannel) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(settings.getPort());
        serverSocketChannel.socket().bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(true);
    }

    private void acceptSocketChannel(ServerSocketChannel serverSocketChannel) {
        LOGGER.trace("Waiting for connections");
        try {
            // ToDo check safe socket channel closing
            final SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                LOGGER.info("Incoming connection from: {}", socketChannel.socket().getRemoteSocketAddress());

                service.submit(HttpRequestHandler
                        .createHttpRequestHandler()
                        .setSocketChannel(socketChannel)
                        .setSettings(settings));

            }
        } catch (IOException e) {
            LOGGER.error("Server socket accept exception", e);
            throw new RuntimeException("Server socket accept exception", e);
        }
    }



    public void stopServer() {
        active = false;
    }


}
