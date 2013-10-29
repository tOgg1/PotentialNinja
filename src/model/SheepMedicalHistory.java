package model;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class SheepMedicalHistory {

    private int sheepid;
    private TreeMap<Long, Integer> history;

    public SheepMedicalHistory(TreeMap<Long, Integer> history, int sheepid) {
        this.history = history;
        this.sheepid = sheepid;
    }

    public TreeMap<Long, Integer> getHistory() {
        return history;
    }

    public void setHistory(TreeMap<Long, Integer> history) {
        this.history = history;
    }

    public int getSheepid() {
        return sheepid;
    }

    public void setSheepid(int sheepid) {
        this.sheepid = sheepid;
    }

    // FOR EXAMPLE!
    public void getKey(long value){
        Set<Map.Entry<Long, Integer>> entries = history.entrySet();

        for(Map.Entry<Long, Integer> entry : entries){
            entry.getKey();
            entry.getValue();
        }
    }
}
