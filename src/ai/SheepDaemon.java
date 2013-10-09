package ai;

import model.Sheep;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/12/13
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class SheepDaemon extends Thread {

    Timer timer;

    ArrayList<Sheep> mSheeps;
    float[] velocities;
    float[] accelerations;

    public SheepDaemon(ArrayList<Sheep> mSheeps) {
        this.mSheeps = mSheeps;
    }

    @Override
    public void run() {

    }

    public void moveSheeps(){

    }
}
