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

        setSize(700, 700);
        add(map.getMap(), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelBottom = new JPanel();
        final JLabel sheepNames = new JLabel();
        panelBottom.add(sheepNames);
        add(panelBottom, BorderLayout.SOUTH);

        //Listener to keep track of nodes.
        map.addListener(new MapViewer.MapViewerListener() {
            @Override
            public void nodeClicked(MapViewer.NodeInfo n) {
                sheepNames.setText(n.getNodeName());
            }
        });

        setVisible(true);

    }

}
