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
                    registerClient(authMode);
                }else if (authMode.startsWith("SI ")){
                    if (checkClientData(authMode)){
                        break;
                    }
                } else if (authMode.startsWith("Ch ")) {
                    changeLogin(authMode);
                }else {
                    System.out.println("client disconnected");
                    break;
                }
            }
        }catch (IOException | SQLException e){
            e.printStackTrace();
        }

    }

    public void registerClient(String inMsg) throws IOException, SQLException {
        String login;
        String password;

        String[] clientData = inMsg.split(" ", 4);
        login = clientData[1];

        password = clientData[3];

        if (dataBaseClients.checkExists(login)){
            //если логин не сущетвует
            dataBaseClients.addClient(login,password);
            out.writeBoolean(true);
        }else {
            //если логин уже существует
            out.writeBoolean(false);
        }
    }

    public boolean checkClientData(String inMsg) throws IOException, SQLException {

        String login;
        String password;

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

    public void changeLogin(String inMsg) throws SQLException, IOException {
        String login;
        String password;
        String newLogin;

        String[] clientData = inMsg.split(" ", 4);
        login = clientData[1];
        password = clientData[2];
        newLogin = clientData[3];
        if (dataBaseClients.checkAuthorization(login, password)){
            if (dataBaseClients.checkExists(newLogin)){
                out.writeInt(0);
                dataBaseClients.changeLogin(login,newLogin);
//                dataBaseClients.readDB();
            }else {
                out.writeInt(2);
            }
        } else {
            out.writeInt(1);
        }
    }
}
