package model;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/12/13
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class Alarm {

    private int alarmID;
    private int sheepID;
    private int alarmFlags;

    public Alarm(int alarmID, int alarmFlags, int sheepID) {
        this.alarmID = alarmID;
        this.alarmFlags = alarmFlags;
        this.sheepID = sheepID;
    }

    public int getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public int getAlarmFlags() {
        return alarmFlags;
    }

    public void setAlarmFlags(int alarmFlags) {
        this.alarmFlags = alarmFlags;
    }

    public int getSheepID() {
        return sheepID;
    }

    public void setSheepID(int sheepID) {
        this.sheepID = sheepID;
    }
}
