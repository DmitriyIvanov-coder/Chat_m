package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private String socketName;

    public String getSocketName() {
        return socketName;
    }

    public Socket getSocket() {
        return socket;
    }

    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler( Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        socketName = String.valueOf(socket.getRemoteSocketAddress());


        Thread t1 = new Thread(()->{
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                while (true){
                    String echo = in.readUTF();
                    server.sendMsg(echo, getSocketName());
//                    out.writeUTF("Client"+socket.getRemoteSocketAddress()+": "+echo);

//                    System.out.println("Client"+socket.getRemoteSocketAddress()+": "+echo);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        t1.start();

        /*Thread t2 = new Thread(()->{
            try {
                while (true){
                    in = new DataInputStream(socket.getInputStream());
                    String str = in.readUTF();
                    if (str.equals("/end")){
                        System.out.println("client disconnected");
                        out.writeUTF("/end");
                        break;
                    }

                    System.out.println("Client: "+str);
//                out.writeUTF("ECHO: "+str);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        t2.start();*/

    }
}
