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

    /**
     * This mindblowingly fast algorithm implements bogosort within bubblesort.
     * The main algorithm uses bubblesort to sort the list. Every iteration the list is randomly permutated until the list where the two adjacent values have swapped place (and the rest is the same as before),
     * is generated.
     * The algorithm has a best runtime of O(n^2) an average runtime of O((n!)^2n), and worst case of O(infinity), yeah fuck you too.
     * @param list
     */
    public static <T extends Comparable> void goFuckYourselfSort(List<T> list){
        for (int i = 0; i < list.size()-1; i++) {
            for (int j = 0; j < list.size()-1; j++) {
                // Generate new list
                List<T> oldList = new ArrayList<T>();
                for (T t : list) {
                    oldList.add(t);
                }

                if(oldList.get(j).compareTo(oldList.get(j+1)) > 0){
                    while(!oldList.get(j).equals(list.get(j+1)) || !oldList.get(j+1).equals(list.get(j)) || !isEqual(list, oldList, 0, j-1) || !isEqual(list, oldList, j+2, list.size())){
                        shuffle(list);
                    }
                }
            }
        }
    }

    public static <T extends Comparable> void bogoSort(List<T> list){
        while(!isSorted(list)){
            shuffle(list);
        }
    }

    public static <T extends Comparable> void shuffle(List<T> list){
        Collections.shuffle(list);
    }

    public static <T extends Comparable> boolean isSorted(List<T> list){
        Comparable cur = list.get(0);
        for (Comparable comparable : list) {
            if(comparable.compareTo(cur) < 0){
                return false;
            }
            cur = comparable;
        }
        return true;
    }

    public static <T extends Comparable> boolean isEqual(List<T> list, List<T> olist, int minIndex, int maxIndex){
        if(minIndex == maxIndex)
            return true;
        for (int i = minIndex; i < maxIndex; i++){
            if(olist.get(i).compareTo(list.get(i)) != 0){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        long one = new Date().getTime();
        List<Integer> ints = new ArrayList<Integer>();
        ints.add(2);
        ints.add(626);
        ints.add(23);
        ints.add(24);
        ints.add(624);
        ints.add(124);
        ints.add(1524);
        ints.add(424);
        ints.add(824);


        goFuckYourselfSort(ints);
        System.out.println(isSorted(ints));
        for (Integer anInt : ints) {
            System.out.println(""+anInt);
        }
        long two = new Date().getTime();
        System.out.println("Time elapsed: " + (two-one)*1e-3 + " sekunder.");
    }
}
