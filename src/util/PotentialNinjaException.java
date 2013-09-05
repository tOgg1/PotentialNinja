package util;

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
