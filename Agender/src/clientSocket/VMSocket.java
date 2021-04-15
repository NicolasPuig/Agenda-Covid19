package clientSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author NicoPuig
 */
public class VMSocket {

    private final String via;

    private final Socket socket;

    private final Reader reader;
    
    private final DataOutputStream dout;

    public VMSocket(String host, int port, String via) throws IOException {
        this.via = via;
        this.socket = new Socket(host, port);
        this.dout = new DataOutputStream(socket.getOutputStream());
        this.reader = new Reader(socket.getInputStream());
        Thread readerThread = new Thread(reader);
        readerThread.setName("ReaderThread-" + via);
        readerThread.start();
        System.out.println("Conectado con Servidor de " + via + "!");
    }

    public void send(String message) throws IOException {
        this.dout.writeUTF(message);
        this.dout.flush();
    }

    public void kill() throws IOException {
        this.dout.close();
        this.socket.close();
    }
}
