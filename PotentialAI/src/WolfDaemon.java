import db.DatabaseHandler;
import model.Sheep;
import util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WolfDaemon extends Thread {
    private DatabaseHandler mHandler;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Future<?> task;

    private ArrayList<Integer> mSheeps;
    private boolean keepScheduling;

    private int delayHours = 4;

    private double accumulation = 0;

    public WolfDaemon(DatabaseHandler mHandler){
        this.mHandler = mHandler;
        this.mSheeps = new ArrayList<Integer>();
        this.keepScheduling = true;
    }

    /**
     * Run the daemon
     */
    @Override
    public void run() {
        task = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                scheduleAttack();
            }
        }, 0, this.delayHours, TimeUnit.HOURS);
    }

    /**
     * Schedulerfunction
     */
    public void scheduleAttack(){
        if(!keepScheduling)
            task.cancel(false);

        Random ran = new Random();

        double gauss = Math.abs(ran.nextGaussian()*mSheeps.size()*this.delayHours/(360*24));
        this.accumulation += ran.nextDouble()*this.accumulation/24 + (ran.nextInt(3)+2)*gauss*this.delayHours/24;
        Log.d("Wolf", "Accumulation: " + this.accumulation);
        int killcount = (int)(gauss*this.accumulation);
        Log.d("Wolf", "Killing " + killcount + " sheeps, gaussian = " + gauss+ ", result = " + gauss*this.accumulation);

        if(killcount == 0)
            return;

        doAttack();

        this.accumulation = 0;
    }

    /**
     * Carry out an attack on a not-so-innocent sheep
     */
    public void doAttackById(int id){
        if(mSheeps.size() == 0)
            return;

        Random ran = new Random();

        //See flagdata for causes
        int cause = ran.nextInt(5) + 1;

        try {
            mHandler.killSheep(id, cause);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        int cause = ran.nextInt(5) + 1;

        try {
            mHandler.killSheep(mSheeps.get(randomSheep), cause);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Carry out a specific attack on an innocent sheep
     * @param cause
     */
    public void doAttack(int cause){
        if(mSheeps.size() == 0)
            return;
        Random ran = new Random();
        int randomSheep = (int)ran.nextFloat()*(mSheeps.size()-1);
        randomSheep = randomSheep < 0 ? 0 : randomSheep;

        try {
            mHandler.killSheep(mSheeps.get(randomSheep), cause);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

    }


    /**
     * Carry out a specific attack on a not-so-innocent sheep
     * @param sheepID
     * @param cause
     */
    public void doAttack(int sheepID, int cause){
        if(mSheeps.size() == 0)
            return;

        if(!mSheeps.contains(sheepID))
            return;

        try {
            mHandler.killSheep(sheepID, cause);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
