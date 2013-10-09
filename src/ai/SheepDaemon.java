package ai;

import db.DatabaseHandler;
import model.Sheep;
import util.Vec2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/12/13
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class SheepDaemon extends Thread {

    private DatabaseHandler handler;

    private Timer timer;

    private ArrayList<Sheep> mSheeps;

    //Hasing the IDs are safer than hashing the objects
    private HashMap<Integer, Vec2> velocities;
    private HashMap<Integer, Vec2> accelerations;

    public SheepDaemon(DatabaseHandler handler, ArrayList<Sheep> mSheeps) {
        this.handler = handler;
        this.mSheeps = mSheeps;
        this.timer = new Timer("SheepDaemon", true);
        this.velocities = new HashMap<Integer,Vec2>();
        this.accelerations = new HashMap<Integer,Vec2>();
    }

    @Override
    public void run() {
        Random ran = new Random();
        for(int i = 0; i < mSheeps.size(); ++i){

            //Velocities and accelerations stored in range [-0.5f, 0.5f]
            this.velocities.put(mSheeps.get(i).getId(), new Vec2(ran.nextFloat() - 0.5f, ran.nextFloat() - 0.5f));
            this.accelerations.put(mSheeps.get(i).getId(), new Vec2(ran.nextFloat() - 0.5f, ran.nextFloat() - 0.5f));
        }
    }

    public void moveSheeps(){
        ArrayList<Vec2> sheepPositions;
        for(int i = 0; i < mSheeps.size(); ++i){
            mSheeps.get(i).
        }
    }

    public void updateSheeps(ArrayList<Sheep> sheeps){

    }


}
