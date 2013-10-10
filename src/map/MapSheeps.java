package map;

import db.DatabaseHandler;
import main.Register;
import model.Sheep;
import util.Vec2;

import java.util.ArrayList;

public class MapSheeps 
{
	private Register register;
	private ArrayList <Sheep> currentSheeps;
	private int farmerId;
	private int [][] position; //kanskje, m� se mer p� det der etterp�, n�r eg komemr litt lengre
    private MapViewer map;
	
	public MapSheeps (DatabaseHandler handler, int farmerId, MapViewer map)
	{
		register = new Register (handler, farmerId);
		currentSheeps = new ArrayList ();
		this.farmerId = farmerId;
        this.map = map;

		setSheeps();
        setCurrentSheepPositions();
	}
	
	public void setSheeps ()
	{
		currentSheeps = register.getAllFarmerSheeps(farmerId);
	}
	
	public void setOldSheeps (ArrayList<Sheep> newSheep, ArrayList<Sheep> oldSheep) 
	{
		oldSheep = newSheep;
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
	
	
}
