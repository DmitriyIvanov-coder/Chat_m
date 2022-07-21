package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    static ServerSocket server;
    private Socket socket;
    static final int PORT = 8189;

    static List<ClientHandler> clientHandlerList = new ArrayList<>();


    public Server () {
        try {
            server = new ServerSocket(PORT);
            System.out.println("server started");
            while (true){
                socket=server.accept();
                System.out.println("client connected");
                clientHandlerList.add(new ClientHandler( this, socket));

            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                server.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMsg( String msg, String name) throws IOException {
        DataOutputStream out;
        for (ClientHandler c :clientHandlerList) {
            out=new DataOutputStream(c.getSocket().getOutputStream());
            out.writeUTF("Client"+name+": "+msg);
        }
    }
}
