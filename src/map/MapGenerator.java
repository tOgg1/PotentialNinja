package map;

public class MapGenerator
{
	private String zoomlvl;
	private String dimensionX;
	private String dimensionY;
	private String center;
	
	public void setZoomlvl (String zoom)
	{
		zoomlvl = zoom;
	}
	
	public void setDimension (String dimX, String dimY)
	{
		dimensionX = dimX;
		dimensionY = dimY;
	}
	
	public void setCenter (String center)
	{
		this.center = center;
	}
	
	public String generateMap ()
	{
		return "http://maps.google.com/maps/api/staticmap?center="+center+"&zoom="+zoomlvl+"&size="+dimensionX+"x"+dimensionY+"&maptype=roadmap&sensor=true";
	}
}
