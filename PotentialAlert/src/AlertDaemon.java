import db.DatabaseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;

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

    private int databaseState;
    private boolean keepScheduling = true;

    private static int pulseMax = 150;
    private static int pulseMinimum = 50;

    public AlertDaemon(){
        this.handler = new DatabaseHandler();
        this.mailHandler = new SMTPHandler();
    }

    @Override
    public void run() {
       // this.timer.schedule();
    }

    private void scheduleAndPoll(){

    }

    private void findAlerts(){

    }

    private void findSheepsByPulse(){

    }

    public static void main(String[] args){

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

        private String hasArgument(String[] args, String argument){
            for(String arg : args){
                if(arg.split(" ")[0].equals(argument))
                    return arg;
            }
            return null;
        }

        public void decryptAndExecute(String[] args){
            args[0] = args[0].toLowerCase();
            args[0] = args[0].replace(" ", "");
            if(args[0].equals("exit") || args[0].equals("close")){
                AlertDaemon.this.keepScheduling = false;
                System.exit(0);
            }

        }

    }
    public static void p(String s){
        System.out.println(s);
    }
}
