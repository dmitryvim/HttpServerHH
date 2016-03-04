import javafx.util.Pair;

import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry on 3/4/16.
 */
public class FileCash {
    private Map<String, Pair<Long, byte[]>> cashedPages;
    private int timeout = 1000 * 60;

    public FileCash() {
        this.cashedPages = new HashMap<>();
    }

    private long getNow() {
        return Calendar.getInstance().getTimeInMillis();
    }

    private boolean checkExistance(String uri) {
        return cashedPages.containsKey(uri);
    }

    private void checkTime(String uri) {
        if (checkExistance(uri)) {
            long time = getNow();
            if (getNow() - cashedPages.get(uri).getKey() > timeout) {
                cashedPages.remove(uri);
            }
        }
    }

    public boolean checkPage(String uri) {
        checkTime(uri);
        return checkExistance(uri);
    }

    public byte[] getPage(String uri) {
        if (!checkPage(uri)) {
            return null;
        }
        return cashedPages.get(uri).getValue();
    }

    public void addPage(String uri, byte[] page) {
        cashedPages.put(uri, new Pair<>(getNow(), page));
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
