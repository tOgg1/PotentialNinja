package ai;

import db.DatabaseHandler;
import model.Sheep;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/12/13
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class WolfDaemon extends Thread {
    private DatabaseHandler mHandler;

    private Timer timer;

    private ArrayList<Integer> mSheeps;

    public WolfDaemon(DatabaseHandler mHandler){
        this.mHandler = mHandler;
        this.mSheeps = new ArrayList<Integer>();
    }

    /**
     * Run the daemon
     */
    @Override
    public void run() {
        timer = new Timer("WolfDaemon", true);
        scheduleAndAttack();
    }

    /**
     * Schedulerfunction
     */
    public void scheduleAndAttack(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                scheduleAndAttack();
            }
        };
        Random ran = new Random();
        timer.schedule(task, (long)3600*ran.nextInt(36));
        doAttack();
    }

    /**
     * Carry out an attack on an innocent sheep
     */
    public void doAttack(){

        if(mSheeps.size() == 0)
            return;

        Random ran = new Random();
        int randomSheep = (int)ran.nextFloat()*(mSheeps.size()-1);

        randomSheep = randomSheep < 0 ? 0 : randomSheep;

        //See flagdata for causes
        int cause = ran.nextInt(4) + 1;

        try {
            mHandler.killSheep(mSheeps.get(randomSheep), cause);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Gets sheepupdates from database
     * @param sheeps
     */
    public void updateSheeps(ArrayList<Sheep> sheeps){
        ArrayList<Integer> newSheeps = new ArrayList<Integer>();
        for(Sheep sheep : sheeps){
            newSheeps.add(sheep.getId());
            if(mSheeps.contains(sheep.getId()))
                continue;
            mSheeps.add(sheep.getId());
        }

        //Remove old sheeps no longer in existence
        for(Integer in : this.mSheeps){
            if(!newSheeps.contains(in)){
                this.mSheeps.remove(in);
            }
        }
    }
}
