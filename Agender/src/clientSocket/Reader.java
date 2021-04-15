package clientSocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author NicoPuig
 */
public class Reader implements Runnable {

    private final DataInputStream din;

    private final Queue<String> buffer;

    public Reader(InputStream din) {
        this.din = new DataInputStream(din);
        this.buffer = new LinkedList<>();
    }

    public String getLastMessage() {
        return buffer.isEmpty() ? null : buffer.remove();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String inputString = din.readUTF();
                System.out.println(inputString);
                buffer.add(inputString);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
