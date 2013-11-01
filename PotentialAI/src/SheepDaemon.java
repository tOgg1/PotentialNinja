import db.DatabaseHandler;
import model.Sheep;
import util.FlagData;
import util.Log;
import util.Vec2;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SheepDaemon extends Thread {

    private DatabaseHandler mHandler;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Future<?> task;

    private ArrayList<Integer> mSheeps;

    //Hasing the IDs are safer than hashing the objects
    private HashMap<Integer, Vec2> velocities;
    private HashMap<Integer, Vec2> accelerations;

    private float multiplier = 1e-3f;

    //Required because we have two threads accessing functions simultaneously (possibly)
    private boolean keepScheduling;
    private int delayHours = 8;

    private Object lock = new Object();

    public SheepDaemon(DatabaseHandler mHandler) {
        this.mHandler = mHandler;
        this.mSheeps = new ArrayList<Integer>();
        this.velocities = new HashMap<Integer,Vec2>();
        this.accelerations = new HashMap<Integer,Vec2>();
        this.keepScheduling = true;
    }

    /**
     * Runs the thread, preferably after a .start() call
     */
    @Override
    public void run() {
        Random ran = new Random();
        for(int i = 0; i < mSheeps.size(); ++i){

            //Velocities and accelerations stored in range [-0.5f, 0.5f]
            this.velocities.put(this.mSheeps.get(i), new Vec2(ran.nextFloat() - 0.5f, ran.nextFloat() - 0.5f));
            this.accelerations.put(this.mSheeps.get(i), new Vec2(ran.nextFloat() - 0.5f, ran.nextFloat() - 0.5f));
        }
        task = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.d("Sheep", "Moving sheep");
                move();
            }
        }, this.delayHours, this.delayHours, TimeUnit.HOURS);
        move();
    }

    /**
     * Schedulerfunction
     */
    public void move(){
        if(!keepScheduling)
            this.task.cancel(false);

        moveSheeps();
        randomizePulse();
    }

    /**
     * Randomize accelerations
     */
    private void randomizeAccelerations(){
        synchronized(this.lock){
            Random ran = new Random();
            for(int i = 0; i < this.accelerations.size(); i++){
                Collection<Vec2> accs = this.accelerations.values();
                for(Vec2 vec : accs){
                    vec.x = ran.nextFloat() - 0.5f;
                    vec.y = ran.nextFloat() - 0.5f;
                }
            }
        }
    }

    private void randomizeAcceleration(int sheepID){
        synchronized(this.lock){
            Random ran = new Random();
            Vec2 acc = accelerations.get(sheepID);
            acc.x = ran.nextFloat() - 0.5f;
            acc.y = ran.nextFloat() - 0.5f;
        }
    }

    private void randomizePulse(){
        synchronized(this.lock){
            Random ran = new Random();
            for(int i = 0; i < mSheeps.size(); ++i){
                double g1 = ran.nextGaussian();
                double g2 = ran.nextGaussian();

                double g = g1+g2/2;

                int pulse = (int)(15*g + 100);

                try {
                    mHandler.setSheepPulse(mSheeps.get(i), pulse);

                    if(pulse > Sheep.pulseMax){
                        mHandler.addAlarm(mSheeps.get(i), FlagData.ALARMHIGHPULSE);
                    }
                    else if(pulse < Sheep.pulseMin){
                        mHandler.addAlarm(mSheeps.get(i), FlagData.ALARMLOWPULSE);
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    public void moveSheep(int sheepID){
        if(!mSheeps.contains(sheepID))
            return;

        synchronized(this.lock){
            try {
                Vec2 pos = mHandler.getSheepPosition(sheepID);
                pos.add(velocities.get(sheepID));
                velocities.get(sheepID).add(accelerations.get(sheepID));
            } catch (SQLException e){
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            randomizeAcceleration(sheepID);
        }
    }

    /**
     * Moves all sheeps and increments velocities by accelerations. Finishes off by randomizing accelerations
     */
    public void moveSheeps(){
        synchronized(this.lock){
            ArrayList<Vec2> sheepPositions = new ArrayList<Vec2>();

            for(int i = 0; i < this.mSheeps.size(); ++i){
                try {
                    sheepPositions.add(this.mHandler.getSheepPosition(mSheeps.get(i)));
                } catch (SQLException e) {
                    continue;
                }
            }

            for(int i = 0; i < this.mSheeps.size(); ++i){
                int id = this.mSheeps.get(i);
                Vec2 pos = sheepPositions.get(i);
                pos.x += velocities.get(id).x*multiplier;
                pos.y += velocities.get(id).y*multiplier;
                this.velocities.get(id).add(this.accelerations.get(id));
                try {
                    this.mHandler.setSheepPosition(id, pos.x, pos.y);
                } catch (SQLException e) {
                    continue;
                }
            }
            randomizeAccelerations();
        }
    }

    /**
     * Gets sheepupdates from database
     * @param sheeps
     */
    public void updateSheeps(ArrayList<Sheep> sheeps){
        synchronized(this.lock){
            Random ran = new Random();
            ArrayList<Integer> newSheeps = new ArrayList<Integer>();

            for(Sheep sheep : sheeps){
                newSheeps.add(sheep.getId());
                if(this.mSheeps.contains(sheep.getId()))
                    continue;
                this.mSheeps.add(sheep.getId());
                this.velocities.put(sheep.getId(), new Vec2(ran.nextFloat(), ran.nextFloat()));
                this.accelerations.put(sheep.getId(), new Vec2(ran.nextFloat(), ran.nextFloat()));
            }

            //Remove sheeps no longer in the system
            for(Integer in : this.mSheeps){
                if(!newSheeps.contains(in)){
                    mSheeps.remove(in);
                    velocities.remove(in);
                    accelerations.remove(in);
                }
            }
        }
    }
}
