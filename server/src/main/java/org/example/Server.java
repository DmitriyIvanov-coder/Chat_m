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
    static List<RegisteredClient> registeredClientList = new ArrayList<>();
    static DataInputStream in;
    static DataOutputStream out;


    public Server () {
        try {
            server = new ServerSocket(PORT);
            System.out.println("server started");
            while (true){
                socket=server.accept();
                System.out.println("client connected");
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                new AuthThread(this,socket, out, in).run();

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

    public void removeFromClientHandlerList(ClientHandler clientHandler){
        System.out.println(clientHandlerList);
        clientHandlerList.remove(clientHandler);
        System.out.println("ClientHandler removed");
        System.out.println(clientHandlerList);
    }
    public boolean registerClient(Socket socket) throws IOException {
        while (true){
            String inMsg = in.readUTF();
            if (!inMsg.equals("0")){
                String login;
                String password;
                String nickname;

                boolean reg = true;

                String[] clientData = inMsg.split(" ", 3);
                login = clientData[0];
                password = clientData[1];
                nickname = clientData[2];

                for (RegisteredClient rc:registeredClientList) {
                    if (rc.getLogin().equals(login)){
                        reg = false;
                    }
                }
                if (reg){
                    registeredClientList.add(new RegisteredClient(login, password, nickname));
                    out.writeBoolean(true);
                    return true;
                }else {
                    out.writeBoolean(false);
                }
            }else {
                return false;
            }
        }
    }

    public boolean checkClientData(Socket socket) throws IOException {
        while (true) {
            String inMsg = in.readUTF();
            if (!inMsg.equals("0")) {
                String login;
                String password;
                boolean next = false;

                String[] clientData = inMsg.split(" ");
                login = clientData[0];
                password = clientData[1];
                for (RegisteredClient rc : registeredClientList) {
                    if (rc.getLogin().equals(login) && rc.getPassword().equals(password)) {
                        next = true;
                        break;
                    }
                }
                if (next) {
                    out.writeBoolean(true);
                    return true;
                }else
                    out.writeBoolean(false);


            } else {
                return false;
            }
        }
    }
}
