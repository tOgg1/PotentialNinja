package main;

import db.DatabaseHandler;
import util.Log;
import util.PotentialNinjaException;

/**
 * Main class, entry point and highest layer of control
 */
public class Main {

    //Login variables
    int farmerID;
    String farmerName;
    float defaultPosX, defaultPosY;


    public static void main(String[] args){
        Main main = new Main();
        main.initialize();
    }

    public void initialize(){
        try{

            try{
                Log.initLogFile();
            }catch(PotentialNinjaException e){
                e.printStackTrace();
            }

            DatabaseHandler handler = new DatabaseHandler();

            //Log in
            if((this.farmerID = handler.authenticate("bjornarsuperfarm", "johnny")) == -1){
                System.exit(1);
            }
            Object[] farmerDetails = handler.getFarmerInformation(this.farmerID);
            if(farmerDetails == null)
                throw new Exception("Unable to find farmer information");
            this.farmerName = (String)farmerDetails[0];
            this.defaultPosX = (Float)farmerDetails[1];
            this.defaultPosY = (Float)farmerDetails[2];

            System.out.println("Welcome to SheepTracker 2013 "+ farmerName+"!");

            testFunction();
            handler.close();
        }
        catch(Exception f){
            f.printStackTrace();
            throw new RuntimeException("Error initializing program with error message: " + f.getMessage() + ". Please contact system administrator");
        }
    }


    /**
     *  Do all database testing here
     */
    public static void testFunction() throws Exception{

        DatabaseHandler handler = new DatabaseHandler();

        System.out.println(""+handler.getFarmerContactInformation(3)[0]);


        /*System.out.println(""+handler.authenticate("bjornarsuperfarm", "johnny"));

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
        */
    }
}
