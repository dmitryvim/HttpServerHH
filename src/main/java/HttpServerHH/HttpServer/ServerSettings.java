package HttpServerHH.HttpServer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerSettings {
    static private final String SERVER_NAME_KEY = "#document/server/name/";
    static private final String SERVER_VERSION_KEY = "#document/server/version/";
    static private final String HTTP_VERSION_KEY = "#document/server/http-version/";
    static private final String HOME_DIRECTORY_KEY = "#document/server/home-directory/";
    static private final String INDEX_FILE_KEY = "#document/server/index-file/";
    static private final String PORT_KEY = "#document/server/port/";
    static private final String CASH_TIMEOUT_KEY = "#document/server/file-cash-timeout/";
    static private final String DIR_NOT_FOUND_KEY = "#document/server/system-page-path/not-found/";
    static private final String DIR_BAD_REQUEST_KEY = "#document/server/system-page-path/bad-request/";
    static private final String DIR_NOT_ALLOWED_KEY = "#document/server/system-page-path/not-allowed/";

    private Map<String, String> attributes;
    private Document document;
    private char delimiter = '/';

    public String getAttr(String key) {
        return attributes.get(key);
    }

    public String getServerName() {
        return getAttr(SERVER_NAME_KEY);
    }

    public String getServerVersion() {
        return getAttr(SERVER_VERSION_KEY);
    }

    public String getHttpVersion() {
        return getAttr(HTTP_VERSION_KEY);
    }

    public String getHomeDirectory() {
        return getAttr(HOME_DIRECTORY_KEY);
    }

    public String getIndexFile() {
        return getAttr(INDEX_FILE_KEY);
    }

    public int getPort() {
        return Integer.parseInt(getAttr(PORT_KEY));
    }

    public int getCashTimeout() {
        return Integer.parseInt(getAttr(CASH_TIMEOUT_KEY));
    }

    public String getPathNotFound() {
        return getAttr(DIR_NOT_FOUND_KEY);
    }

    public String getPathBadRequest() {
        return getAttr(DIR_BAD_REQUEST_KEY);
    }

    public String getPathNotAllowed() {
        return getAttr(DIR_NOT_ALLOWED_KEY);
    }

    public static ServerSettings createServerSettings(String filename) throws IOException, SAXException, ParserConfigurationException {
        return new ServerSettings(filename).init();
    }

    private ServerSettings(String filename) throws ParserConfigurationException, IOException, SAXException {
        attributes = new HashMap<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(filename);
    }

    private ServerSettings init() {
        visit(document, "");
        return this;
    }

    private void visit(Node node, String prefix) {
        if (node.hasChildNodes()) {
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                visit(list.item(i), prefix + node.getNodeName() + delimiter);
            }
        } else {
            String value = node.getNodeValue().trim();
            if (!value.isEmpty()) {
                attributes.put(prefix, value);
            }
        }
    }


}
