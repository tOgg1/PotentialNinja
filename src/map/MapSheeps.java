package map;

import java.util.ArrayList;
import db.DatabaseHandler;
import model.Sheep;
import main.Register;

public class MapSheeps 
{
	private Register register;
	private ArrayList <Sheep> currentSheeps;
	private int farmerId;
	private int [][] position; //kanskje, må se mer på det der etterpå, når eg komemr litt lengre
	
	public MapSheeps (DatabaseHandler handler, int farmerId)
	{
		register = new Register (handler);
		currentSheeps = new ArrayList ();
		this.farmerId = farmerId;
		setSheeps();
	}
	
	public void setSheeps ()
	{
		currentSheeps = register.getAllFarmerSheeps(farmerId);
	}
	
	public void setOldSheeps (ArrayList<Sheep> newSheep, ArrayList<Sheep> oldSheep) 
	{
		oldSheep = newSheep;
	}
	
	
}
