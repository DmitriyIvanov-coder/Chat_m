package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    static ServerSocket server;
    static Socket socket;
    static final int PORT = 8189;
    static DataInputStream in;
    static DataOutputStream out;



    public static void main(String[] args) {
//        Hello world!
//        some new words
        try {
            server = new ServerSocket(PORT);
            System.out.println("server started");
            socket=server.accept();
            System.out.println("client connected");


            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);

            Thread t1 = new Thread(()->{
                while (true){
                    while (true){
                        String msgOut =  sc.nextLine();
                        try {
                            out.writeUTF(msgOut);
                            System.out.println("Server: "+msgOut);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            t1.setDaemon(true);
            t1.start();



            while (true){
                String str = in.readUTF();
                if (str.equals("/end")){
                    System.out.println("client disconnected");
                    out.writeUTF("/end");
                    break;
                }

                System.out.println("Client: "+str);
//                out.writeUTF("ECHO: "+str);
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
}
