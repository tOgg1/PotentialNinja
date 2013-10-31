package util;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/29/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralUtil {

    public static Color selectedSheepColor = new Color(0xAA, 0x40, 0x32);
    public static Color farmColor = new Color(0x80, 0x80, 0x80);

    public static String charToString(char[] array){
        StringBuilder sb = new StringBuilder();
        for(char c : array){
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Helper function to get the key from a TreeMap by value
     * @param map
     * @param value
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> T getKeyByValue(TreeMap<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isEmailValid(String email){
        String[] splitt = email.split("@");
        if(splitt.length != 2)
            return false;

        splitt = email.split("@")[1].split("\\p{P}");
        if(splitt.length != 2){
            return false;
        }
        return true;
    }

}
