package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class AuthThread implements Runnable{
    Server server;
    Socket socketClient;
    private DataOutputStream out;
    private DataInputStream in;

    private DataBaseClients dataBaseClients;

    public AuthThread(Server server, Socket socketClient, DataOutputStream out, DataInputStream in, DataBaseClients dataBaseClients) {
        this.server = server;
        this.socketClient = socketClient;
        this.out = out;
        this.in = in;
        this.dataBaseClients = dataBaseClients;
    }


    @Override
    public void run() {
        try {
            while (true){
                String authMode = in.readUTF();
                if (authMode.startsWith("R ")){
                    if (registerClient(authMode)){
                        break;
                    }
                }else if (authMode.startsWith("Ch ")){
                    if (checkClientData(authMode)){
                        break;
                    }
                }
            }
        }catch (IOException | SQLException e){
            e.printStackTrace();
        }

    }

    public boolean registerClient(String inMsg) throws IOException, SQLException {
        String login;
        String password;
        String nickname;

        boolean reg = true;

        String[] clientData = inMsg.split(" ", 4);
        login = clientData[1];
        nickname = clientData[2];
        password = clientData[3];


        if (dataBaseClients.checkExists(login)){
            //если логин не сущетвует
            dataBaseClients.addClient(login,password);
            out.writeBoolean(true);
            return true;
        }else {
            //если логин уже существует
            out.writeBoolean(false);
            return false;
        }


    }

    public boolean checkClientData(String inMsg) throws IOException, SQLException {

        String login;
        String password;
        String nickname = null;
        boolean next = false;

        String[] clientData = inMsg.split(" ", 3);
        login = clientData[1];
        password = clientData[2];

        if (dataBaseClients.checkAuthorization(login, password)) {
            if (!server.isClientOnline(login)){
                server.clientHandlerList.add(new ClientHandler( server, socketClient, login));
                out.writeUTF("0");
                out.writeUTF(login);
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
}
