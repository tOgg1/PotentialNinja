package map;

import db.DatabaseHandler;
import main.Register;
import model.Sheep;
import model.SheepHistory;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import util.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class MapSheeps
{
	private Register register;
	private ArrayList <Sheep> currentSheeps;
	private int farmerId;
    private MapViewer map;
    private ArrayList<MapMarkerDot> mapMarkers = null;
    private TreeMap<Integer, MapMarkerDot> dotTreeMap = null;
    private int MapListenerSheepId;

	
	public MapSheeps (DatabaseHandler handler, int farmerId, final MapViewer map)
	{
		register = new Register (handler, farmerId);
		currentSheeps = new ArrayList<Sheep> ();

		this.farmerId = farmerId;
        this.map = map;
        //Sets the center of the map
        this.map.setMapCenter(getFarmerCenter().toList());

		setSheeps();
        setCurrentSheepPositions();
        //setHistoricSheepPosition(7);

        map.addListener(new MapViewer.MapViewerListener() {
            @Override
            public void nodeClicked(MapViewer.NodeInfo n) {
                mapMarkers = map.getMapMarkers();
                dotTreeMap = map.getDotId();

                for(MapMarkerDot d : mapMarkers){
                    if(n.getDot() == d ){
                        MapListenerSheepId = getKeyByValue(dotTreeMap, d);
                    }
                }

            }

            @Override
            public void nodeDoubleClicked() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

        });
	}


    /**
     * Helper function to get sheepID.
     * To be used within listener.
     * @return
     */
    public int getMapListenerSheepID(){
        return this.MapListenerSheepId;
    }

    /**
     * Gets farmer center from register.
     * @return
     */
    public Vec2 getFarmerCenter(){
        return register.getFarmerPosition();
    }

    /**
     * adds all of the farmers sheep to currentSheeps.
     */
	public void setSheeps ()
	{
		currentSheeps = register.getAllFarmerSheeps();
	}



    /**
     * Adds the current position of all the sheep to the map.
     *
     */
    public void setCurrentSheepPositions(){
        map.removeMarkers();

        float lat, lon;
        int counter = 0;
        ArrayList<Vec2> positions = register.getSheepPositions();

        for (Vec2 v : positions){
            lat = v.x;
            lon = v.y;
            map.addMarker(currentSheeps.get(counter).getName(), lat, lon, currentSheeps.get(counter).getId());
            counter += 1;
        }
    }

    /**
     * Finds the three last positions of sheep and adds them to map.
     * @param sheepid
     */
    public void setHistoricSheepPosition(int sheepid){
        map.removeMarkers();

        float lat, lon;
        Color color = null;

        SheepHistory history = register.getSheepHistory(sheepid);
        TreeMap<Long, Vec2> sheepHistory = history.getHistory();

        Collection<Vec2> pairs = sheepHistory.values();
        Vec2[] array = new Vec2[pairs.size()];
        pairs.toArray(array);

        for (int i = 1; i < 4; i++){
            Vec2 position = array[array.length-i];
            lat = position.x;
            lon = position.y;

            System.out.println(lat + " " + lon);

            switch(i){
                case 1:
                    color = Color.YELLOW;
                    break;
                case 2:
                    color = Color.BLUE;
                    break;
                case 3:
                    color = Color.RED;
                    break;
            }
            map.addMarker(lat,lon,color);
        }
    }

    /**
     * Helper function to get the key from a TreeMap by value
     * @param map
     * @param value
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> T getKeyByValue(TreeMap<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
