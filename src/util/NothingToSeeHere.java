package util;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/24/13
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class NothingToSeeHere {

    public static String hashOne = "98gergetr345sgtnegagaeteeeefa9234nkgkne9g9egrbn23bn";
    public static String hashTwo = "bnt34567ajkn245bvhagdfgh9bnjg3b234gbn23023jaejkrger";

    public static int[] clamps = {48, 57, 65, 90, 97, 122};

    public static String t(String s) {
        return null;
    }

    public static String f(String s){
        return null;

    }


    public static String a(String s){
        return null;

    }

    public static String b(String s){
        return null;

    }

    public static String c(String s){
        byte[] bytes = s.getBytes();
        for(int i = 0; i < bytes.length; ++i){
            bytes[i] += hashOne.charAt(bytes[i]*i);
        }
        return null;
    }

    public static int ghoi(int magic){
        int i = magic;
        int max = getFirstBit(magic)+1;

        i = (i < 0 || i > hashOne.length()) ? magic/(1 >> max) : i;
        return i;
    }

    public static String d(String s){
        return null;
    }


    public static int getFirstBit(int number){
        int i = 0x10000000;
        int count = 0;
        while((number & i) != 1){
            count++;
            i = i << 1;
        }
        return count;
    }

    public static void main(String[] args){

    }
}
