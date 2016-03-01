import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mhty on 26.02.16.
 */
public class HttpHeaderReaderTest {
    HttpHeaderClient httpHeader;


    @Test
    public void testParseFirstLine() throws Exception {
        httpHeader = HttpHeaderClient.createHttpHeaderReader("GET / HTTP/1.1");
        assertEquals("GET / HTTP/1.1\r\n", httpHeader.getHttpHeaderFirstLine().toString());
    }

    @Test
    public void testGetPath() {
        httpHeader = HttpHeaderClient.createHttpHeaderReader("GET /a/b/c HTTP/1.1");
        assertEquals("/a/b/c", httpHeader.getPath());
    }

    @Test
    public void testIfGetMethodTrue() {
        httpHeader = HttpHeaderClient.createHttpHeaderReader("GET /a/b/c HTTP/1.1");
        assertEquals(true, httpHeader.ifMethodGet());
    }

    @Test
    public void testIfGetMethodFalse() {
        httpHeader = HttpHeaderClient.createHttpHeaderReader("POST /GET/ HTTP/1.1");
        assertEquals(false, httpHeader.ifMethodGet());
    }

    @Test
    public void testGetProtocolVersion() {
        httpHeader = HttpHeaderClient.createHttpHeaderReader("GET /a/b/c HTTP/1.1");
        assertEquals("HTTP/1.1", httpHeader.getProtocolVersion());
    }





}