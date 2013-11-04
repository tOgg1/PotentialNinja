package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 11/4/13
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bogo{

    public static void bogoSort(int[] values){
        List<Integer> list = new ArrayList<Integer>();

        for(int i = 0; i < values.length; i++){
            list.add(values[i]);
        }

        while(!isSorted(list)){
            shuffle(list);
        }

        int i = 0;
        for (Integer integer : list) {
            values[i++] = integer;
        }
    }

    public static void shuffle(List<Integer> list){
        Collections.shuffle(list);

    }

    public static boolean isSorted(List<Integer> list){
        int cur = -Integer.MAX_VALUE;
        for (Integer integer : list) {
            if(integer < cur)
                return false;
            cur = integer;
        }
        return true;
    }

    public static void main(String[] args){
        int[] values = {4,3,5,21,5,6,9,5,12,23,7,39};
        long one = new Date().getTime();
        bogoSort(values);
        long two = new Date().getTime();
        System.out.println("Time elapsed: " + (two-one)*1e-3 + " sekunder.");
    }
}
