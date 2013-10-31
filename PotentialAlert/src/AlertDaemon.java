import db.DatabaseHandler;
import model.Alarm;
import util.Log;
import util.PotentialNinjaException;
import util.SMTPHandler;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlertDaemon extends Thread {

    private Timer timer;
    private DatabaseHandler handler;
    private SMTPHandler mailHandler;

    private boolean keepScheduling = true;

    private static String[] template_message = {"This is an email notifying you that sheep ", ". The alarm triggered at ", "."};
    private static String[] template_message_causes = {" is dead. Please login to SheepTracker for more information", " is registered with unusually low pulse", " is registered with unusually high pulse"};

    private static String email_subject = "SheepTracker Alarm Notification";

    private static String configname = "config.txt";

    private SimpleDateFormat sformat;
    private SimpleDateFormat lformat;

    public AlertDaemon(){
        this.handler = new DatabaseHandler();
        initSMTP();
        this.sformat = new SimpleDateFormat("MM/DD");
        this.lformat = new SimpleDateFormat("hh:mm:ss a");
        this.timer = new Timer();
        try {
            Log.initLogFile();
        } catch (PotentialNinjaException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

    @Override
    public void run(){
        InputManager iManage = new InputManager();
        iManage.start();
        scheduleAndPoll();
    }

    private void scheduleAndPoll(){
        if(!keepScheduling){
            return;
        }
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                scheduleAndPoll();
            }
        };
        this.timer.schedule(task, 60000);
        poll();
    }

    private void poll(){
        ArrayList<Alarm> alarms = findAlarms();

        if(alarms != null){
            if(!alarms.isEmpty()){
                for(Alarm alarm : alarms){
                    try{
                        int sheepid = alarm.getSheepID();
                        String sheepname = handler.getSheepName(sheepid);
                        sheepname = (sheepname != null ? sheepname : "(internal id)" + sheepid);
                        int flags = alarm.getAlarmFlags();
                        int farmerid = handler.getSheepOwner(sheepid);
                        Date now = new Date();
                        String email_subject = AlertDaemon.email_subject + ": Sheep with id " + sheepname  + ", " + this.sformat.format(now);
                        String email_message = template_message[0] + sheepname + template_message_causes[flags-1] + template_message[1] + lformat.format(now) + template_message[2];

                        Object[] contactInf = handler.getFarmerContactInformation(farmerid);

                        if(contactInf != null){
                            String contactEmail = (String)contactInf[2];
                            mailHandler.sendMail(contactEmail, email_message, email_subject);
                            Log.d("Email", "Email successfully sent to " + contactEmail);
                        }
                        String farmerEmail = handler.getFarmerEmail(farmerid);
                        mailHandler.sendMail(farmerEmail, email_message, email_subject);
                        Log.d("Email", "Email successfully sent to " + farmerEmail);

                        handler.inactiveAlarm(alarm.getAlarmID());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
        Log.d("Alert", "Polling complete");
    }

    private ArrayList<Alarm> findAlarms(){
        try {
            return handler.getAllActiveAlarms();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args){
        AlertDaemon alert = new AlertDaemon();
        alert.start();
    }

    class InputManager extends Thread{

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
                AlertDaemon.this.keepScheduling = false;
                System.exit(0);
            }
            else if(args[0].equals("poll")){
                AlertDaemon.this.poll();
            }
            else{
                p("Invalid command: " + args[0]);
            }
        }

    }
    public static void p(String s){
        System.out.println(s);
    }
}
