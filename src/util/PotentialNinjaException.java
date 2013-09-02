package util;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/2/13
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class PotentialNinjaException extends Exception {


    public PotentialNinjaException(){
        super();
        Log.e("Exception", "Some PotentialNinjaException");
    }

    public PotentialNinjaException(String s){
        super(s);
        Log.e("Exception", s);
    }

    public PotentialNinjaException(String s, Exception inner){
        super(s,inner);
    }
}
