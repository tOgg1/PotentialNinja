package model;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/12/13
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class SheepHistory {
    private int sheepID;
    private ArrayList<Pos> history;

    public SheepHistory(ArrayList<Pos> history, int sheepID) {
        this.history = history;
        this.sheepID = sheepID;
    }

    public ArrayList<Pos> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<Pos> history) {
        this.history = history;
    }

    public int getSheepID() {
        return sheepID;
    }

    public void setSheepID(int sheepID) {
        this.sheepID = sheepID;
    }
}
