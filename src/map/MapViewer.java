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
import java.util.ArrayList;
import java.util.Random;

public class MapViewer extends JFrame implements JMapViewerEventListener{

    private JMapViewerTree treeMap = null;

    //ArrayList of all added mapMarkers.
    private ArrayList<MapMarkerDot> mapMarkers;


    /**
     * Constructor
     */
    public MapViewer(){
        super("Map Viewer");

        treeMap = new JMapViewerTree("Zones");

        add(treeMap, BorderLayout.CENTER);

        mapMarkers = new ArrayList<MapMarkerDot>();

        testMapMarkers();
        setCenter(63.44,10.37, 10);

        setSize(700,700);
        setVisible(true);
    }


    /**
     * Process commands from the JMapViewerTree
     * @param jmvCommandEvent
     */
    @Override
    public void processCommand(JMVCommandEvent jmvCommandEvent) {
    }

    /**
     * Get the viewer of the map
     *
     * @return
     */
    private JMapViewer map(){
        return treeMap.getViewer();
    }

    /**
     * Helper function for creating coordinates
     * @param lat
     * @param lon
     * @return
     */
    private static Coordinate c(double lat, double lon) {
        return new Coordinate(lat, lon);
    }

    /**
     * Helper function to add MapMarkers.
     * @param name
     * @param lat
     * @param lon
     */
    public void addMarker(String name, double lat, double lon){
        MapMarkerDot dot = new MapMarkerDot(name,c(lat,lon));
        mapMarkers.add(dot);
        map().addMapMarker(dot);
    }

    /**
     * Removes all MapMarkers from the map
     */
    public void removeMarkers(){
       mapMarkers.clear();
       map().removeAllMapMarkers();
    }

    /**
     * Generate random MapMarkers
     */
    public void testMapMarkers(){
        Random r = new Random();

        for (int i = 0; i<10; i++){

            String name = "dot" + i;
            double randomValue1 = 70*r.nextDouble();
            double randomValue2 = 70*r.nextDouble();
            addMarker(name, randomValue1, randomValue2);
        }
    }

    /**
     * Helper function to set the center of the map
     *
     * @param lat
     * @param lon
     * @param zoom
     */
    public void setCenter(double lat, double lon, int zoom){
        map().setDisplayPositionByLatLon(lat,lon,zoom);
    }


    /**
     * Entry point
     * @param args
     */
    public static void main(String[] args){
        new MapViewer();
    }
}
