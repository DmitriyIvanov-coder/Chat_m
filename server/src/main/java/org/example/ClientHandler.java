package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private String socketName;
    private String nickName;

    public String getSocketName() {
        return socketName;
    }

    public Socket getSocket() {
        return socket;
    }

    private DataInputStream in;
//    private DataOutputStream out;


    public ClientHandler(Server server, Socket socket, String nickName) {
        this.server = server;
        this.socket = socket;
        this.nickName = nickName;


    Thread t1 = new Thread(()->{
            try {
                in = new DataInputStream(socket.getInputStream());
//                out = new DataOutputStream(socket.getOutputStream());

                while (true){
                    String echo = in.readUTF();
                    if (echo.equals("//end")){
                        System.out.println("Client"+socket.getRemoteSocketAddress()+": "+echo);
                        break;
                    }
                    System.out.println("Client"+socket.getRemoteSocketAddress()+": "+echo);
                    server.sendMsg(echo, nickName);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    server.removeFromClientHandlerList(this);
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        t1.start();

    }
}
