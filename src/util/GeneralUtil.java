package util;

import java.awt.*;

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
