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
            this.login();
        }
        catch(Exception f){
            f.printStackTrace();
            throw new RuntimeException("Error initializing program with error message: " + f.getMessage() + ". Please contact system administrator");
        }
    }

    public void login(){
        Welcome welcome = new Welcome(this, this.mHandler);
        welcome.setVisible(true);
    }

    public void run(int farmerid, Welcome welcome){
        mRegister = new Register(mHandler, farmerid);
        MainMenu mainWindow = new MainMenu(this, welcome, farmerid, mHandler, mRegister);
        mainWindow.setVisible(true);

    }
}
