package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static ServerSocket server;
    private Socket socket;
    static final int PORT = 8189;

    DataBaseClients dataBaseClients = new DataBaseClients();

    static List<ClientHandler> clientHandlerList = new ArrayList<>();
    List<String> onlineClientsNicks = new ArrayList<>();
    static DataInputStream in;
    static DataOutputStream out;

    ExecutorService executorService;


    public Server () throws SQLException, ClassNotFoundException {
        try {
            executorService = Executors.newCachedThreadPool();
            server = new ServerSocket(PORT);
            System.out.println("server started");
            while (true){
                socket=server.accept();
                System.out.println("client connected");
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                executorService.execute(new AuthThread(this,socket, out, in, dataBaseClients));
//                executorService.shutdown();
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
            if (name.equals(nickAddress)){
                toSender.writeUTF("System: Нельзя отправлять сообщения самому себе");
            }else {
                boolean isExist = false;
                for (ClientHandler c :clientHandlerList) {
                    if (c.getNickName().equals(name)){

                        continue;
                    } else{
                        if (c.getNickName().equals(nickAddress)){
                            out=new DataOutputStream(c.getSocket().getOutputStream());
                            out.writeUTF("(P) "+name+": "+privateMsg[2]);
                            isExist=true;
                            break;
                        }
                    }
                }
                if (isExist){
                    toSender.writeUTF("(to"+nickAddress+") "+name+": "+privateMsg[2]);
                }
            }

        }
    }

    public void removeFromClientHandlerList(ClientHandler clientHandler){
        System.out.println(clientHandlerList);
        clientHandlerList.remove(clientHandler);
        System.out.println("ClientHandler removed");
        System.out.println(clientHandlerList);
    }

    public boolean isClientOnline(String nickname){
        for (ClientHandler list : clientHandlerList) {
            if (list.getNickName().equals(nickname)){
                return true;
            }
        }
        return false;
    }

    public void sendOnlineClients() throws IOException {
        for (ClientHandler ch:clientHandlerList) {
            onlineClientsNicks.add(ch.getNickName());
        }
        for (ClientHandler ch:clientHandlerList) {
            ch.getOut().writeUTF("//");
            OutputStream outputStream = ch.getSocket().getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(onlineClientsNicks);
        }
        onlineClientsNicks.clear();
    }
}
