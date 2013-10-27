import db.DatabaseHandler;
import model.Alarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/24/13
 * Time: 9:41 AM
 *
 * This class both fetches and stores alerts, and handles them approprietly.
 */
public class AlertDaemon extends Thread {

    private Timer timer;
    private DatabaseHandler handler;
    private SMTPHandler mailHandler;

    private boolean keepScheduling = true;

    private static String[] template_message = {"This is a message alerting you that sheep ", " is ", ". The alarm triggered at " + "."};
    private static String[] template_message_causes = {"dead. Please login to SheepTracker for more information", "is registered with unusually low pulse", "is registered with unusually high pulse"};

    private static String email_subject = "SheepTracker Alarm Notification";

    private SimpleDateFormat sformat;
    private SimpleDateFormat lformat;

    public AlertDaemon(){
        this.handler = new DatabaseHandler();
        this.mailHandler = new SMTPHandler();
        this.sformat = new SimpleDateFormat("MM/DD");
        this.lformat = new SimpleDateFormat("hh:mm:ss a");
        this.timer = new Timer();
    }

    @Override
    public void run(){
        InputManager iManage = new InputManager();
        iManage.start();
        scheduleAndPoll();

    }

    private void scheduleAndPoll(){
        if(!keepScheduling)
            return;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
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
                    try {
                        int sheepid = alarm.getSheepID();
                        int flags = alarm.getAlarmFlags();
                        int farmerid = handler.getSheepOwner(sheepid);
                        Date now = new Date();
                        String email_subject = AlertDaemon.email_subject + ": sheep with id " + sheepid + ", " + this.sformat.format(now);
                        String email_message = template_message[0] + sheepid + template_message[1] + template_message_causes[1] + lformat.format(now) + template_message_causes[2];

                        Object[] contactInf = handler.getFarmerContactInformation(farmerid);

                        if(contactInf != null)
                        {
                            String email = (String)contactInf[2];
                            mailHandler.sendMail(email, email_message, email_subject);

                        }
                        String farmerEmail = handler.getFarmerEmail(farmerid);
                        mailHandler.sendMail(farmerEmail, email_message, email_subject);

                    } catch (SQLException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
    }

    private ArrayList<Alarm> findAlarms(){
        try {
            return handler.getAllActiveAlarms();
        } catch (SQLException e) {
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
                        p("Argument length: " + args.length);
                        System.out.print("\nArguments: ");
                        for(String s : args){
                            System.out.print(s + " ");
                        }
                        System.out.print("\n");
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
                p("Invalid command" + args[0]);
            }
        }

    }
    public static void p(String s){
        System.out.println(s);
    }
}
