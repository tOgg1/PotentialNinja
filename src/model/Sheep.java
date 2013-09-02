package model;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 9/2/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sheep
{
    int id;
    int ownerid;
    int age;
    int mileage;

    public Sheep(int age, int id, int mileage, int ownerid) {
        this.age = age;
        this.id = id;
        this.mileage = mileage;
        this.ownerid = ownerid;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }
}
