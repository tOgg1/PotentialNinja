package ai;

import db.DatabaseHandler;

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

        sheepThread = new SheepDaemon(null);
        wolfThread = new WolfDaemon();
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

    }

    public void cascadeData(){

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
            this.cascadeData();
        }
    }
}
