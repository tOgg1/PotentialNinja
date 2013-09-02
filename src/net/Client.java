package net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/2/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client
{
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public Client(Socket socket){
        this.socket = socket;

    }
}
