import db.DatabaseHandler;

import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/24/13
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class AlertDaemon extends Thread {

    private Timer timer;
    private DatabaseHandler handler;
    private SMTPHandler mailHandler;

    private int databaseState;

    public AlertDaemon(){
        this.handler = new DatabaseHandler();
        this.mailHandler = new SMTPHandler();
    }

    @Override
    public void run() {

    }

    private void scheduleAndPoll(){

    }
}
