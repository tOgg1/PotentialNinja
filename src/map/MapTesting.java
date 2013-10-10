package map;

import db.DatabaseHandler;
import main.Register;
import model.Sheep;
import util.Vec2;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: FF63
 * Date: 9/23/13
 * Time: 1:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class MapTesting extends JFrame {

    private MapViewer map;
    private float lat,lon;

    /**
     * Creates a JFrame containing map.
     */
    public MapTesting() throws SQLException {
        super("Map Viewer");
        map = new MapViewer();

        DatabaseHandler db = new DatabaseHandler();


        int farmerid = db.authenticate("farm","farm");
        Register register = new Register(db, farmerid);

        ArrayList<Vec2> position = new ArrayList<>();
        ArrayList<Sheep> activeSheep = db.getSheeps(farmerid);

        for (Sheep s : activeSheep){
            position.add(db.getSheepPosition(s.getId()));
        }

        int counter = 0;

        for (Vec2 v : position){
          this.lon = v.y;
          this.lat = v.x;
          map.addMarker(activeSheep.get(counter).getName(),this.lat,this.lon);
          counter += 1;
        }


        add(map.getMap(), BorderLayout.CENTER);

        setSize(700,700);
        setVisible(true);

        //System.exit(1);

    }
    

    public static void main(String[] args) throws SQLException {

        new MapTesting();

    }
}
