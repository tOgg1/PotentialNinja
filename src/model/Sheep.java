package model;

public class Sheep
{
    int id;
    int ownerid;
    int age;
    int mileage;
    int healthflags;
    String name;

    public Sheep(int id, int age, int healthflags, int mileage, int ownerid, String name) {
        this.age = age;
        this.id = id;
        this.mileage = mileage;
        this.ownerid = ownerid;
        this.healthflags = healthflags;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealthflags() {
        return healthflags;
    }

    public void setHealthflags(int healthflags) {
        this.healthflags = healthflags;
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
