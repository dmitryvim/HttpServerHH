package HttpServerHH.Application;

import HttpServerHH.HttpServer.HttpServer;
import HttpServerHH.HttpServer.ServerSettings;

import static java.lang.Thread.sleep;

public class Main {

    public  static void main (String [] argv){
        if (argv.length != 1) {
            System.out.println("Wrong usage! Please enter the path to settings file");
            System.exit(1);
        }

        ServerSettings settings = ServerSettings.createServerSettings(argv[0]);
        HttpServer httpServer = HttpServer.build(settings);
        httpServer.start();
    }
}
