package main;

import db.DatabaseHandler;
import model.Alarm;
import model.Pos;
import model.SheepHistory;
import util.Log;
import util.PotentialNinjaException;

import java.util.ArrayList;
import java.util.Map;

/**
 * Main class, entry point and highest layer of control
 */
public class Main {

    //TODO: Throws Exception is temporary, implement exception-handling
    public static void main(String[] args) throws Exception {
        try{
            Log.initLogFile();
        }catch(PotentialNinjaException e){
            e.printStackTrace();
        }
        DatabaseHandler handler = new DatabaseHandler();

        System.out.println(""+handler.authenticate("bjornarsuperfarm", "johnny"));

        System.out.println("\nFarmer 3 INFO\n--------------");
        System.out.println(""+handler.getFarmerInformation(3)[0]);
        System.out.println(""+handler.getFarmerInformation(3)[1]);
        System.out.println(""+handler.getFarmerInformation(3)[2]);

        SheepHistory history = handler.getSheepHistory(1);

        System.out.println("\nSHEEP HISTORY for sheep"+history.getSheepID()+"\n--------------");
        for(Map.Entry<Long, Pos> pair : history.getHistory().entrySet()){
            System.out.println("Time: " + pair.getKey() + " |  Pos: " + pair.getValue().toString());
        }

        ArrayList<Alarm> alarms = handler.getAlarms(3);
        System.out.println("\nALARMS for farmer 3\n--------------");
        for(Alarm alarm : alarms){
            System.out.println("Sheep: " + alarm.getSheepID() + " | Flag: " + alarm.getAlarmFlags());
        }

        handler.close();
    }
}
