import db.DatabaseHandler;
import model.Sheep;
import util.FlagData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/12/13
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class FateDaemon extends Thread {
    private DatabaseHandler handler;
    private SheepDaemon sheepThread;
    private WolfDaemon wolfThread;

    private Timer timer;
    private boolean keepScheduling;

    int databaseState;


    public FateDaemon(){
        handler = new DatabaseHandler();

        databaseState = getDBState(handler);
        sheepThread = new SheepDaemon(handler);
        wolfThread = new WolfDaemon(handler);
        reFetchAllData();
        keepScheduling = false;
    }

    public static int getDBState(DatabaseHandler handler){
        try {
            return handler.getState();
        } catch (SQLException e) {
            return 0;
        }
    }

    public void reFetchAllData(){
        try {
            ArrayList<Sheep> sheeps = handler.getAllSheeps();
            this.cascadeData(sheeps);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void cascadeData(ArrayList<Sheep> sheeps){
        sheepThread.updateSheeps(sheeps);
        wolfThread.updateSheeps(sheeps);
    }

    @Override
    public void run() {
        this.sheepThread.start();
        this.wolfThread.start();

        this.keepScheduling = true;

        this.timer = new Timer("FateTimer", false);
        scheduleAndUpdate();
        InputManager iManage = new InputManager();
        iManage.start();
    }

    private void scheduleAndUpdate(){
        if(keepScheduling){
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    scheduleAndUpdate();
                }
            };
            this.timer.schedule(task, 10000);
        }
        int newState = getDBState(this.handler);

        if(newState != this.databaseState){
            this.reFetchAllData();
        }
    }

    public static void main(String[] args){
        FateDaemon daemon = new FateDaemon();
        daemon.start();
    }

    class InputManager extends Thread{

        private BufferedReader input;

        private String mStartUp = "Command Parser v1.0 for fateDaemon Software\nCreated by tOgg1\n";
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
                FateDaemon.this.keepScheduling = false;
                System.exit(0);
            }
            else if(args[0].equals("kill")){
                int id = -1, cause = -1;

                if(args.length > 1){
                    String argv;

                    if((argv = hasArgument(args, "cause")) != null){
                        p(argv);
                        String[] argc = argv.split(" ");
                        if(argc.length == 1){
                            p("Missing value for argument \"cause\"");
                            return;
                        }
                        if(argc[1].equals("wolf"))
                            cause = FlagData.DEATHBYWOLF;
                        else if(argc[1].equals("fall"))
                            cause = FlagData.DEATHBYFALL;
                        else if(argc[1].equals("disease"))
                            cause = FlagData.DEATHBYDISEASE;
                        else if(argc[1].equals("human"))
                            cause = FlagData.DEATHBYHUMAN;
                        else if(argc[1].equals("sheep"))
                            cause = FlagData.DEATHBYSHEEP;
                        else
                            cause = FlagData.DEATHUNKNOWN;
                    }

                    if((argv = hasArgument(args, "id")) != null){
                        id = 0;
                        String[] argc = argv.split(" ");
                        if(argc.length < 2){
                            p("Missing id for command \"kill -id\"");
                            return;
                        }
                        try{
                            p(argv);
                            id = Integer.parseInt(argc[1]);
                        }catch(Throwable t){
                            p("Invalid id for command \"kill -id\"");
                            return;
                        }
                    }

                    if(id != -1 && cause != -1){
                        FateDaemon.this.wolfThread.doAttack(id, cause);
                        if(debug)
                            p("Command Executed: kill -id -cause");
                        return;
                    }
                    if(id != -1){
                        FateDaemon.this.wolfThread.doAttackById(id);
                        if(debug)
                            p("Command Executed: kill -id");

                        return;
                    }

                    if(cause != -1){
                        FateDaemon.this.wolfThread.doAttack(cause);
                        if(debug)
                            p("Command Executed: kill -cause");
                        return;
                    }
                    p("Invalid arguments for command \"kill\"");
                }
                else{
                    FateDaemon.this.wolfThread.doAttack();
                }
            }
            else if(args[0].equals("move")){
                if(args.length > 1){
                    String argv;
                    if((argv = hasArgument(args, "all")) != null){
                        FateDaemon.this.sheepThread.moveSheeps();
                        if(debug)
                            p("All sheeps moved");
                    }
                    else if((argv = hasArgument(args, "id")) != null){
                        String[] argc = argv.split(" ");
                        if(argc.length != 2){
                            p("Invalid arguments for command \"move\"");
                            return;
                        }
                        int id = 0;
                        try{
                            id = Integer.parseInt(argc[1]);
                        }catch(Throwable t){
                            p("Invalid id for command \"move -id\"");
                            return;
                        }
                        FateDaemon.this.sheepThread.moveSheep(id);
                        if(debug)
                            p("Sheep "+id+" moved");
                    }
                    else{
                        p("Argument "+args[1]+" to command \"move\" not regocnized");
                    }
                }
            }
        }

    }
    public static void p(String s){
        System.out.println(s);
    }
}


