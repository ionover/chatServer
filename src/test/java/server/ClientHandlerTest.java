package server;

import org.example.server.ClientHandler;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ClientHandlerTest {

    @Test
    void send_shouldFormatMessageCorrectly() throws Exception {
        ClientHandler handler = new ClientHandler(null, null);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);

        Field outField = ClientHandler.class.getDeclaredField("out");
        outField.setAccessible(true);
        outField.set(handler, pw);

        handler.send("alice", "hello world");

        String expected = "[alice] hello world" + System.lineSeparator();
        assertEquals(expected, sw.toString());
    }
}
