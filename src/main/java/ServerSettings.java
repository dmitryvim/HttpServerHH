import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// /Users/mhty/IdeaProjects/HttpServer/server-settings/settings.xml
class ServerSettings {
    private Map<String, String> attributes;
    private Document document;
    private char delimiter = '/';

    static public void main(String[] args) {
        ServerSettings ss = new ServerSettings("/Users/mhty/IdeaProjects/HttpServer/server-settings/settings.xml");
        ss.init();
    }

    private ServerSettings(String filename) {
        attributes = new HashMap<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(filename);
        } catch (Exception e) {
            throw new RuntimeException("Cannot read server settings document");
        }
    }

    private void init() {
        visit(document, "");
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
