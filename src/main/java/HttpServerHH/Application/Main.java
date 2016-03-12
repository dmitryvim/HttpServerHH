package HttpServerHH.Application;

import HttpServerHH.HttpServer.HttpServer;
import HttpServerHH.HttpServer.ServerSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public  static void main (String [] argv){

        if (argv.length != 1) {
            LOGGER.error("Wrong usage");
            System.exit(1);
        }

        ServerSettings settings = null;
        try {
            settings = ServerSettings.createServerSettings(argv[0]);
            LOGGER.trace("Settings initialized");
        } catch (IOException | SAXException | ParserConfigurationException e) {
            LOGGER.error("Cannot read settings file {}", argv[0], e);
            System.exit(1);
        }


        HttpServer httpServer = HttpServer.build(settings);
        LOGGER.trace("Server built");
        httpServer.start();
        LOGGER.trace("Server started");
    }
}
