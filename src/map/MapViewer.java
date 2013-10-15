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
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MapViewer extends MouseAdapter implements JMapViewerEventListener, MouseListener {

    private JMapViewerTree treeMap = null;
    private JMapViewer map = null;

    //ArrayList of all current dots.
    private ArrayList<MapMarkerDot> mapDots = null;
    private ArrayList<String> mouseDotName = null;

    //LinkedList to keep track of listeners
    private LinkedList<MapViewerListener> listeners = null;

    private final DecimalFormat df = new DecimalFormat("#.0");

    //variables to keep track of where the mouse is
    private double mouseX;
    private double mouseY;


    /**
     * Constructor
     */
    public MapViewer(){

        treeMap = new JMapViewerTree("Zones");
        map = this.getMap();

        mapDots = new ArrayList<MapMarkerDot>();
        mouseDotName = new ArrayList<String>();
        listeners = new LinkedList<MapViewerListener>();

        //Sets the map type.
        map.setTileSource(new OsmTileSource.CycleMap());

        // Listen to the map viewer for user operations so components will
        // receive events and update
        map.addJMVListener(this);

        //Sets the movement mouse button to mouse1
        DefaultMapController mapController = new DefaultMapController(map);
        mapController.setMovementMouseButton(MouseEvent.BUTTON1);


        //Adds a mouse listener to capture mouse activity.
        map.addMouseListener(
                new MouseAdapter() {

                    @Override
                    /**
                     * When user clicks left mouse button, this method will get the mouse position
                     */
                    public void mouseClicked(MouseEvent e) {
                        if(e.getButton() == MouseEvent.BUTTON1){

                            Coordinate pos = getMap().getPosition(e.getX(),e.getY());
                            mouseX = pos.getLat();
                            mouseY = pos.getLon();
                            setMouseDotName();
                        }

                    }
                }
        );

        //MapDaemon daemon = new MapDaemon(25);
        //daemon.start();
        setCenter(63.44, 10.37, 10);

    }

    /**
     * Adds the dots from the (x,y) location of the mouse to an ArrayList.
     */
    public void setMouseDotName(){

        if(!mouseDotName.isEmpty()){
            mouseDotName.clear();
        }

        for (MapMarkerDot d : mapDots){

            String dotLat = df.format(d.getLat());
            String dotLon = df.format(d.getLon());
            String mouseLat = df.format(mouseX);
            String mouseLon = df.format(mouseY);

            if (dotLat.equals(mouseLat) && dotLon.equals(mouseLon)){

                mouseDotName.add(d.getName());

                NodeInfo nodeInfo = new NodeInfo(d.getName(), dotLat, dotLon);

                for (MapViewerListener mvl : listeners){
                    mvl.nodeClicked(nodeInfo);
                }

            }
        }
    }

    /**
     * NodeInfo class. Used to keep track of the node info.
     */
    public static class NodeInfo{
        String nodeName;
        String dotLat;
        String dotLon;

        /**
         * Constructor
         * @param nodeName
         * @param dotLat
         * @param dotLon
         */
        public NodeInfo(String nodeName, String dotLat, String dotLon){
            this.nodeName = nodeName;
            this.dotLat = dotLat;
            this.dotLon = dotLon;
        }

        /**
         * Helper function to get node name from NodeInfo
         * @return
         */
        public String getNodeName(){
            return this.nodeName;
        }

        /**
         * Helper function to get Latitude from NodeInfo
         * @return
         */
        public String getDotLat(){
            return this.dotLat;
        }

        /**
         * Helper function to get Longitude from NodeInfo
         * @return
         */
        public String getDotLon(){
            return this.dotLon;
        }


    }

    /**
     *  Listener interface
     */
    public static interface MapViewerListener{
        public void nodeClicked(NodeInfo n);
    }

    /**
     * Add listener to "listeners"  LinkedList.
     * @param mvl
     */
    public void addListener(MapViewerListener mvl){
        listeners.add(mvl);
    }


    /**
     * Probably redundant. Will review at a later time
     * @return
     */
    public ArrayList<String> getMouseDotName(){
        if(!mouseDotName.isEmpty()){
            return mouseDotName;
        }

        return null;
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
     * Get (lat,lon) coordinates based on the (x,y) coordinates in frame.
     * @param mapPointX
     * @param mapPointY
     * @return
     */
    public Coordinate getPosition(int mapPointX, int mapPointY){
        return map.getPosition(mapPointX, mapPointY);
    }

    /**
     * Returns list of active Map Markers.
     * @return
     */
    public ArrayList<MapMarkerDot> getCurrentMapMarkers(){
        return mapDots;
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
        mapDots.add(dot);

        map.addMapMarker(dot);
    }

    /**
     * Helper function to add MapMarker with different background color
     * @param lat
     * @param lon
     * @param color
     */
    public void addMarker(double lat, double lon, Color color){
        MapMarkerDot dot = new MapMarkerDot(c(lat,lon));
        dot.setBackColor(color);
        map.addMapMarker(dot);
    }

    /**
     * Removes all MapMarkers from the map
     */
    public void removeMarkers(){
       mapDots.clear();
       map.removeAllMapMarkers();
    }


    /**
     * Set MapMarker visibility
     * @param visibility
     */
    public void setMarkerVisiblity(boolean visibility){
        map.setMapMarkerVisible(visibility);
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
        double[] accelerations;
        String[] names;
        public boolean running;
        long counter;

        public MapDaemon(int count){
            Random ran = new Random();

            positions = new double[2*count];
            velocities = new double[2*count];
            accelerations = new double[2*count];

            names = new String[count];
            for(int i = 0; i < 2*count; ++i){
                names[i/2] = sheepNames[ran.nextInt(MapViewer.sheepNames.length)];
                positions[i] = 63.391829 + ran.nextDouble() *0.08 - 0.04;
                positions[++i] =  10.242294 + ran.nextDouble()*0.08 - 0.04;
                velocities[i-1] = 5e-6*ran.nextDouble() - 2.5e-6;
                velocities[i] = 5e-6*ran.nextDouble() - 2.5e-6;
                accelerations[i-1] = 5e-8*ran.nextDouble() - 2.5e-8;
                accelerations[i] = 5e-8*ran.nextDouble() - 2.5e-8;
            }
            running = true;
            counter = 0;
        }

        @Override
        public void run() {
            Random ran = new Random();
            while(running){

                // Markers from 2 thousand and late
                MapViewer.this.removeMarkers();
                counter++;

                // Newton integration is really cool
                for(int i = 0; i < positions.length; ++i){
                    MapViewer.this.addMarker(names[i/2], positions[i], positions[++i]);
                    positions[i-1] += velocities[i-1];
                    positions[i] += velocities[i];
                    velocities[i-1] += accelerations[i-1];
                    velocities[i] += accelerations[i];
                }

                // Pseudo-Randomized acceleration for scientifically accurate sheep-like behaviour
                if(counter % 100 == 0){
                    for(int i = 0; i < positions.length; ++i){
                        if(ran.nextInt(4) > 3){
                            accelerations[++i-1] = 5e-8*ran.nextDouble() - 2.5e-8;
                            accelerations[i] = 5e-8*ran.nextDouble() - 2.5e-8;
                        }
                    }
                }

                // Slow sheep down periodically, to avoid sheep throwing a fit
                if((counter+10) % 100 == 0){
                    for(int i = 0; i < positions.length; ++i){
                        accelerations[++i-1] = -0.05*velocities[i-1];
                        accelerations[i] = -0.05*velocities[i];
                    }
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
            "Stian the sheepminator",
            "Sondre, sheep of doom",
            "Nora, the sheepmother",
            "Vigdis, sheep of trikom",
            "Vilde, queen of sheeps",
            "Tormod, sheep of sweat",
            "Ragnar",
            "Bjørnar",
            "Gudrun",
            "Arne",
            "Simon",
            "Huldra",
            "Jørunn",
            "Sigrid",
            "Anders",
            "Oddleif",
            "Torleif",
            "Thor",
            "Gud",
            "Jesus",
            "Messias",
            "Moses",
            "Abraham",
            "Paul",
            "Isaac",
            "Mordi",
            "Sebastian",
            "Fredrik",
            "Frederik",
            "Kristoffer",
            "Mikkel",
            "Nicolas",
            "Nikolai",
            "Bernt",
            "Bjarne",
            "Berit",
            "Bertil",
            "Martine",
            "Marte",
            "Judas"
    };

}
