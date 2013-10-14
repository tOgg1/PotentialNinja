package map;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import db.DatabaseHandler;
import model.Sheep;
import main.Register;
import model.SheepHistory;
import util.Vec2;

public class MapSheeps
{
	private Register register;
	private ArrayList <Sheep> currentSheeps;
	private int farmerId;
    private MapViewer map;

	
	public MapSheeps (DatabaseHandler handler, int farmerId, MapViewer map)
	{
		register = new Register (handler, farmerId);
		currentSheeps = new ArrayList<Sheep> ();
		this.farmerId = farmerId;
        this.map = map;

		setSheeps();
        setCurrentSheepPositions();
        //setHistoricSheepPosition(7);
	}

    /**
     * adds all of the farmers sheep to currentSheeps.
     */
	public void setSheeps ()
	{
		currentSheeps = register.getAllFarmerSheeps(farmerId);
	}

    /**
     * Adds the current position of all the sheep to the map.
     *
     */
    public void setCurrentSheepPositions(){
        float lat, lon;
        int counter = 0;
        ArrayList<Vec2> positions = register.getSheepPositions(farmerId);

        for (Vec2 v : positions){
            lat = v.x;
            lon = v.y;
            map.addMarker(currentSheeps.get(counter).getName(), lat, lon);
            counter += 1;
        }
    }

    /**
     * Finds the three last positions of sheep and adds them to map.
     * @param sheepid
     */
    public void setHistoricSheepPosition(int sheepid){
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
	
	
}
