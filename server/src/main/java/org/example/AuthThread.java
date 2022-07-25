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
                String authMode = in.readUTF();
                if (authMode.startsWith("R ")){
                    if (server.registerClient(socketClient, authMode)){
                        break;
                    }
                }else if (authMode.startsWith("Ch ")){
                    if (server.checkClientData(socketClient, authMode)){
                        break;
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
