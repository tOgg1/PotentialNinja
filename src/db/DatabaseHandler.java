package db;


import model.Alarm;
import model.Pos;
import model.Sheep;
import model.SheepHistory;
import util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * DatabaseHandler class, deals with getting and setting data
 *
 */
public class DatabaseHandler {

    private Connection db;

    public DatabaseHandler(){
        try{

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            db = DriverManager.getConnection(DatabaseSettings.db_url, DatabaseSettings.db_user, DatabaseSettings.db_pw);
            Log.d("Database", "It works!");

        }catch(ClassNotFoundException  | InstantiationException | IllegalAccessException | SQLException e){
            Log.e("Database", "You suck, " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Takes in an account-name and password, authenticates, and returns the farmerid
     * @param account
     * @param password
     * @return -1 is returned if farmer is not found, or there were trouble authenticating
     */
    public int authenticate(String account, String password){
        try {
            PreparedStatement query = db.prepareStatement("SELECT id, password FROM user WHERE username = ?");
            query.setString(1, account);
            ResultSet result = query.executeQuery();

            if(!result.next())
                return -1;

            String hash = encryptPassword(password);
            System.out.println(hash);
            if(hash.equals(result.getString("password")))
                return result.getInt("id");
            return -1;
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * Adds a sheep to the database
     * @param name
     * @param age
     * @param healthflags
     * @param pos_x
     * @param pos_y
     * @param ownerid
     */
    public void addSheep(String name, int age, int healthflags,  int pos_x, int pos_y, int ownerid){
        try {
            PreparedStatement query = db.prepareStatement("INSERT INTO sheep(name, age, healthflags, pos_x, pos_y, farmeid) VALUES(?,?,?,?,?)");
            query.setString(1,name);
            query.setInt(2, age);
            query.setInt(3, healthflags);
            query.setInt(4, pos_x);
            query.setInt(5, pos_y);
            query.setInt(6, ownerid);
            query.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Creates a farmer account, and returns the new farmer id of the account
     * @param accountname
     * @param password
     * @param farmerName
     * @param pos_x
     * @param pos_y
     * @return
     */
    public int createAccount(String accountname, String password, String farmerName, float pos_x, float pos_y){
        try {

            int id = getNextAutoIncrement("farmer", "id");

            PreparedStatement query = db.prepareStatement("INSERT INTO farmer(name, default_pos_x, default_pos_y) VALUES (?,?,?)");
            query.setString(1, farmerName);
            query.setFloat(2, pos_x);
            query.setFloat(3, pos_y);
            query.executeUpdate();

            query = db.prepareStatement("INSERT INTO user(id, username, password) VALUES (?,?,?)");
            query.setInt(1, id);
            query.setString(2, accountname);
            query.setString(3, encryptPassword(password));
            query.executeUpdate();

            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Fetches all farmer information for given farmer id
     * @param id
     * @return
     */
    public Object[] getFarmerInformation(int id){
        try {
            PreparedStatement query = db.prepareStatement("SELECT (name, default_pos_x, default_pos_y) FROM farmer WHERE id = ?");
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();

            if(!rs.next())
                return null;

            Object[] returnData = new Object[3];

            returnData[0] = rs.getString("name");
            returnData[1] = rs.getFloat("default_pos_x");
            returnData[2] = rs.getFloat("default_pos_y");

            return returnData;
        } catch (SQLException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Fetches all alarms
     * @param farmerID
     */
    public ArrayList<Alarm> getAlarms(int farmerID){
        try {
            PreparedStatement query = db.prepareStatement("SELECT * FROM alarm WHERE ? = (SELECT farmerid from sheep WHERE id = alarm.sheepid)");
            query.setInt(1, farmerID);
            ResultSet rs = query.executeQuery();

            if(!rs.next())
                return null;

            ArrayList<Alarm> results = new ArrayList<Alarm>();
            results.add(new Alarm(rs.getInt("alarmflags"), rs.getInt("sheepid")));

            while(rs.next()){
                results.add(new Alarm(rs.getInt("alarmflags"), rs.getInt("sheepid")));
            }

            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets all sheeps of farmer
     * @param farmerID
     * @return
     */
    public ArrayList<Sheep> getSheeps(int farmerID){
        try{
            PreparedStatement query = db.prepareStatement("SELECT * FROM sheep WHERE farmerid = ?");
            query.setInt(1,farmerID);
            ResultSet rs = query.executeQuery();

            if(!rs.next())
                return null;
            ArrayList<Sheep> results = new ArrayList<Sheep>();
            results.add(new Sheep(rs.getInt("id"), rs.getInt("age"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid")));

            while(rs.next()){
                results.add(new Sheep(rs.getInt("id"), rs.getInt("age"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid")));
            }

            return results;
        } catch (SQLException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Gets all history of sheep
     * @param sheepid
     * @return
     */
    public SheepHistory getSheepHistory(int sheepid){
        try{
            PreparedStatement query = db.prepareStatement("SELECT * FROM sheephistory WHERE sheepid = ?");
            query.setInt(1,sheepid);
            ResultSet rs = query.executeQuery();

            if(!rs.next())
                return null;

            TreeMap<Long, Pos> posHistory = new TreeMap<Long, Pos>();
            posHistory.put(rs.getLong("timestamp"), new Pos(rs.getFloat("pos_x"), rs.getFloat("pos_y")));

            while(rs.next()){
                posHistory.put(rs.getLong("timestamp"), new Pos(rs.getFloat("pos_x"), rs.getFloat("pos_y")));
            }

            return new SheepHistory(posHistory, sheepid);

        } catch (SQLException e) {
            e.printStackTrace();

            return null;
        }
    }

    public void setSheepHealthFlag(){

    }

    private int getNextAutoIncrement(String table, String column) throws SQLException{
        PreparedStatement query = db.prepareStatement("SELECT MAX("+column+") as max FROM "+table);
        ResultSet rs = query.executeQuery();
        rs.next();
        return rs.getInt("max")+1;
    }

    private String encryptPassword(String string){
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(string.getBytes());
            StringBuffer buffer = new StringBuffer();
            for(int i = 0; i < result.length; i++){
                buffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            return "NINJA!";
        }
    }
}
