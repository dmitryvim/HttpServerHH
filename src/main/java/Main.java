import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by mhty on 20.02.16.
 */
public class Main {

    public  static void main (String [] argv){
        //WebServer server = new WebServer(1234);
        //server.start();
        HttpServer
                .build()
                .setInetPort(1234)
                .setHomeDirectory("/home/dmitry/IdeaProjects/HttpServerHH/server-home")
                //.setHomeDirectory("/Users/mhty/IdeaProjects/HttpServer/server-home")
                .start();
    }
}
