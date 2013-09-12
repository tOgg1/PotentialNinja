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

        handler.addSheep("Stian", 21, 0xFFFFFFFF, 1337.0f, 1337.0f, 3);
    }
}
