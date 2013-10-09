package ai;

import db.DatabaseHandler;
import model.Sheep;

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
        reFetchAllData();
        sheepThread = new SheepDaemon(handler);
        wolfThread = new WolfDaemon(handler);
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
}
