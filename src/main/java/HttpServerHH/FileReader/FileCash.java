package HttpServerHH.FileReader;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry on 3/4/16.
 */
public class FileCash {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCash.class);

    private Map<String, Pair<Long, byte[]>> cashedPages;
    private int timeout = 1000 * 60;

    public FileCash() {
        this.cashedPages = new HashMap<>();
    }

    private long getNow() {
        return Calendar.getInstance().getTimeInMillis();
    }

    private boolean checkExistence(String uri) {
        return cashedPages.containsKey(uri);
    }

    private void checkTime(String uri) {
        if (checkExistence(uri)) {
            if (getNow() - cashedPages.get(uri).getKey() > timeout) {
                cashedPages.remove(uri);
            }
        }
    }

    public boolean contains(String uri) {
        checkTime(uri);
        return checkExistence(uri);
    }

    public byte[] getPage(String uri) {
        if (!contains(uri)) {
            return null;
        }
        LOGGER.debug("Page read {}", uri);
        return cashedPages.get(uri).getValue();
    }

    public void addPage(String uri, byte[] page) {
        cashedPages.put(uri, new Pair<>(getNow(), page));
        LOGGER.debug("Page added {}", uri);
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
