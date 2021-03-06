import db.DatabaseHandler;
import model.Sheep;
import util.FlagData;
import util.Log;
import util.PotentialNinjaException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FateDaemon extends Thread {
    private DatabaseHandler handler;
    private SheepDaemon sheepThread;
    private WolfDaemon wolfThread;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Future<?> task;
    private boolean keepScheduling;

    int databaseState;

    private int delayMinutes = 2;

    public FateDaemon(){
        handler = new DatabaseHandler();
        try {
            Log.initLogFile();
        } catch (PotentialNinjaException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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

        this.task = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run(){
                update();
            }
        }, 0, this.delayMinutes, TimeUnit.MINUTES);
        InputManager iManage = new InputManager();
        iManage.start();
    }

    private void update(){
        int newState = getDBState(this.handler);

        if(!keepScheduling){
            this.task.cancel(false);
        }

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

        private String mStartUp = "Command Parser v1.0e\nCreated by tOgg\n";
        private String mReady = "Ready for commands ...";
        private boolean debug = false;

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
            else if(args[0].equals("debug")){
                this.debug = !this.debug;
            }
        }

    }
    public static void p(String s){
        System.out.println(s);
    }
}


