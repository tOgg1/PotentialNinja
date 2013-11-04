package main;

import db.DatabaseHandler;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 11/4/13
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(String[] args){
        DatabaseHandler handler = new DatabaseHandler();


        for(int i = 0; i < 1000; ++i){
            long one = new Date().getTime();

            p("Adding sheeps to farmer"+i);
            for (int j = 0; j < 200; j++){
                try {
                    handler.addSheep("Sau"+j, one, 0, -120*(i/1000) + 60, -120*(i/1000) + 60, "m", i+24);
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            long two = new Date().getTime();
            p("Time elapsed: " + (two-one)*1e-3 + " sekunder.");
        }
    }

    public static void p(String s){
        System.out.println(s);
    }
}
