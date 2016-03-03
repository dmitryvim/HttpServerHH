import org.junit.Test;

import static org.junit.Assert.*;


public class HttpHeaderReaderTest {
    HttpHeaderReader httpHeader;


    @Test
    public void testParseFirstLine() throws Exception {
        httpHeader = HttpHeaderReader.createHttpHeaderReader("GET / HTTP/1.1");
        assertEquals("GET / HTTP/1.1\r\n", httpHeader.getHttpHeaderFirstLine().toString());
    }

    @Test
    public void testGetPath() {
        httpHeader = HttpHeaderReader.createHttpHeaderReader("GET /a/b/c HTTP/1.1");
        assertEquals("/a/b/c", httpHeader.getPath());
    }

    @Test
    public void testIfGetMethodTrue() {
        httpHeader = HttpHeaderReader.createHttpHeaderReader("GET /a/b/c HTTP/1.1");
        assertEquals(true, httpHeader.ifMethodGet());
    }

    @Test
    public void testIfGetMethodFalse() {
        httpHeader = HttpHeaderReader.createHttpHeaderReader("POST /GET/ HTTP/1.1");
        assertEquals(false, httpHeader.ifMethodGet());
    }

    @Test
    public void testGetProtocolVersion() {
        httpHeader = HttpHeaderReader.createHttpHeaderReader("GET /a/b/c HTTP/1.1");
        assertEquals("HTTP/1.1", httpHeader.getProtocolVersion());
    }





}