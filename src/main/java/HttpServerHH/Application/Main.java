package HttpServerHH.Application;

import HttpServerHH.HttpServer.HttpServer;
import HttpServerHH.HttpServer.ServerSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public  static void main (String [] argv){

        if (argv.length != 1) {
            LOGGER.error("Tried to start server without settings");
            System.exit(1);
        }

        ServerSettings settings = ServerSettings.createServerSettings(argv[0]);
        LOGGER.trace("Settings initialized");

        HttpServer httpServer = HttpServer.build(settings);
        LOGGER.trace("Server built");

        httpServer.start();
        LOGGER.trace("Server started");
    }
}
