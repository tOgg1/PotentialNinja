import util.Log;
import util.PotentialNinjaException;
import util.SMTPHandler;
import util.ServerInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MailServer {

    private ServerSocket serverSocket;
    private ArrayList<ConnectionThread> connections;
    private SMTPHandler mailHandler;

    private boolean running;

    private static String configname = "config.txt";

    public MailServer(){
        try {
            Log.initLogFile();

            initSMTP();
            this.connections = new ArrayList<ConnectionThread>();
            serverSocket = new ServerSocket(ServerInfo.port);
            this.run();
            Log.d("Server", "Server running on port: " + ServerInfo.port);
        } catch (IOException | PotentialNinjaException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
    }

    private void initSMTP(){
        File file = new File(this.configname);
        String email_account;
        String email_password;
        int email_password_factory;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String account = reader.readLine();

            if(account == null){
                throw new RuntimeException("Error reading ´config.txt´: Missing account");
            }
            else{
                email_account = account;
            }

            String password = reader.readLine();

            if(password == null){
                throw new RuntimeException("Error reading ´config.txt´: Missing password");
            }else{
                email_password = password;
            }

            String factoryCode = reader.readLine();

            if(factoryCode == null){
                throw new RuntimeException("Error reading ´config.txt´: Missing factory code");
            }
            else{
                try{
                    email_password_factory = Integer.parseInt(factoryCode);
                }catch(NumberFormatException e){
                    throw new RuntimeException("Error reading ´config.txt´: Invalid factory code");
                }
            }

            this.mailHandler = new SMTPHandler(email_account, email_password, email_password_factory);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File ´config.txt´ not found, or is corrupted");
        } catch (IOException e) {
            throw new RuntimeException("Error reading ´config.txt´");
        }
    }

    private void close() throws Exception{
        for(ConnectionThread c : connections){
            c.close();
        }
        this.serverSocket.close();
    }

    private void run() throws IOException{
        this.running = true;
        InputManager iManage = new InputManager();
        iManage.start();


        Socket socket;
        Log.d("Server", "Accepting connections...");
        while((socket = serverSocket.accept()) != null){
            Log.d("Server", "Connection received from " + socket.getInetAddress().getHostAddress());
            ConnectionThread connection = new ConnectionThread(socket);
            connection.start();
            connections.add(connection);
        }
        this.running = false;
    }

    private boolean sendEmail(ConnectionThread thread)throws PotentialNinjaException{
        if(thread.message == null || thread.recipient == null || thread.subject == null){
            throw new PotentialNinjaException("All email info was not set before email was flagged for sending");
        }

        if(this.mailHandler.sendMail(thread.recipient, thread.message, thread.subject)){
            Log.d("Mail", thread.recipient + ", " + thread.message + ", " + thread.subject);
            this.connections.remove(thread);
            return true;
        }else{
            return false;
        }
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
        }

        public void close() throws Exception{
            this.socket.close();
            this.is.close();
            this.os.close();
        }

        @Override
        public void run() {
            Log.d("Server", "Running connectionthread");
            this.begin = false;
            try {
                is = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                os = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("Server","Attempting to read");
            char[] buffer = new char[0xFFF];
            try {
                while(is.read(buffer) != -1){
                    Log.d("Server", "Got message");
                    int status = decryptMessage(buffer);
                    if(status == 0){
                        break;
                    }

                    if(status == -1){
                        os.write(ServerInfo.code_error);
                    }else if(status == -2){
                        os.write(ServerInfo.code_invalid);
                    }else{
                        os.write(ServerInfo.code_ok);
                    }
                    os.flush();
                    buffer = new char[0xFFF];
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int decryptMessage(char[] buffer){
            try{
                int sendCode = Integer.parseInt(buffer[0]+"");
                Log.d("Server", "Code: " + sendCode);

                if(sendCode == ServerInfo.code_begin){
                    Log.d("Server", "Code begin registered");
                    begin = true;
                    return 1;
                }else if(sendCode == ServerInfo.code_end){
                    Log.d("Server", "Code end registered");
                    begin = false;
                    try {
                        if(MailServer.this.sendEmail(this)){
                            this.close();
                            return 1;
                        }else{
                            return -1;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return 0;
                }else if(sendCode == ServerInfo.code_message){
                    Log.d("Server", "Code message registered");

                    if(buffer == null || !begin){
                        return -1;
                    }
                    Log.d("Mail", new String(buffer).substring(1));
                    this.message = new String(buffer).substring(1);
                    return 1;
                }else if(sendCode == ServerInfo.code_recipient){
                    Log.d("Server", "Code recipient registered");

                    if(buffer == null || !begin){
                        return -1;
                    }
                    Log.d("Mail", new String(buffer).substring(1));
                    this.recipient = new String(buffer).substring(1);
                    return 1;
                }else if(sendCode == ServerInfo.code_subject){
                    Log.d("Server", "Code subject registered");

                    if(buffer == null|| !begin){
                        return -1;
                    }
                    Log.d("Mail", new String(buffer).substring(1));
                    this.subject = new String(buffer).substring(1);
                    return 1;

                }else{
                    return -2;
                }
            }catch(NumberFormatException e){
                e.printStackTrace();
                return -1;
            }
        }
    }

    private class InputManager extends Thread{

        private BufferedReader input;

        private String mStartUp = "Command Parser v1.0 for PotentialServer\nCreated by tOgg1\n";
        private String mReady = "Ready for commands ...";
        private final boolean debug = true;

        public InputManager(){
            p(mStartUp);
            input = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run(){
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
                MailServer.this.running = true;

                try{
                    MailServer.this.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.exit(0);
            }else{
                p("Invalid command: " + args[0]);
            }
        }

    }
    public static void p(String s){
        System.out.println(s);
    }


    public static void main(String[] args){
        MailServer server = new MailServer();
    }
}
