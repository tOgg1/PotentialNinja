package ai;

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

    private ArrayList<Integer> sheepIDs;

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

        this.timer = new Timer("FateTimer", true);
        scheduleAndUpdate();
        while(keepScheduling){
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
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

    class InputManager implements Runnable{

        private BufferedReader input;

        public InputManager(){
            input = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {
            try {
                String string;

                while((string = input.readLine()) != null){
                    String args[] = string.split("-");
                    decryptAndExecute(args);
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        public void decryptAndExecute(String[] args){
            args[0] = args[0].toLowerCase();
            if(args[0].equals("exit")){
                FateDaemon.this.keepScheduling = false;
            }
            else if(args[0].equals("kill")){
                if(args.length > 1){
                    args[1] = args[1].toLowerCase();
                    if(args[1].equals("wolf"))
                        FateDaemon.this.wolfThread.doAttack(FlagData.DEATHBYWOLF);
                    else if(args[1].equals("fall"))
                        FateDaemon.this.wolfThread.doAttack(FlagData.DEATHBYFALL);
                    else if(args[1].equals("disease"))
                        FateDaemon.this.wolfThread.doAttack(FlagData.DEATHBYDISEASE);
                    else if(args[1].equals("human"))
                        FateDaemon.this.wolfThread.doAttack(FlagData.DEATHBYHUMAN);
                    else if(args[1].equals("sheep"))
                        FateDaemon.this.wolfThread.doAttack(FlagData.DEATHBYSHEEP);
                    else
                        FateDaemon.this.wolfThread.doAttack(FlagData.DEATHUNKNOWN);
                }
                else{
                    FateDaemon.this.wolfThread.doAttack();
                }
            }
            else if(args[0].equals("move")){
                if(args.length > 1){
                    args[1] = args[1].toLowerCase();
                    if(args[1].equals("all")){
                        FateDaemon.this.sheepThread.moveSheeps();
                    }
                    else if(args[1].equals("id")){
                        String[] argc = args[1].split(" ");
                        if(argc.length != 2){
                            System.out.println("Invalid arguments for command \"move\"");
                            return;
                        }
                        int id = 0;
                        try{
                        id = Integer.parseInt(argc[1]);
                        }catch(Throwable t){
                            System.out.println("Invalid id for command \"move -id\"");
                            return;
                        }
                        FateDaemon.this.sheepThread.moveSheep();

                    }
                }
            }

        }

    }
}
