package net;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/2/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Packet implements Serializable {
    private String header;
    private Object[] args;

    public Packet(String header, Object... args)
    {
        this.header = header;
        this.args = args;
    }

    public Object[] getData()
    {
        return args;
    }

    public String getHeader()
    {
        return header;
    }
}
