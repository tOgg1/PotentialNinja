import db.DatabaseHandler;
import util.Log;
import util.PotentialNinjaException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/31/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmailServer {

    private ServerSocket serverSocket;
    private ArrayList<ConnectionThread> connections;
    private DatabaseHandler mHandler;

    private boolean running;

    public EmailServer(){
        try {
            Log.initLogFile();
            serverSocket = new ServerSocket(ServerInfo.port);
            this.run();
        } catch (IOException | PotentialNinjaException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
    }

    private void run() throws IOException{
        this.running = true;
        InputManager iManage = new InputManager();
        iManage.start();

        Socket socket;
        while((socket = serverSocket.accept()) != null){
            ConnectionThread connection = new ConnectionThread(socket);
            connection.start();
            connections.add(connection);
        }
        this.running = false;
    }

    private void sendEmail(ConnectionThread thread){


        this.connections.remove(thread);
    }

    private class ConnectionThread extends Thread{
        private Socket socket;

        private BufferedReader is;
        private BufferedWriter os;

        private boolean begin;

        public String message = null;
        public String recipient = null;
        public String subject = null;

        public ConnectionThread(Socket socket){
            this.socket = socket;
            try {
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        @Override
        public void run() {
            this.begin = false;
            char[] buffer = new char[0xFF];
            try {
                while(is.read(buffer) != 0){
                    decryptMessage(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        private void decryptMessage(char[] buffer){
            try{
                int sendCode = Integer.parseInt(new String(buffer[0] +""+ buffer[1]));
                if(sendCode == ServerInfo.code_begin){
                    begin = true;
                    return;
                }else if(sendCode == ServerInfo.code_end){

                }else if(sendCode == ServerInfo.code_message){

                }
                else if(sendCode == ServerInfo.code_message){

                }

            }catch(NumberFormatException e){
                e.printStackTrace();
            }
        }
    }

    private class InputManager extends Thread{

        private BufferedReader input;

        private String mStartUp = "Command Parser v1.0 for AlertDaemon\nCreated by tOgg1\n";
        private String mReady = "Ready for commands ...";
        private final boolean debug = true;

        public InputManager(){
            p(mStartUp);
            input = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {
            try {
                String string;
                p(mReady);

                while((string = input.readLine()) != null){
                    String args[] = string.split("-");
                    if(debug){
                        for(String s : args){
                            System.out.print(s + " ");
                        }
                    }
                    decryptAndExecute(args);
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        public void decryptAndExecute(String[] args){
            args[0] = args[0].toLowerCase();
            args[0] = args[0].replace(" ", "");
            if(args[0].equals("exit") || args[0].equals("close")){
                EmailServer.this.running = true;
                System.exit(0);
            }
            else{
                p("Invalid command: " + args[0]);
            }
        }

    }
    public static void p(String s){
        System.out.println(s);
    }


    public static void main(String[] args){

    }
}
