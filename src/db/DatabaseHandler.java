package db;


import model.Alarm;
import model.Sheep;
import model.SheepHistory;
import model.SheepMedicalHistory;
import util.GeneralUtil;
import util.Log;
import util.NothingToSeeHere;
import util.Vec2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

/**
 * DatabaseHandler class, deals with getting and setting data in the database.
 *
 */
public class DatabaseHandler {

    /**
     * Database connection parameter
     */
    private Connection db;

    public DatabaseHandler(){
        try{
            //Connects to database
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.db = DriverManager.getConnection(DatabaseSettings.db_url, NothingToSeeHere.f(DatabaseSettings.db_user), NothingToSeeHere.f(DatabaseSettings.db_pw));
            Log.d("Database", "Database connection established");

        }catch(ClassNotFoundException  | InstantiationException | IllegalAccessException | SQLException e){
            Log.e("Database", "You suck, " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closes database connection
     */
    public void close(){
        try {
            this.db.close();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    /**
     * Takes in an account-name and password, authenticates, and returns the farmerid
     * @param account The account name
     * @param password The password, this will be SHA-1 encrypted.
     * @return -1 is returned if farmer is not found, or if there were trouble authenticating
     */
    public int authenticate(String account, String password) {
        try {
            PreparedStatement query = this.db.prepareStatement("SELECT id, password FROM user WHERE username = ?");
            query.setString(1, account);
            ResultSet rs = query.executeQuery();

            if(!rs.next())
                return -1;

            String hash = encryptPassword(password);
            if(hash.equals(rs.getString("password")))
                return this.getFarmerId(rs.getInt("id"));
            return -1;
        } catch (SQLException | NoSuchAlgorithmException e) {
            return -1;
        }
    }

    /**
     * Simple reset of password. Takes a farmerid and updates the corresponding user.
     * @param farmerid The farmerid
     * @param newPassword The new password
     * @throws SQLException
     */
    public void resetPassword(int farmerid, String newPassword) throws SQLException {
        String hashedPassword;
        try {
            hashedPassword = this.encryptPassword(newPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new SQLException("Hashing algorithm failed (SHA-1 not available). Contact system administrator.");
        }
        PreparedStatement query = this.db.prepareStatement("UPDATE user SET password = ? WHERE farmerid = ?");
        query.setString(1, hashedPassword);
        query.setInt(2, farmerid);
        query.executeUpdate();
    }

    /**
     * Adds a sheep to the database
     * @param name Sheep name
     * @param healthflags Sheeps healthflags
     * @param pos_x Sheeps latitude
     * @param pos_y Sheeps longitude
     * @param farmerid Sheeps farmer
     */
    public void addSheep(String name, long birthdate, int healthflags, double pos_x, double pos_y, String sex, int farmerid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("INSERT INTO sheep(name, birthdate, healthflags, pos_x, pos_y, farmerid, sex, alive) VALUES(?,?,?,?,?,?,?,?)");
        query.setString(1, name);
        query.setLong(2, birthdate);
        query.setInt(3, healthflags);
        query.setDouble(4, pos_x);
        query.setDouble(5, pos_y);
        query.setInt(6, farmerid);
        query.setString(7, sex);
        query.setBoolean(8, true);
        query.executeUpdate();

        int sheepid = getSheepByName(name, farmerid).getId();

        addToSheepHealthHistory(sheepid, healthflags);

        updateState();
    }

    /**
     * Adds sheep to database, neglecting sheep position
     *
     * @param name Sheeps name
     * @param birthdate Sheeps birthdate
     * @param healthflags Sheeps healthflags
     * @param farmerid Sheeps farmer
     * @throws SQLException
     */
    public void addSheep(String name, long birthdate, int healthflags, String sex, int farmerid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("INSERT INTO sheep(name, birthdate, healthflags, farmerid, sex, alive) VALUES(?,?,?,?,?,?)");
        query.setString(1,name);
        query.setLong(2, birthdate);
        query.setInt(3, healthflags);
        query.setFloat(4, farmerid);
        query.setString(5, sex);
        query.setBoolean(6, true);
        query.executeUpdate();
        updateState();
    }

    /**
     * Wrapper function that takes a sheep-object and adds it to the database.
     * @param sheep The sheep-object to be added.
     * @throws SQLException
     */
    public void addSheep(Sheep sheep)throws SQLException{
        this.addSheep(sheep.getName(), sheep.getBirthdate(), sheep.getHealthflags(), sheep.getPos().x, sheep.getPos().y, sheep.getSex(), sheep.getFarmerid());
    }

    /**
     * Adds an alarm to the database
     * @param sheepid Sheep's id
     * @param alarmflag Alarmflags, cause of alarm.
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
     * @param accountname Name of account
     * @param password Password, this will be SHA-1 encrypted.
     * @param farmerName Farmers name
     * @param pos_x Latitude of farm
     * @param pos_y Longitude of farm
     * @return
     */
    public int createAccount(String accountname, String password, String farmerName, double pos_x, double pos_y) throws SQLException{
        try {
            int id = getNextAutoIncrement("farmer");

            PreparedStatement query = this.db.prepareStatement("INSERT INTO farmer(name, default_pos_x, default_pos_y) VALUES (?,?,?)");
            query.setString(1, farmerName);
            query.setDouble(2, pos_x);
            query.setDouble(3, pos_y);
            query.executeUpdate();

            query = this.db.prepareStatement("INSERT INTO user(username, password, farmerid) VALUES (?,?,?)");
            query.setString(1, accountname);
            query.setString(2, encryptPassword(password));
            query.setInt(3, id);
            query.executeUpdate();
            updateState();
            return id;
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Takes in userid and returns farmerid
     * @param userid Accountid of farmer
     * @return integer containing farmerid
     * @throws SQLException
     */
    public int getFarmerId(int userid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT farmerid FROM user WHERE id = ?");
        query.setInt(1, userid);
        ResultSet rs = query.executeQuery();

        if(!rs.next()){
            return -1;
        }
        return rs.getInt("farmerid");
    }

    /**
     * Tries to get farmerid from email and/or number
     * @param email Farmers email
     * @param number Farmers telephone number
     * @return
     */
    public int getFarmerId(String email, String number) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT id FROM farmer WHERE email = ? or number = ?");
        query.setString(1, email);
        query.setString(2, number);

        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return -1;

        return rs.getInt("id");
    }

    /**
     * Fetches all farmer information for given farmer id
     * @param id Farmers id
     * @return Returns an Object[] array of length three as follows {name, number, email}, returns null if farmer is not found
     * @throws SQLException
     */
    public Object[] getFarmerInformation(int id) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT name, email, number FROM farmer WHERE id = ?");
        query.setInt(1, id);
        ResultSet rs = query.executeQuery();

        Log.d("Database", "Getting farmer information");

        if(!rs.next())
            return null;
        return new Object[]{rs.getString("name"), rs.getString("number"), rs.getString("email"),};
    }

    /**
     * Gets farmers email
     * @param farmerid Farmers id
     * @return Farmers email
     * @throws SQLException
     */
    public String getFarmerEmail(int farmerid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT email FROM farmer WHERE id = ?");
        query.setInt(1, farmerid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        return rs.getString("email");
    }

    /**
     * Fetches all information about given farmers contact
     * @param id Farmers id
     * @return Returns an Object[] array of length three as follows {contactname, contactnumber, contactemail}, returns null if a contact is not found
     * @throws SQLException
     */
    public Object[] getFarmerContactInformation(int id) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT name, number, email FROM contact WHERE id = (SELECT contact_id from farmer where farmer.id = ?)");
        query.setInt(1, id);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        return new Object[]{rs.getString("name"), rs.getString("number"), rs.getString("email")};
    }

    /**
     * Gets farmers username
     * @param farmerid Farmers id
     * @return Returns null if no username for given farmerid is found
     * @throws SQLException
     */
    public String getFarmerUsername(int farmerid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT username FROM user WHERE farmerid = ?");
        query.setInt(1, farmerid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        return rs.getString("username");
    }

    /**
     * Set farmers contact
     * @param name Contact's name
     * @param number Contact's telephone number
     * @param email Contact's email
     */
    public void setFarmerContact(int farmerID, String name, String number, String email) throws SQLException{
        int id = getNextAutoIncrement("contact");

        PreparedStatement query = this.db.prepareStatement("INSERT INTO contact(name, number, email) VALUES (?,?,?)");
        query.setString(1, name);
        query.setString(2, number);
        query.setString(3, email);
        query.executeUpdate();

        query = this.db.prepareStatement("UPDATE farmer SET contact_id = ? WHERE id = ?");
        query.setInt(1, id);
        query.setInt(2, farmerID);
        query.executeUpdate();
        updateState();
    }

    /**
     * Updates farmers contact
     * @param farmerID Farmer's id
     * @param name Contact's name
     * @param number Contact's telephone number
     * @param email Contact's email
     * @throws SQLException
     */
    public void updateFarmerContact(int farmerID, String name, String number, String email) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE contact SET name = ?, number = ?, email = ? WHERE id = (SELECT contact_id FROM farmer WHERE id = ?)");
        query.setString(1, name);
        query.setString(2, number);
        query.setString(3, email);
        query.setInt(4, farmerID);

        query.executeUpdate();
        updateState();
    }

    /**
     * Checks if farmer has a contact present in the system
     * @param farmerID Farmer's id
     * @return True if farmer has a contact, false if not, or if farmer is not found.
     * @throws SQLException
     */
    public boolean hasFarmerContact(int farmerID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT ISNULL(contact_id) FROM farmer WHERE id = ?");
        query.setInt(1, farmerID);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return false;

        return !rs.getBoolean(1);
    }

    /**
     * Set farmers information
     * @param farmerid Farmer's id
     * @param username Farmer's new username
     * @param name Farmer's new name
     * @param number Farmer's new telephone number
     * @param email Farmer's new email
     * @throws SQLException
     */
    public void setFarmerInformation(int farmerid, String username, String name, String number, String email)throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE farmer SET name = ?, number = ?, email = ? WHERE id = ? ");
        query.setString(1, name);
        query.setString(2, number);
        query.setString(3, email);
        query.setInt(4, farmerid);
        query.executeUpdate();


        query = this.db.prepareStatement("UPDATE user SET username = ? WHERE farmerid = ?");
        query.setString(1, username);
        query.setInt(2, farmerid);
        query.executeUpdate();
    }

    /**
     * Sets farmer location.
     * @param farmerid Farmer's id
     * @param pos_x Farmer's farm's latitude
     * @param pos_y Farmer's farm's longitude
     * @throws SQLException
     */
    public void setFarmerLocation(int farmerid, double pos_x, double pos_y) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE farmer SET default_pos_x = ?, default_pos_y = ? WHERE id = ?");
        query.setDouble(1, pos_x);
        query.setDouble(2, pos_y);
        query.setInt(3, farmerid);
        query.executeUpdate();
    }

    /**
     *
     * @param id Farmer's id
     * @return Returns a Vec2-object with the farmer's farm's position
     * @throws SQLException
     */
    public Vec2 getFarmerLocation(int id) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT default_pos_x, default_pos_y FROM farmer WHERE id = ?");
        query.setInt(1, id);
        ResultSet rs = query.executeQuery();

        if (!rs.next()){
            return null;
        }

        return new Vec2(rs.getFloat("default_pos_x"), rs.getFloat("default_pos_y"));
    }

    /**
     * Checks if a sheep is dead
     * @param sheepid Sheep's id
     * @return true if sheep is dead, false if sheep is not found or sheep is not dead.
     * @throws SQLException
     */
    public boolean isSheepDead(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT alive FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return false;
        return !rs.getBoolean("alive");

    }

    /**
     * Flags a sheep as dead
     * @param sheepid Sheep's id
     * @return true if sheep was succesfully killed, false if not.
     * @throws SQLException
     */
    public boolean killSheep(int sheepid, int cause) throws SQLException{

        if(isSheepDead(sheepid))
            return true;

        Sheep sheep = this.getSheep(sheepid);
        Vec2 pos = this.getSheepPosition(sheepid);
        PreparedStatement query = this.db.prepareStatement("UPDATE sheep SET alive = ? WHERE id = ?");
        query.setBoolean(1, false);
        query.setInt(2, sheepid);

        if(!(query.executeUpdate() > 0)){
            return false;
        }

        query = this.db.prepareStatement("INSERT INTO deadsheep(sheepid, timeofdeath, causeofdeath) VALUES(?,?,?)");
        query.setInt(1, sheepid);
        query.setLong(2, (new Date()).getTime());
        query.setInt(3, cause);
        query.executeUpdate();
        return true;
    }

    /**
     * Revives a sheep that is dead
     * @param sheepid Sheep's id
     * @return True if sheep was succesfully revived, false if not.
     * @throws SQLException
     */
    public boolean reviveSheep(int sheepid) throws SQLException{

        //Sheep is already alive, hurray!
        if(!isSheepDead(sheepid))
            return true;

        PreparedStatement query = this.db.prepareStatement("UPDATE sheep SET alive = ? WHERE id = ?");
        query.setBoolean(1, true);
        query.setInt(2, sheepid);

        if(!(query.executeUpdate() > 0)){
            return false;
        }

        query = this.db.prepareStatement("DELETE FROM deadsheep WHERE sheepid = ?");
        query.setInt(1, sheepid);
        query.executeUpdate();
        updateState();
        return true;
    }

    /**
     * Fetches all active alarms in database
     * @return An arraylist of the active alarms. null of no such alarms is found.
     * @throws SQLException
     */
    public ArrayList<Alarm> getAllActiveAlarms() throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM alarm WHERE isactive = ?");
        query.setInt(1, 1);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        ArrayList<Alarm> results = new ArrayList<Alarm>();
        results.add(new Alarm(rs.getInt("id"), rs.getInt("alarmflags"), rs.getInt("sheepid")));

        while(rs.next()){
            results.add(new Alarm(rs.getInt("id"), rs.getInt("alarmflags"), rs.getInt("sheepid")));
        }
        return results;
    }

    /**
     * Flags an alarm as active
     * @param alarmid Alarm's id
     * @throws SQLException
     */
    public void activateAlarm(int alarmid)throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE alarm SET isactive = ? WHERE id = ?");
        query.setInt(1, 1);
        query.setInt(1, alarmid);
        query.executeUpdate();
    }

    /**
     * Flags an alarm as inactive.
     * @param alarmid Alarm's alarm-
     * @throws SQLException
     */
    public void inactivateAlarm(int alarmid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE alarm SET isactive = ? WHERE id = ?");
        query.setInt(1, 0);
        query.setInt(2, alarmid);
        query.executeUpdate();
    }

    /**
     * Gets all active and inactive alarms in database
     * @return Arraylist of all alarms.
     * @throws SQLException
     */
    public ArrayList<Alarm> getAllAlarms() throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM alarm");
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        ArrayList<Alarm> results = new ArrayList<Alarm>();
        results.add(new Alarm(rs.getInt("id"), rs.getInt("alarmflags"), rs.getInt("sheepid")));

        while(rs.next()){
            results.add(new Alarm(rs.getInt("id"), rs.getInt("alarmflags"), rs.getInt("sheepid")));
        }
        return results;
    }

    /**
     * Gets all alarms associated with farmer.
     * @param farmerID Farmer's id
     * @return Arraylist with alarms.
     * @throws SQLException
     */
    public ArrayList<Alarm> getAlarmsToFarmer(int farmerID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM alarm WHERE ? = (SELECT farmerid from sheep WHERE id = alarm.sheepid) and isactive = 1");
        query.setInt(1, farmerID);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        ArrayList<Alarm> results = new ArrayList<Alarm>();
        results.add(new Alarm(rs.getInt("id"), rs.getInt("alarmflags"), rs.getInt("sheepid")));

        while(rs.next()){
            results.add(new Alarm(rs.getInt("id"), rs.getInt("alarmflags"), rs.getInt("sheepid")));
        }
        return results;
    }

    /**
     * Flags an alarm as shown. An alarm is shown if it has been loaded into the GUI of the associated farmer.
     * @param alarmid Alarm's id
     * @throws SQLException
     */
    public void setAlarmAsShown(int alarmid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE alarm SET isshown = ? WHERE id = ?");
        query.setInt(1,1);
        query.setInt(2, alarmid);
        query.executeUpdate();
    }

    /**
     * Get all non-shown alarms for farmer
     * @param farmerID Farmer's id
     * @return Arraylist of all alarms associated with farmer that hasn't been shown yet
     * @throws SQLException
     */
    public ArrayList<Alarm> getNonShownAlarms(int farmerID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM alarm WHERE ? = (SELECT farmerid from sheep WHERE id = alarm.sheepid) and isshown = ?");
        query.setInt(1, farmerID);
        query.setInt(2, 0);

        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        ArrayList<Alarm> results = new ArrayList<Alarm>();
        results.add(new Alarm(rs.getInt("id"), rs.getInt("alarmflags"), rs.getInt("sheepid")));

        while(rs.next()){
            results.add(new Alarm(rs.getInt("id"), rs.getInt("alarmflags"), rs.getInt("sheepid")));
        }
        return results;
    }

    /**
     * Attempts to find sheep by name and farmerid.
     * @param name Sheep's name
     * @param farmerid Sheep's farmer's id
     * @return A sheep object with the found sheep's details. Null if not such sheep is found.
     * @throws SQLException
     */
    public Sheep getSheepByName(String name, int farmerid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheep WHERE name = ? AND farmerid = ?");
        query.setString(1, name);
        query.setInt(2, farmerid);

        ResultSet rs = query.executeQuery();

        if(!rs.next())
            throw new SQLException("Ingen sau med det navnet funnet");

        return new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex"));
    }

    /**
     * Gets a sheep by its id.
     * @param sheepID Sheep's id
     * @return A sheep object.
     * @throws SQLException
     */
    public Sheep getSheep(int sheepID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheep WHERE id = ?");
        query.setInt(1, sheepID);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            throw new SQLException("Ingen sau med ID " + sheepID);
        return new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex"));
    }

    /**
     * Returns ALL sheeps in database. This might be very slow.
     * @return Arraylist with sheeps.
     * @throws SQLException
     */
    public ArrayList<Sheep> getAllSheeps() throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheep");
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        ArrayList<Sheep> results = new ArrayList<Sheep>();
        results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));

        while(rs.next()){
            results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));
        }
        return results;
    }

    /**
     * Gets all alive sheeps in database.
     * @return Arraylist with sheeps.
     * @throws SQLException
     */
    public ArrayList<Sheep> getAllAliveSheeps() throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheep WHERE alive = 1");
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        ArrayList<Sheep> results = new ArrayList<Sheep>();
        results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));

        while(rs.next()){
            results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));
        }
        return results;
    }


    /**
     * Gets all sheeps associated with farmer.
     * @param farmerID Farmer's id
     * @return Arraylist of sheeps.
     */
    public ArrayList<Sheep> getSheeps(int farmerID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheep WHERE farmerid = ? and alive = 1");
        query.setInt(1,farmerID);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        ArrayList<Sheep> results = new ArrayList<Sheep>();
        results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));

        while(rs.next()){
            results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));
        }
        return results;
    }

    /**
     * Get all sheep that are alive, and are associated with farmer.
     * @param farmerID Farmer's id.
     * @return Arraylist of sheeps.
     * @throws SQLException
     */
    public ArrayList<Sheep> getAliveSheeps(int farmerID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheep WHERE alive = ? AND farmerid = ?");
        query.setInt(1, 1);
        query.setInt(2, farmerID);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        ArrayList<Sheep> results = new ArrayList<Sheep>();
        results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));

        while(rs.next()){
            results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));
        }
        return results;
    }

    /**
     * Returns all dead sheep of farmer.
     * @param farmerID Farmer's id.
     * @return Returns an arraylist of sheep, or null of no sheep is found.
     * @throws SQLException
     */
    public ArrayList<Sheep> getDeadSheeps(int farmerID) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheep WHERE alive = ? AND farmerid = ?");
        query.setInt(1, 0);
        query.setInt(2, farmerID);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        ArrayList<Sheep> results = new ArrayList<Sheep>();
        results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));

        while(rs.next()){
            results.add(new Sheep(rs.getInt("id"), rs.getLong("birthdate"), rs.getInt("healthflags"), rs.getInt("mileage"), rs.getInt("farmerid"), rs.getDouble("pos_x"), rs.getDouble("pos_y"), rs.getString("name"), rs.getString("sex")));
        }
        return results;
    }

    /**
     * Gets all history of sheep
     * @param sheepid Sheep's id.
     * @return SheepHistory object.
     */
    public SheepHistory getSheepHistory(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheephistory WHERE sheepid = ?");
        query.setInt(1,sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        TreeMap<Long, Vec2> posHistory = new TreeMap<Long, Vec2>();
        posHistory.put(rs.getLong("timestamp"), new Vec2(rs.getDouble("pos_x"), rs.getDouble("pos_y")));

        while(rs.next()){
            posHistory.put(rs.getLong("timestamp"), new Vec2(rs.getDouble("pos_x"), rs.getDouble("pos_y")));
        }
        return new SheepHistory(posHistory, sheepid);
    }

    /**
     * Get all medical history of sheep
     * @param sheepid Sheep's id.
     * @return SheepMedicalHistory object.
     * @throws SQLException
     */
    public SheepMedicalHistory getSheepMedicalHistory(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT * FROM sheepmedicalhistory WHERE sheepid = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        TreeMap<Long, Integer> medHistory= new TreeMap<Long, Integer>();
        medHistory.put(rs.getLong("timestamp"), rs.getInt("healthflag"));

        while(rs.next()){
            medHistory.put(rs.getLong("timestamp"), rs.getInt("healthflag"));
        }
        return new SheepMedicalHistory(medHistory, sheepid);
    }

    /**
     * Gets sheep's name.
     * @param sheepid Sheep's id.
     * @return String with sheepname.
     * @throws SQLException
     */
    public String getSheepName(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT name FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;

        return rs.getString("name");
    }

    /**
     * Gets a sheeps current position
     * @param sheepid Sheep's id.
     * @return Vec2 with sheep's current position.
     * @throws SQLException
     */
    public Vec2 getSheepPosition(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT pos_x, pos_y FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return null;
        return new Vec2(rs.getDouble("pos_x"), rs.getDouble("pos_y"));
    }

    /**
     * Gets the farmer associated with this sheep.
     * @param sheepid sheep's id.
     * @return
     * @throws SQLException
     */
    public int getSheepOwner(int sheepid) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT farmerid FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            return -1;
        return rs.getInt("farmerid");
    }

    /**
     * Sets a sheeps current position, stores the previous position in sheephistory.
     * @param sheepid Sheep's id.
     * @param posX Sheep's new latitude.
     * @param posY Sheep's new longitude.
     * @throws SQLException
     */
    public void setSheepPosition(int sheepid, double posX, double posY) throws SQLException{
        //Get current position
        PreparedStatement query = this.db.prepareStatement("SELECT pos_x, pos_y FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            throw new SQLException("Unable to find sheep");

        float tempX, tempY;
        tempX = rs.getFloat("pos_x");
        tempY = rs.getFloat("pos_y");

        //Store current position in history
        query = this.db.prepareStatement("INSERT INTO sheephistory(sheepid, pos_x, pos_y, timestamp) VALUES (?,?,?,?)");

        long timeStamp = (new java.util.Date()).getTime();
        query.setInt(1, sheepid);
        query.setFloat(2, tempX);
        query.setFloat(3, tempY);
        query.setLong(4, timeStamp);
        query.executeUpdate();

        double distance = GeneralUtil.getDistance(tempX, tempY, posX, posY);

        //Set new position
        query = this.db.prepareStatement("UPDATE sheep SET mileage = mileage + ?, pos_x = ?, pos_y = ? WHERE id = ?");
        query.setDouble(1, distance);
        query.setDouble(2, posX);
        query.setDouble(3, posY);
        query.setInt(4, sheepid);
        query.executeUpdate();
        updateState();
    }

    /**
     * Overwrites the flag to passed in variable. Use with caution as already set flags will be removed.
     * The boolean parameter is in place to avoid storing multiple history versions as the method is called internally from "addSheepHealthFlag".
     * @param sheepid Sheep's id.
     * @param flag Sheep's new healthflag.
     * @param storeHistory A boolean stating whether or not to store.
     * @throws SQLException
     */
    public void setSheepHealthFlag(int sheepid, int flag, boolean storeHistory) throws SQLException{
        if(storeHistory){
            addToSheepHealthHistory(sheepid, flag);
        }

        PreparedStatement query = this.db.prepareStatement("UPDATE sheep SET healthflags = ? WHERE id = ?");
        query.setInt(1, flag);
        query.setInt(2, sheepid);
        query.executeUpdate();
        updateState();
    }


    /**
     * Adds a health flag to the sheep.
     * @param sheepid Sheep's id.
     * @param flag Healthflag that is to be added to the total healthflag.
     * @throws SQLException
     */
    public void addSheepHealthFlag(int sheepid, int flag) throws SQLException{

        addToSheepHealthHistory(sheepid, flag);

        PreparedStatement query = this.db.prepareStatement("SELECT healthflags FROM sheep WHERE id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            throw new SQLException("Couldn't find sheep");

        int mFlags = rs.getInt("healthflags");
        mFlags |= flag;

        this.setSheepHealthFlag(sheepid, mFlags, false);
        updateState();
    }

    /**
     * Removes parts of the sheep's healthflag.
     * @param sheepid Sheep's id.
     * @param healthflag Flag that represents the bits that are to be removed from the healthflag.
     * @throws SQLException
     */
    public void removeSheepHealthFlag(int sheepid, int healthflag) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT healthflags FROM sheep where id = ?");
        query.setInt(1, sheepid);
        ResultSet rs = query.executeQuery();

        if(!rs.next())
            throw new SQLException("Couldnt find sheep");

        int mFlags = rs.getInt("healthflags");

        mFlags ^= healthflag;

        this.setSheepHealthFlag(sheepid, mFlags, true);
    }

    /**
     * Adds healthflag to sheep's healthhistory.
     * @param sheepid Sheep's id.
     * @param healthflag Healthflag to be stored.
     * @throws SQLException
     */
    public void addToSheepHealthHistory(int sheepid, int healthflag) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("INSERT INTO sheepmedicalhistory(healthflag, timestamp, sheepid) VALUES(?,?,?)");
        query.setInt(1, healthflag);
        query.setLong(2, new Date().getTime());
        query.setInt(3, sheepid);
        query.executeUpdate();
    }

    /**
     * Sets sheeps pulse
     * @param sheepid Sheep's id
     * @param pulse Sheep's pulse.
     * @throws SQLException
     */
    public void setSheepPulse(int sheepid, int pulse) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE sheep SET pulse = ? WHERE id = ?");
        query.setInt(1, pulse);
        query.setInt(2, sheepid);
        query.executeUpdate();
    }


    /**
     * Set name of sheep
     * @param sheepid Sheep's id.
     * @param name Sheep's name.
     * @throws SQLException
     */
    public void setSheepName(int sheepid, String name) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE sheep SET name = ? WHERE id = ?");
        query.setString(1, name);
        query.setInt(2, sheepid);
        query.executeUpdate();
        updateState();
    }

    /**
     * Sets sex of the sheep
     * @param sheepid Sheep's id.
     * @param sex Sheep's sex.
     * @throws SQLException
     */
    public void setSheepSex(int sheepid, String sex) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE sheep SET sex = ? WHERE id = ?");
        query.setString(1, sex);
        query.setInt(2, sheepid);
        query.executeUpdate();
        updateState();
    }

    /**
     * Sets birthday of the sheep
     * @param sheepid Sheep's id.
     * @param timestamp Timestamp indicating sheep's birthdate.
     * @throws SQLException
     */
    public void setSheepBirthdate(int sheepid, long timestamp) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("UPDATE sheep SET birthdate = ? WHERE id = ?");
        query.setLong(1, timestamp);
        query.setInt(2, sheepid);
        query.executeUpdate();
    }

    /**
     * Gets the state of the database.
     * The state is an integer deciding how many essential operations have been done on the database since the beginning of time.
     * This is used to decide when to repoll data from the database.
     * @return Returns the state as an integer.
     * @throws SQLException
     */
    public int getState() throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SELECT value FROM state WHERE id = 1");
        ResultSet rs = query.executeQuery();

        rs.next();

        return rs.getInt(1);
    }

    /**
     * Updates the state by incremenatation.
     * @throws SQLException
     */
    private void updateState() throws SQLException{
        int state = getState();

        state+=1;

        PreparedStatement query = this.db.prepareStatement("UPDATE state SET value = ?");
        query.setInt(1,state);
        query.executeUpdate();
    }

    /**
     * Find the next autoincrement of column 'column' in table 'table'
     * @param table Table to get autoincrement value of.
     * @return int containing next autoincrement value.
     * @throws SQLException
     */
    private int getNextAutoIncrement(String table) throws SQLException{
        PreparedStatement query = this.db.prepareStatement("SHOW TABLE STATUS LIKE ?");
        query.setString(1, table);
        ResultSet rs = query.executeQuery();
        rs.next();
        return rs.getInt("Auto_increment");
    }

    /**
     * Takes a rawstring password and encrypts it with SHA1.
     * @param string Password to be encrypted.
     * @return String containing SHA1-digest.
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
