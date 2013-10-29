package util;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/29/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralUtil {

    public static String charToString(char[] array){
        StringBuilder sb = new StringBuilder();
        for(char c : array){
            sb.append(c);
        }

        return sb.toString();
    }

}
