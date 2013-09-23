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

        MapDaemon daemon = new MapDaemon(100);
        daemon.start();
        setCenter(63.44,10.37,10);

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

    class MapDaemon extends Thread{

        double[] positions;
        double[] velocities;
        String[] names;
        public boolean running;

        public MapDaemon(int count){
            Random ran = new Random();
            positions = new double[2*count];
            velocities = new double[2*count];
            names = new String[count];
            for(int i = 0; i < 2*count; ++i){
                names[i/2] = sheepNames[ran.nextInt(MapViewer.sheepNames.length)];
                positions[i] =
                        63.391829 + ran.nextDouble() *0.1;
                positions[++i] =  10.242294 + 0.1*ran.nextDouble();
                velocities[i-1] = 0.05*ran.nextDouble();
                velocities[i] = 0.05*ran.nextDouble();
            }
            running = true;
        }

        @Override
        public void run() {
            while(running){
                MapViewer.this.removeMarkers();
                for(int i = 0; i < 2*positions.length -1; ++i){
                    if(i/2 > 99)
                        break;
                    MapViewer.this.addMarker(names[(int)Math.floor(i/2)], positions[i], positions[++i]);
                    positions[i-1] += velocities[i-1];
                    positions[i] += velocities[i];
                }
                try {
                    Thread.sleep(1000/30);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    public static String[] sheepNames = {
            "Ragnar",
            "Bjørnar",
            "Gudrun",
            "Arne",
            "Simon",
            "Huldra",
            "Jørunn",
            "Sigrid",
            "Anders",
            "Torleif",
            "Thor"
    };

}
