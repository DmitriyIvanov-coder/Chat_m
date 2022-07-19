package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class client
{
    static Socket socket;
    static final int PORT = 8189;
    static final String IP_ADDRESS = "localhost";

    static DataInputStream in;
    static DataOutputStream out;


    public static void main( String[] args )
    {
        try {
            socket = new Socket(IP_ADDRESS,PORT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);
            Thread t1 =new Thread(()->{
               while (true){
                   while (true){
                       String msgOut =  sc.nextLine();
                       try {
                           out.writeUTF(msgOut);
                           System.out.println("Client: "+msgOut);

                       } catch (IOException e) {
                           throw new RuntimeException(e);
                       }
                   }
               }
            });
            t1.setDaemon(true);
            t1.start();

            while (true){
                String msgIn = in.readUTF();
                if (msgIn.equals("/end")){
                    System.out.println("server disconnect us");
                    out.writeUTF("/end");
                    break;
                }
//                System.out.println(msgIn);
                System.out.println("Server: "+msgIn);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                in.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
