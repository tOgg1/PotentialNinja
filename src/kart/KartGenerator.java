package kart;

public class KartGenerator 
{
	private String zoomlvl;
	private String dimensjonX;
	private String dimensjonY;
	private String center;
	
	public void setZoomlvl (String zoom)
	{
		zoomlvl = zoom;
	}
	
	public void setDimensjon (String dimX, String dimY)
	{
		dimensjonX = dimX;
		dimensjonY = dimY;
	}
	
	public void setCenter (String adresse)
	{
		center = adresse;
	}
	
	public String kart ()
	{
		return "http://maps.google.com/maps/api/staticmap?center="+center+"&zoom="+zoomlvl+"&size="+dimensjonX+"x"+dimensjonY+"&maptype=roadmap&sensor=true";
	}
}
