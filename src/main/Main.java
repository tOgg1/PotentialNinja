package main;

import db.DatabaseHandler;
import gui.MainMenu;
import gui.Welcome;
import util.Log;
import util.PotentialNinjaException;

/**
* Main class, entry point and highest layer of control
*/
public class Main {

    private Register mRegister;
    private DatabaseHandler mHandler;

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

            this.mHandler = new DatabaseHandler();
            Welcome welcome = new Welcome(this, this.mHandler);
            welcome.setVisible(true);
        }
        catch(Exception f){
            f.printStackTrace();
            throw new RuntimeException("Error initializing program with error message: " + f.getMessage() + ". Please contact system administrator");
        }
    }

    public void run(int farmerid){
        mRegister = new Register(mHandler, farmerid);
        MainMenu mainWindow = new MainMenu(null, mHandler, mRegister);
        mainWindow.setVisible(true);

    }
}
