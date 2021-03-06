package net;

import util.GeneralUtil;
import util.PotentialNinjaException;
import util.ServerInfo;

import java.io.*;
import java.net.Socket;

public class MailClient {
    private Socket socket;

    private BufferedReader is;
    private BufferedWriter os;

    private boolean running;

    public MailClient(){
        this.running = false;
        try {
            //socket = new Socket("127.0.0.1", ServerInfo.port);
            socket = new Socket(ServerInfo.decryptIp(ServerInfo.ipenc), ServerInfo.port);
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() throws Exception{
        if(this.running)
            return;
        this.running = true;
        this.is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void sendMail(String recipient, String message, String subject) throws Exception{

        if(!this.running)
            throw new IOException("Socket not running");

        if(!GeneralUtil.isEmailValid(recipient))
            throw new PotentialNinjaException(recipient + " is not a valid email.");

        char[] buffer = new char[0xF];

        this.os.write(ServerInfo.code_begin+"");
        this.os.flush();


        if(is.read(buffer) != -1){
            if(buffer[0] != ServerInfo.code_ok){
                throw new IOException("Server_begin rejected: " + buffer[0]);
            }
        }

        char[] recipientBuffer = (ServerInfo.code_recipient+""+recipient).toCharArray();
        char[] messageBuffer = (ServerInfo.code_message+""+message).toCharArray();
        char[] subjectBuffer = (ServerInfo.code_subject+""+subject).toCharArray();


        this.os.write(recipientBuffer);
        this.os.flush();

        if(is.read(buffer) != -1){
            if(buffer[0] != ServerInfo.code_ok){
                throw new IOException("Server_recipient rejected: " + buffer[0]);
            }
        }


        this.os.write(messageBuffer);
        this.os.flush();

        if(is.read(buffer) != -1){
            if(buffer[0] != ServerInfo.code_ok){
                throw new IOException("Server_message rejected: " + buffer[0]);
            }
        }



        this.os.write(subjectBuffer);
        this.os.flush();

        if(is.read(buffer) != -1){
            if(buffer[0] != ServerInfo.code_ok){
                throw new IOException("Server_subject rejected: " + buffer[0]);
            }
        }


        this.os.write(ServerInfo.code_end+"");
        this.os.flush();

        if(is.read(buffer) != -1){
            if(buffer[0] != ServerInfo.code_ok){
                throw new IOException("Error sending mail: " + buffer[0]);
            }
        }
    }
}
