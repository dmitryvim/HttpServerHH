import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static java.lang.Thread.sleep;

public class Main {

    public  static void main (String [] argv){
        HttpServer httpServer = HttpServer
                .build()
                .setInetPort(12346)
                .setHomeDirectory("/home/dmitry/IdeaProjects/HttpServerHH/server-home");
                //.setHomeDirectory("/Users/mhty/IdeaProjects/HttpServer/server-home");
        httpServer.start();
        try {
            sleep(1000 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        httpServer.stopServer();
    }
}
