package db;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseHandler class, deals with getting and setting data
 *
 */
public class DatabaseHandler {

    private Connection db;

    public DatabaseHandler(){
        try{
            // Establish a ssh-connection to our remote server
            final JSch jsch = new JSch();
            Session session =  jsch.getSession(DatabaseSettings.remote_user, DatabaseSettings.remote_host, 22);
            session.setPassword(DatabaseSettings.remote_pw);

            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
            session.setPortForwardingL(3306, DatabaseSettings.remote_host, 3306);

            //Connect to the database
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            db = DriverManager.getConnection(DatabaseSettings.db_url, DatabaseSettings.db_user, DatabaseSettings.db_pw);

        }  catch(ClassNotFoundException  | InstantiationException | IllegalAccessException | JSchException | SQLException e){
            Log.e("Database", "You suck");
        }
    }


}
