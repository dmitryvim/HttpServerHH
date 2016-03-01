import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mhty on 26.02.16.
 */
public class HttpHeaderTest {
    HttpHeader httpHeader;

    @Before
    public void createHttpHeader() {
        httpHeader = new HttpHeader() {
            String firstLine = " ";

            @Override
            public void parseFirstLine(String line) {
                firstLine = line;
            }

            @Override
            protected StringBuilder getHttpHeaderFirstLine() {
                return new StringBuilder(firstLine + "\r\n");
            }
        };
    }


    @Test
    public void testGetParameter() throws Exception {
        httpHeader.addParameter("A: B");
        assertEquals("B", httpHeader.getParameter("A"));
    }

    @Test
    public void testChangeParameter() throws Exception {
        httpHeader.addParameter("A: B");
        httpHeader.addParameter("A: C");
        assertEquals("C", httpHeader.getParameter("A"));
    }

    @Test
    public void testParseString() throws Exception {
        httpHeader.parseString("abc\r\nA1: B1\n\rA2: B2\n\r\n");
        assertEquals("B1", httpHeader.getParameter("A1"));
        assertEquals("B2", httpHeader.getParameter("A2"));
    }

    @Test
    public void testGetHttpHeader() throws Exception {
        httpHeader.parseFirstLine("abc");
        httpHeader.addParameter("A1: B1");
        httpHeader.addParameter("A2: B2");
        assertEquals("abc\r\nA1: B1\nA2: B2\n\n", httpHeader.getHttpHeader());

    }
}