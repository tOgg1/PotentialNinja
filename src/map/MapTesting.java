package map;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: FF63
 * Date: 9/23/13
 * Time: 1:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class MapTesting extends JFrame {

    private MapViewer map;

    /**
     * Creates a JFrame containing map.
     */
    public MapTesting(){
        super("Map Viewer");
        map = new MapViewer();

        add(map.getMap(), BorderLayout.CENTER);

        setSize(700,700);
        setVisible(true);


    }

    public static void main(String[] args){
        new MapTesting();

    }
}
