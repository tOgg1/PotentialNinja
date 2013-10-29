package main;

import db.DatabaseHandler;
import model.Sheep;

/**
 * STAY AWAY!!!!!!!!!!
 */
public class TormodTest {

    public static void main(String[] args) throws Exception{

        DatabaseHandler handler = new DatabaseHandler();
        Sheep sheep = handler.getSheep(7);
        p(""+sheep.getSex());
    }

    public static void p(String s){
        System.out.println(s);
    }
}
