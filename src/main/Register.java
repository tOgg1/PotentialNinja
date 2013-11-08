package main;

import db.DatabaseHandler;
import model.Sheep;
import model.SheepHistory;
import util.Vec2;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Main storage for runtime information.
 */
public class Register {

    private DatabaseHandler mHandler;
    private ArrayList<Sheep> deadSheep;
    private ArrayList<Sheep> activeSheeps;

    private int farmerID;

    private Vec2 pos;

    public Register(DatabaseHandler handler, int farmerID){
        this.mHandler = handler;
        this.farmerID = farmerID;

        try {
            //this.activeSheeps = handler.getSheeps(this.farmerID);
            this.activeSheeps = handler.getAliveSheeps(this.farmerID);
            this.deadSheep = handler.getDeadSheeps(this.farmerID);
        } catch (SQLException e) {
            activeSheeps = new ArrayList<Sheep>();
        }

        try {
            this.pos = handler.getFarmerLocation(this.farmerID);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Checks if register contains sheep by class.
     * @param sheep Sheep object.
     * @return Returns true if the register contains the sheep.
     */
    public boolean containsSheep(Sheep sheep){
        return this.activeSheeps.contains(sheep);
    }

    /**
     * Checks if register contains sheep by id.
     * @param sheepId Sheep's id.
     * @return Returns true if the register contains the sheep.
     */
    public boolean containsSheepById(int sheepId){
        for(Sheep s : this.activeSheeps){
            if(s.getId() == sheepId)
                return true;
        }
        return false;
    }

    /**
     * Checks if register contains sheep by name.
     * @param name Sheep's name.
     * @return Returns sheep's id if sheep is found, and -1 if not found.
     */
    public int getSheepIdByName(String name){
        for (Sheep s : this.activeSheeps){
            if(name.equals(s.getName())){
               return s.getId();
            }
        }
        return -1;
    }

    /**
     * Takes a sheepid and returns its name if sheep is found.
     * @param sheepid Sheep's id.
     * @return Returns sheepname, and null if no sheep is found
     */
    public String getSheepName(int sheepid){
        for(Sheep s : this.activeSheeps){
            if(s.getId() == sheepid){
                return s.getName();
            }
        }

        for(Sheep s : this.deadSheep){
            if(s.getId() == sheepid){
                return s.getName();
            }
        }
        return null;
    }

    /**
     * Gets sheep by id if it exists. Returns null if sheep is not found
     * @param id
     */
    public Sheep getSheepById(int id){
        for(Sheep s : this.activeSheeps){
            if(s.getId() == id)
                return s;
        }
        return null;
    }

    public Sheep getSheepByName(String name){
        for(Sheep s : this.activeSheeps){
            if(s.getName().equals(name))
                return s;
        }
        return null;
    }

    /**
     * Get all sheeps for farmerID. Returns null if no sheeps are found
     * @return  Returns arraylist with sheep.
     */
    public ArrayList<Sheep> getAllSheeps(){
        if(this.activeSheeps == null)
        {
            return new ArrayList<Sheep>();
        }
        return activeSheeps;
    }

    /**
     * Gets farmer's id.
     * @return farmerid.
     */
    public int getFarmerID(){
        return farmerID;
    }

    /**
     * Fetches sheep positions from database.
     * @return Returns arraylist with sheeppositions
     */
    public ArrayList<Vec2> getSheepPositions(){
        try{
            ArrayList<Vec2> positions = new ArrayList<Vec2>();
            ArrayList<Sheep> farmerSheep = this.getAllSheeps();

            for(Sheep s : farmerSheep){
                int sheepID = s.getId();
                positions.add(mHandler.getSheepPosition(sheepID));
            }
            return positions;

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets sheep's history from sheepid.
     * @param sheepID Sheep's id.
     * @return SheepHistory object.
     */
    public SheepHistory getSheepHistory(int sheepID){
        try {
            return mHandler.getSheepHistory(sheepID);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    /**
     * Returns the Vec2 position of the farmer.
     * @return Vec2 object.
     */
    public Vec2 getFarmerLocation(){
        return this.pos;
    }

    /**
     * Sets farmer location.
     * @param x
     * @param y
     */
    public void setFarmerLocation(double x, double y){
        try {
            this.mHandler.setFarmerLocation(this.farmerID, x, y);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.pos.x = x;
        this.pos.y = y;
    }


    /**
     * Updates register with new sheeps.
     */
    public void reFetchSheeps(){
        try {
            this.activeSheeps = this.mHandler.getAliveSheeps(this.farmerID);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Updates register with farmer position.
     */
    public void reFetchFarmerPos(){
        try{
            this.pos = this.mHandler.getFarmerLocation(this.farmerID);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
