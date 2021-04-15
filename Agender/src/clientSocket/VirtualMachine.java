package clientSocket;

import java.io.IOException;

/**
 *
 * @author NicoPuig
 */
public class VirtualMachine {

    public static void main(String[] args) {
        String host = "127.0.0.1";  // localhost
        int port = 5500;    // default port

        try {
            VMSocket socketWsp = new VMSocket(host, port, "Whatsapp");

            for (int i = 0; i < 10000; i++) {
                socketWsp.send("");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
