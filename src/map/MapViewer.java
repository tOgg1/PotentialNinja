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

import java.util.ArrayList;
import java.util.Random;

public class MapViewer implements JMapViewerEventListener{

    private JMapViewerTree treeMap = null;

    //ArrayList of all added mapMarkers.
    private ArrayList<MapMarkerDot> mapMarkers;


    /**
     * Constructor
     */
    public MapViewer(){

        treeMap = new JMapViewerTree("Zones");

        mapMarkers = new ArrayList<MapMarkerDot>();

        testMapMarkers();
        setCenter(63.44,10.37, 10);

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
    public JMapViewer getMap(){
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
        getMap().addMapMarker(dot);
    }

    /**
     * Removes all MapMarkers from the map
     */
    public void removeMarkers(){
       mapMarkers.clear();
       getMap().removeAllMapMarkers();
    }

    /**
     * Generate random MapMarkers
     */
    private void testMapMarkers(){
        Random r = new Random();

        //Creates 10 random dots and adds them to the world map.
        for (int i = 1; i<11; i++){
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
        getMap().setDisplayPositionByLatLon(lat,lon,zoom);
    }

}
