package main;

import db.DatabaseHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/9/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(String[] args ) throws Exception{

        DatabaseHandler handler = new DatabaseHandler();
        int farmerid = handler.authenticate("farm", "farm");
        System.out.println("Success!!" + farmerid);
    }

}
