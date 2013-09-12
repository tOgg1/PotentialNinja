package main;

import db.DatabaseHandler;
import util.Log;
import util.PotentialNinjaException;

public class Main {

    public static void main(String[] args) {
        try{
            Log.initLogFile();
        }catch(PotentialNinjaException e){
            e.printStackTrace();
        }
        DatabaseHandler handler = new DatabaseHandler();

        int accountid = handler.authenticate("bjornarsuperfarm", "johnny");
        System.out.println(""+accountid);
    }
}
