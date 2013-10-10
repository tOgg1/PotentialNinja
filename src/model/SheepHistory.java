package model;

import util.Vec2;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/12/13
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class SheepHistory {
    private int sheepID;
    private TreeMap<Long, Vec2> history;

    public SheepHistory(TreeMap<Long, Vec2> history, int sheepID) {
        this.history = history;
        this.sheepID = sheepID;
    }

    public TreeMap<Long, Vec2> getHistory() {
        return history;
    }

    public void setHistory(TreeMap<Long, Vec2> history) {
        this.history = history;
    }

    public int getSheepID() {
        return sheepID;
    }

    public void setSheepID(int sheepID) {
        this.sheepID = sheepID;
    }
}
