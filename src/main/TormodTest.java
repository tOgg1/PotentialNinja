package main;

import db.DatabaseHandler;

/**
 * STAY AWAY!!!!!!!!!!
 */
public class TormodTest {

    public static void main(String[] args) throws Exception{

        int sheepid = 9;
        DatabaseHandler handler = new DatabaseHandler();
        handler.reviveSheep(9);

        p(""+handler.isSheepDead(sheepid));
    }

    public static void p(String s){
        System.out.println(s);
    }
}
