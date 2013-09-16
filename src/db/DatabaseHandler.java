package db;


import model.Alarm;
import model.Pos;
import model.Sheep;
import model.SheepHistory;
import util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

/**
 * DatabaseHandler class, deals with getting and setting data in the database.
 *
 */
public class DatabaseHandler {

    private Connection db;

    public DatabaseHandler(){
        try{

            //Connects to database
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.db = DriverManager.getConnection(DatabaseSettings.db_url, DatabaseSettings.db_user, DatabaseSettings.db_pw);
            Log.d("Database", "Database connection established");

        }catch(ClassNotFoundException  | InstantiationException | IllegalAccessException | SQLException e){
            Log.e("Database", "You suck, " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            //Closing connection
            this.db.close();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Takes in an account-name and password, authenticates, and returns the farmerid
     * @param account
     * @param password
     * @return -1 is returned if farmer is not found, or if there were trouble authenticating
     */
    public int authenticate(String account, String password) {
        try {
            PreparedStatement query = this.db.prepareStatement("SELECT id, password FROM user WHERE username = ?");
            query.setString(1, account);
            ResultSet result = query.executeQuery();

            if(!result.next())
                return -1;

            String hash = encryptPassword(password);
            if(hash.equals(result.getString("password")))
                return result.getInt("id");
            return -1;
        } catch (SQLException | NoSuchAlgorithmException e) {
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
    public void addSheep(String name, int age, int healthflags, float pos_x, float pos_y, int ownerid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("INSERT INTO sheep(name, age, healthflags, pos_x, pos_y, farmerid) VALUES(?,?,?,?,?,?)");
        query.setString(1,name);
        query.setInt(2, age);
        query.setInt(3, healthflags);
        query.setFloat(4, pos_x);
        query.setFloat(5, pos_y);
        query.setInt(6, ownerid);
        query.executeUpdate();
    }

    /**
     * Adds an alarm to the database
     * @param sheepid
     * @param alarmflag
     * @throws SQLException
     */
    public void addAlarm(int sheepid, int alarmflag) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("INSERT INTO alarm(alarmflags, sheepid, isactive) VALUES(?,?,?)");
        query.setInt(1, alarmflag);
        query.setInt(2, sheepid);
        query.setInt(3, 1);
        query.executeUpdate();
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
    public int createAccount(String accountname, String password, String farmerName, float pos_x, float pos_y) throws SQLException{
        try {

            int id = getNextAutoIncrement("farmer", "id");

            PreparedStatement query = this.db.prepareStatement("INSERT INTO farmer(name, default_pos_x, default_pos_y) VALUES (?,?,?)");
            query.setString(1, farmerName);
            query.setFloat(2, pos_x);
            query.setFloat(3, pos_y);
            query.executeUpdate();

            query = this.db.prepareStatement("INSERT INTO user(id, username, password) VALUES (?,?,?)");
            query.setInt(1, id);
            query.setString(2, accountname);
            query.setString(3, encryptPassword(password));
            query.executeUpdate();

            return id;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Fetches all farmer information for given farmer id
     * @param id
     * @return Returns an Object[] array of length three as follows {name, posx, posy}, returns null if farmer is not found
     * @throws SQLException
     */
    public Object[] getFarmerInformation(int id) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT name, default_pos_x, default_pos_y FROM farmer WHERE id = ?");
        query.setInt(1, id);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        return new Object[]{rs.getString("name"), rs.getFloat("default_pos_x"), rs.getFloat("default_pos_y")};
    }

    /**
     * Fetches all information about given farmers contact
     * @param id
     * @return Returns an Object[] array of length three as follows {name, number, email}, returns null if a contact is not found
     * @throws SQLException
     */
    public Object[] getFarmerContactInformation(int id) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT name, number, email FROM contact WHERE id = (SELECT Contact_id from farmer where farmer.id = ?)");
        query.setInt(1, id);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        return new Object[]{rs.getString("name"), rs.getString("number"), rs.getString("email")};
    }

    /**
     *
     * @param name
     * @param number
     * @param email
     * @return Returns false if an error occurs
     */
    public void setFarmerContact(int farmerID, String name, String number, String email) throws SQLException{
        int id = getNextAutoIncrement("Contact", "id");
        PreparedStatement query = this.db.prepareStatement("INSERT INTO contact(name, number, email) VALUES (?,?,?)");
        query.setString(1,name);
        query.setString(2,number);
        query.setString(3,email);
        query.executeUpdate();

        query = this.db.prepareStatement("UPDATE farmer SET Contact_id = ? WHERE id = ?");
        query.setInt(1, id);
        query.setInt(2, farmerID);
        query.executeUpdate();
    }

    /**
     *
     * @param sheepid
     * @return  Returns an Object[] array of length four as follows {age, mileage, healthflags, name}, returns null if sheep is not found
     * @throws SQLException
     */
    public Object[] getSheepInformation(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT age, mileage, healthflags, name FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        return new Object[]{rs.getInt("age"), rs.getInt("mileage"), rs.getInt("healthflags"), rs.getString("name")};

    }

    /**
     * Gets all alarms to sheeps that are owned by a farmer
     * @param farmerID
     */
    public ArrayList<Alarm> getAlarms(int farmerID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM alarm WHERE ? = (SELECT farmerid from sheep WHERE id = alarm.sheepid)");
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
    }

    /**
     * Gets all sheeps of farmer
     * @param farmerID
     * @return
     */
    public ArrayList<Sheep> getSheeps(int farmerID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheep WHERE farmerid = ?");
        query.setInt(1,farmerID);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        ArrayList<Sheep> results = new ArrayList<Sheep>();
        results.add(new Sheep(rs.getInt("id"), rs.getInt("age"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getString("name")));

        while(rs.next()){
            results.add(new Sheep(rs.getInt("id"), rs.getInt("age"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getString("name")));
        }

        return results;
    }

    /**
     * Gets all history of sheep
     * @param sheepid
     * @return
     */
    public SheepHistory getSheepHistory(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheephistory WHERE sheepid = ?");
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
    }

    /**
     * Gets a sheeps current position
     * @param sheepid
     * @return
     * @throws SQLException
     */
    public Pos getSheepPosition(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT pos_x, pos_y FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        return new Pos(rs.getFloat("pos_x"), rs.getFloat("pos_y"));
    }

    /**
     * Sets a sheeps current position, stores the previous position in sheephistory
     * @param sheepid
     * @param posX
     * @param posY
     * @throws SQLException
     */
    public void setSheepPosition(int sheepid, float posX, float posY) throws SQLException{
        //Get current position
        PreparedStatement query = this.db.prepareStatement("SELECT pos_x, pos_y FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            throw new SQLException("Unable to find sheep");

        float tempX, tempY;
        tempX = rs.getFloat("pos_x");
        tempY = rs.getFloat("pos_y");

        //Store current position in history                                                              h
        query = this.db.prepareStatement("INSERT INTO sheephistory(sheepid, pos_x, pos_y, timestamp) VALUES (?,?,?,?)");

        long timeStamp = (new java.util.Date()).getTime();
        query.setInt(1, sheepid);
        query.setFloat(2, tempX);
        query.setFloat(3, tempY);
        query.setLong(4, timeStamp);
        query.executeUpdate();

        //Set new position
        query = this.db.prepareStatement("UPDATE sheep SET pos_x = ?, pos_y = ? WHERE id = ?");
        query.setFloat(1, posX);
        query.setFloat(2, posY);
        query.setInt(3, sheepid);
        query.executeUpdate();
    }

    /**
     * Sets the sheep's health flag, overrides all current healthflags, use with caution.
     */
    public void setSheepHealthFlag(int sheepid, int flag) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE sheep SET healthflags = ? WHERE id = ?");
        query.setInt(1, flag);
        query.setInt(2, sheepid);
        query.executeUpdate();
    }

    /**
     * Adds a setting to the sheep's health flag
     * @return
     */
    public void addSheepHealthFlag(int sheepid, int flag) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT healthflags FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            throw new SQLException("Couldn't find sheep");

        int mFlags = rs.getInt("healthflags");
        mFlags |= flag;

        this.setSheepHealthFlag(sheepid, mFlags);
    }

    /**
     * Find the next autoincrement of column 'column' in table 'table'
     * @param table
     * @param column
     * @return
     * @throws SQLException
     */
    private int getNextAutoIncrement(String table, String column) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT MAX("+column+") as max FROM "+table);
        ResultSet rs = query.executeQuery();
        rs.next();
        return rs.getInt("max")+1;
    }

    /**
     * Encrypt a password with the SHA1 algorithm
     * @param string
     * @return
     */
    private String encryptPassword(String string) throws NoSuchAlgorithmException{
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(string.getBytes());
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < result.length; i++){
            buffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return buffer.toString();
    }
}
