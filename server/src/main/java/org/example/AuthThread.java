package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AuthThread implements Runnable{
    Server server;
    Socket socketClient;
    private DataOutputStream out;
    private DataInputStream in;

    public AuthThread(Server server, Socket socketClient, DataOutputStream out, DataInputStream in) {
        this.server = server;
        this.socketClient = socketClient;
        this.out = out;
        this.in = in;
    }


    @Override
    public void run() {
        try {
            while (true){
                if (in.readBoolean()){
                    if (server.registerClient(socketClient)){
                        break;
                    }
                }else {
                    if (server.checkClientData(socketClient)){
                        break;
                    }
                }
            }
            server.clientHandlerList.add(new ClientHandler( server, socketClient));
        }catch (IOException e){
            e.printStackTrace();
        }
//        finally {
//            try {
//                socketClient.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }
}
