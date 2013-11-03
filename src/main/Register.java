package main;

import db.DatabaseHandler;
import model.Sheep;
import model.SheepHistory;
import util.Vec2;

import java.sql.SQLException;
import java.util.ArrayList;

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
     * Checks if register contains sheep by class
     * @param sheep
     * @return
     */
    public boolean containsSheep(Sheep sheep){
        return this.activeSheeps.contains(sheep);
    }

    /**
     * Checks if register contains sheep by id
     * @param sheepId
     * @return
     */
    public boolean containsSheepById(int sheepId){
        for(Sheep s : this.activeSheeps){
            if(s.getId() == sheepId)
                return true;
        }
        return false;
    }

    /**
     * Checks if register contains sheep by name
     * @param name
     * @return
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
     * Takes a sheepid and returns its name if sheep is found
     * @param sheepid
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
     * Adds sheep by class
     * @param sheep
     */
    public void addSheep(Sheep sheep){
        this.activeSheeps.add(sheep);
    }

   /**
     * Fetches sheep from database by id.
     * @param sheepId
     */
    public void addSheepById(int sheepId){
        try {
            Sheep sheep = mHandler.getSheep(sheepId);
            if(sheep != null)
                 this.activeSheeps.add(sheep);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Fetches all sheeps for farmer from database.
     */
    public void addSheepsByFarmerId(){
        try {
            ArrayList<Sheep> farmerSheeps = mHandler.getSheeps(this.farmerID);
            if(!farmerSheeps.isEmpty())
                this.activeSheeps.addAll(farmerSheeps);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
     * @return
     */
    public ArrayList<Sheep> getAllSheeps(){
        if(this.activeSheeps == null)
        {
            return new ArrayList<Sheep>();
        }
        return activeSheeps;
    }

    public int getFarmerID(){
        return farmerID;
    }

    public void setFarmerID(int farmerID){
        this.farmerID = farmerID;
    }

    /**
     * Fetches sheep positions from database.
     * @return
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
     * Helper method to grab sheep history
     * @param sheepID
     * @return
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
     * Returns the Vec2 position of the farmer
     * @return
     */
    public Vec2 getFarmerLocation(){
        return this.pos;
    }

    public void setFarmerLocation(double x, double y){
        try {
            this.mHandler.setFarmerLocation(this.farmerID, x, y);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.pos.x = x;
        this.pos.y = y;
    }


    public void reFetchSheeps(){
        try {
            this.activeSheeps = this.mHandler.getAliveSheeps(this.farmerID);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void reFetchFarmerPos(){
        try{
            this.pos = this.mHandler.getFarmerLocation(this.farmerID);
        } catch (SQLException e){

        }

    }


}
