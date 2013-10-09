package ai;

import db.DatabaseHandler;

import java.sql.SQLException;
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
    private boolean keepPolling;

    int databaseState;

    public FateDaemon(){
        handler = new DatabaseHandler();

        databaseState = getDBState(handler);

        sheepThread = new SheepDaemon(null);
        wolfThread = new WolfDaemon();
        keepPolling = false;
    }

    public static int getDBState(DatabaseHandler handler){
        try {
            return handler.getState();
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public void run() {
        sheepThread.start();
        wolfThread.start();

        keepPolling = true;

        timer = new Timer("FateTimer", true);
        scheduleAndUpdate();
    }

    private void scheduleAndUpdate(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                scheduleAndUpdate();
            }
        };
        timer.schedule(task, 10000);
    }
}
