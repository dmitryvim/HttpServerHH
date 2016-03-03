import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Main {

    public  static void main (String [] argv){
        HttpServer
                .build()
                .setInetPort(1234)
                //.setHomeDirectory("/home/dmitry/IdeaProjects/HttpServerHH/server-home")
                .setHomeDirectory("/Users/mhty/IdeaProjects/HttpServer/server-home")
                .start();
    }
}
