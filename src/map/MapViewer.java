package map;

import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import util.GeneralUtil;
import util.Log;
import util.Vec2;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import static util.GeneralUtil.assertEquals;

public class MapViewer extends MouseAdapter implements JMapViewerEventListener, MouseListener {

    private JMapViewerTree treeMap = null;
    private JMapViewer map = null;

    private int defaultZoom = 10;
    private double mapCenterX, mapCenterY;

    //ArrayList of all current dots.
    private ArrayList<MapMarkerDot> mapDots = null;
    private ArrayList<String> mouseDotName = null;
    private TreeMap<Integer, MapMarkerDot> dotId = null;

    //LinkedList to keep track of listeners
    private ArrayList<MapViewerListener> listeners = null;

    private DecimalFormat df = null;

    //variables to keep track of where the mouse is
    private double mouseX = 0;
    private double mouseY = 0;

    /**
     * Constructor
     */
    public MapViewer(){
        treeMap = new JMapViewerTree("Zones");
        map = this.getMap();

        mapDots = new ArrayList<MapMarkerDot>();
        mouseDotName = new ArrayList<String>();
        dotId = new TreeMap<Integer, MapMarkerDot>();
        listeners = new ArrayList<MapViewerListener>();

        //Sets the map type.
        map.setTileSource(new OsmTileSource.CycleMap());

        // Listen to the map viewer for user operations so components will
        // receive events and update
        map.addJMVListener(this);
        map.addMouseListener(new MapListener());

        //Sets the movement mouse button to mouse1
        final DefaultMapController mapController = new DefaultMapController(map);
        mapController.setMovementMouseButton(MouseEvent.BUTTON1);
        mapController.setDoubleClickZoomEnabled(false);

    }

    /**
     * MapListener
     */
    private class MapListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                Coordinate pos = map.getPosition(e.getX(), e.getY());
                mouseX = pos.getLat();
                mouseY = pos.getLon();
                for(MapViewerListener listen : listeners){
                    listen.mapClicked(mouseX, mouseY);
                }
                //setMouseDotName();
                Log.d("Map", "CHECKING FOR CLICKS\n-----------------\n");

                checkForNodeClicks();
            }
        }
    }

    /**
     * Checks for node clicks.
     */
    public void checkForNodeClicks(){
        MapMarkerDot least = null;
        double leastValue = Double.MAX_VALUE;
        double leastX = 0, leastY = 0;
        DecimalFormat df = new DecimalFormat("#.0000000");
        for (MapMarkerDot mapDot : mapDots) {
            leastX = mapDot.getLat();
            leastY = mapDot.getLon();

            int zoom = map.getZoom();

            // Latitude needs to correlate for shrinking latitude at higher longitudes, thus dividing by c*mouseY (This is slightly incorrect, longitude-shrinking is not linear).
            // It seems that the map is shrinking by a value between 1.5 and 2.0 every time. Therefore the Math.pow(1.8, -zoom+1) logarithmic correction. +1 as zoom starts at 1.
            if(assertEquals(leastX, mouseX, 0.01f*Math.pow(1.8,-zoom+1)/Math.abs(0.2f*mouseY), Math.pow(1.8,-zoom+1)/Math.abs(0.2f*mouseY)) && assertEquals(leastY, mouseY, 0.01f*Math.pow(1.8, -zoom+1), Math.pow(1.8, -zoom+1))){
                Log.d("Map", "FOUND ONE!");
                double distance = GeneralUtil.getDistance(leastX, leastY, mouseX, mouseY);
                if(GeneralUtil.getDistance(leastX, leastY, mouseX, mouseY) < leastValue){
                    leastValue = distance;
                    least = mapDot;
                }
            }
        }

        if(least != null){
            NodeInfo nodeInfo = new NodeInfo(least.getName(), df.format(leastX), df.format(leastY), least);
            for (MapViewerListener listener : listeners) {
                listener.nodeClicked(nodeInfo);
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
        MapMarkerDot dot;

        /**
         * Constructor
         * @param nodeName
         * @param dotLat
         * @param dotLon
         */
        public NodeInfo(String nodeName, String dotLat, String dotLon, MapMarkerDot dot){
            this.nodeName = nodeName;
            this.dotLat = dotLat;
            this.dotLon = dotLon;
            this.dot = dot;
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

        /**
         * Helper function to get MapMarkerDot from NodeInfo.
         * @return
         */
        public MapMarkerDot getDot(){
            return this.dot;
        }
    }

    /**
     *  Listener interface
     */
    public static interface MapViewerListener{
        public void nodeClicked(NodeInfo n);
        public void mapClicked(double x, double y);
    }

    /**
     * Adds listener.
     * @param mvl MapViewerListener
     */
    public void addListener(MapViewerListener mvl){
        listeners.add(mvl);
    }

    /**
     * Process commands from the JMapViewerTree
     * @param jmvCommandEvent JMVCommandEvent
     */
    @Override
    public void processCommand(JMVCommandEvent jmvCommandEvent) {
    }

    /**
     * Get the viewer of the map
     *
     * @return returns the map viewer.
     */
    public JMapViewer getMap(){
        return treeMap.getViewer();
    }

    /**
     * Returns list of active Map Markers.
     * @return mapDots
     */
    public ArrayList<MapMarkerDot> getCurrentMapMarkers(){
        return mapDots;
    }

    /**
     * Helper function to set the center of the map
     * @param position Vec2 containing position
     */
    public void setMapCenter(Vec2 position){
       mapCenterX = position.x;
       mapCenterY = position.y;
       map.setDisplayPositionByLatLon(mapCenterX ,mapCenterY ,defaultZoom);
    }

    /**
     * Helper function for creating coordinates
     * @param lat Latitude
     * @param lon Longitude
     * @return
     */
    private static Coordinate c(double lat, double lon) {
        return new Coordinate(lat, lon);
    }

    /**
     * Returns a TreeMap containing DotID's with MapMarkerDot as values.
     * @return Returns dotId if it exists, else returns null
     */
    public TreeMap<Integer, MapMarkerDot> getDotId(){
        if (!dotId.isEmpty()){
            return dotId;
        }
            return null;
    }

    /**
     * Helper function to add MapMarkers.
     * @param name Marker name
     * @param lat  Marker latitude
     * @param lon Marker longitude
     * @param id  Marker id
     */
    public void addMarker(String name, double lat, double lon, int id){
        MapMarkerDot dot = new MapMarkerDot(name,c(lat,lon));
        mapDots.add(dot);
        dotId.put(id,dot);

        map.addMapMarker(dot);
    }

    /**
     * Helper function to add MapMarker with different background color
     * @param lat Marker latitude
     * @param lon Marker longitude
     * @param color Marker color
     */
    public void addMarker(double lat, double lon, Color color){
        MapMarkerDot dot = new MapMarkerDot(c(lat,lon));
        dot.setBackColor(color);
        map.addMapMarker(dot);
    }

    /**
     * Adds a map marker with name, lat, lon and color.
     * @param name Marker name
     * @param lat  Marker latitude
     * @param lon  Marker longitude
     * @param color Marker color
     */
    public void addMarker(String name, double lat, double lon, Color color){
        MapMarkerDot dot = new MapMarkerDot(name, c(lat,lon));
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
     * Returns an ArrayList of all map markers.
     * @return ArrayList containing MapMarkerDots
     */
    public ArrayList<MapMarkerDot> getMapMarkers(){
        return this.mapDots;
    }

}
