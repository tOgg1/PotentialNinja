package map;

/**
 * Created with IntelliJ IDEA.
 * User: FF63
 * Date: 9/17/13
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */

import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;

import javax.swing.*;
import java.awt.*;

public class JMapViewerTest extends JFrame implements JMapViewerEventListener{

    private JMapViewerTree treeMap = null;

    private JMapViewer map(){
        return treeMap.getViewer();
    }

    private static Coordinate c(double lat, double lon) {
        return new Coordinate(lat, lon);
    }

    public JMapViewerTest(){
        super("Map Viewer");

        treeMap = new JMapViewerTree("Zones");

        add(treeMap, BorderLayout.CENTER);

        MapMarkerDot dot = new MapMarkerDot("sup m8", c(39.20102,40.29123));
        map().addMapMarker(dot);

        setSize(700,700);
        setVisible(true);
    }

    @Override
    public void processCommand(JMVCommandEvent jmvCommandEvent) {
    }

    public static void main(String[] args){
        new JMapViewerTest();
    }
}
