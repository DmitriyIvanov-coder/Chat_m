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

    public void sendMsg( String msg, String name, DataOutputStream toSender) throws IOException {
        DataOutputStream out;
        if (msg.isEmpty()||msg.trim().length()==0){
            toSender.writeUTF("System: Нельзя отправлять пустое сообщение");
        }else {
            for (ClientHandler c :clientHandlerList) {
                out=new DataOutputStream(c.getSocket().getOutputStream());
                out.writeUTF(name+": "+msg);
            }
        }

    }

    public void sendPrivateMsg(String msg, String name, DataOutputStream toSender) throws IOException {
        DataOutputStream out;
        String[] privateMsg = msg.split(" ", 3);
        if (privateMsg.length<2){
            toSender.writeUTF("System: Укажите получателя");
        } else
        if (privateMsg.length != 3){
            toSender.writeUTF("System: Нельзя отправлять пустое сообщение");
        }else if (privateMsg[2].trim().length()==0||privateMsg[2].isEmpty()){
            toSender.writeUTF("System: Нельзя отправлять пустое сообщение");
        }else {


                String nickAddress = privateMsg[1];
                boolean isExist = false;
                for (ClientHandler c :clientHandlerList) {

                    if (c.getNickName().equals(nickAddress)){
                        out=new DataOutputStream(c.getSocket().getOutputStream());
                        out.writeUTF("(P) "+name+": "+privateMsg[2]);
                        isExist=true;
                        break;
                    }
                }
                if (isExist){
                    toSender.writeUTF("(to"+nickAddress+") "+name+": "+privateMsg[2]);
                }
        }
    }

    public void removeFromClientHandlerList(ClientHandler clientHandler){
        System.out.println(clientHandlerList);
        clientHandlerList.remove(clientHandler);
        System.out.println("ClientHandler removed");
        System.out.println(clientHandlerList);
    }
    public boolean registerClient(Socket socket,  String inMsg) throws IOException {
//            String inMsg = in.readUTF();
                String login;
                String password;
                String nickname;

                boolean reg = true;

                String[] clientData = inMsg.split(" ", 4);
                login = clientData[1];
                nickname = clientData[2];
                password = clientData[3];

                for (RegisteredClient rc:registeredClientList) {
                    if (rc.getLogin().equals(login)){
                        reg = false;
                    }
                }
                if (reg){
                    registeredClientList.add(new RegisteredClient(login, password, nickname));
                    out.writeBoolean(true);
                    clientHandlerList.add(new ClientHandler( this, socket, nickname));
                    return true;
                }else {
                    out.writeBoolean(false);
                    return false;
                }


    }

    public boolean checkClientData(Socket socket, String inMsg) throws IOException {

                String login;
                String password;
                String nickname = null;
                boolean next = false;

                String[] clientData = inMsg.split(" ", 3);
                login = clientData[1];
                password = clientData[2];
                for (RegisteredClient rc : registeredClientList) {
                    if (rc.getLogin().equals(login) && rc.getPassword().equals(password)) {
                        next = true;
                        nickname = rc.getNickname();
                        break;
                    }
                }
                if (next) {
                    if (!isClientOnline(nickname)){
                        clientHandlerList.add(new ClientHandler( this, socket, nickname));
                        out.writeUTF("0");
                        out.writeUTF(nickname);
                        return true;
                    }else {
                        out.writeUTF("2");
                        return false;
                    }
                }else {
                    out.writeUTF("1");
                    return false;
                }

    }

    public boolean isClientOnline(String nickname){
        for (ClientHandler list : clientHandlerList) {
            if (list.getNickName().equals(nickname)){
                return true;
            }
        }
        return false;
    }
}
