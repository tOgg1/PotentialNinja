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
            
            this.farmerID = handler.authenticate("bjornarsuperfarm", "johnny");
            
            if(this.farmerID == -1)
            
            //Log in
            if((this.farmerID = handler.authenticate("bjornarsuperfarm", "johnny")) == -1){
                System.exit(1);
            }
            Register register = new Register(handler, this.farmerID);

            Object[] farmerDetails = handler.getFarmerInformation(this.farmerID);
            if(farmerDetails == null)
                throw new Exception("Unable to find farmer information");
            this.farmerName = (String)farmerDetails[0];
            this.defaultPosX = (Float)farmerDetails[1];
            this.defaultPosY = (Float)farmerDetails[2];

            register.setFarmerID(this.farmerID);

            System.out.println("Welcome to SheepTracker 2013 "+ farmerName+"!");

            handler.close();
        }
        catch(Exception f){
            f.printStackTrace();
            throw new RuntimeException("Error initializing program with error message: " + f.getMessage() + ". Please contact system administrator");
        }
    }
}
