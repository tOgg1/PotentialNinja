package main;

import db.DatabaseHandler;
import map.MapSheeps;
import map.MapViewer;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/9/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test extends JFrame{

    public static void main(String[] args ) throws Exception{
        new Test();

    }

    public Test() throws Exception{

        DatabaseHandler handler = new DatabaseHandler();
        int farmerid = handler.authenticate("farm", "farm");

        MapViewer map = new MapViewer();

        MapSheeps mapSheep = new MapSheeps(handler, farmerid, map);

        add(map.getMap(), BorderLayout.CENTER);
        setSize(700,700);
        setVisible(true);

    }

}
