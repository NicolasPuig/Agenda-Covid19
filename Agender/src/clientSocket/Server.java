package clientSocket;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
//    public static void main(String[] args) {
//        try {
//            System.out.println("Creando puerto de Servidor...");
//            ServerSocket ss = new ServerSocket(5500);
//            System.out.println("Conectando...");
//            Socket s = ss.accept();
//            System.out.println("Conectado con cliente");
//            DataInputStream din = new DataInputStream(s.getInputStream());
//            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
//            int n = 1;
//            while (true) {
//                String inStr = din.readUTF();
//                System.out.println(">>> " + inStr);
//                String outStr = "Solicitud de Agenda " + n++;
//                System.out.println("<<< " + outStr);
//                dout.writeUTF(outStr);
//                dout.flush();
//            }
//        } catch (IOException e) {
//            System.out.println("Conexion cerrada");
//            System.out.println(e.getMessage());
//        }
//    }
}
