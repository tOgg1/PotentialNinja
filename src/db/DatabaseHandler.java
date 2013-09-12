package db;


import model.Alarm;
import model.Sheep;
import util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

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
            PreparedStatement query = db.prepareStatement("SELECT password, Farmer_id FROM user WHERE username = ?");
            query.setString(0, account);
            ResultSet result = query.executeQuery();

            if(!result.next())
                return -1;

            String hash = encryptPassword(password);
            if(hash.equals(result.getString("password")))
                return result.getInt("Farmer_id");
            return -1;
        } catch (SQLException e) {
            return -1;
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
            query.setString(0, farmerName);
            query.setFloat(1, pos_x);
            query.setFloat(2, pos_y);

            query = db.prepareStatement("INSERT INTO user(id, username, password) VALUES (?,?,?)");
            query.setInt(0, id);
            query.setString(1, accountname);
            query.setString(2, encryptPassword(password));
            query.executeUpdate();

            return id;
        } catch (SQLException e) {
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
            query.setInt(0, id);
            ResultSet rs = query.executeQuery();

            if(!rs.next())
                return null;

            Object[] returnData = new Object[3];

            returnData[0] = rs.getString("name");
            returnData[1] = rs.getFloat("default_pos_x");
            returnData[2] = rs.getFloat("default_pos_y");

            return returnData;
        } catch (SQLException e) {
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
            query.setInt(0, farmerID);
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
            return null;
        }
    }

    public ArrayList<Sheep> getSheeps(int farmerID){
        try{
            PreparedStatement query = db.prepareStatement("SELECT * FROM sheep WHERE farmerid = ?");
            query.setInt(0,farmerID);
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
            return null;
        }
    }

    public void getSheepHistory(){

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
            return "NINJA!";
        }
    }
}
