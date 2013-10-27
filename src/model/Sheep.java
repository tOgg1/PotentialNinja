package model;

public class Sheep
{

    public final static String SEX_MALE = "m";
    public final static String SEX_FEMALE = "f";

    int id;
    int farmerid;
    //int age;
    int mileage;
    int healthflags;
    int birthdate;
    int pulse;
    String sex;
    String name;

    SheepHistory posHistory;
    SheepMedicalHistory medicalHistory;

    public int getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(int birthdate) {
        this.birthdate = birthdate;
    }

    public SheepMedicalHistory getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(SheepMedicalHistory medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public SheepHistory getPosHistory() {
        return posHistory;
    }

    public void setPosHistory(SheepHistory posHistory) {
        this.posHistory = posHistory;
    }

    public Sheep(int id, int birthdate, int healthflags, int mileage, int farmerid, String name, String sex) {
        //this.age = age;
        this.id = id;
        this.mileage = mileage;
        this.farmerid = farmerid;
        this.healthflags = healthflags;
        this.birthdate = birthdate;
        this.name = name;

        if(sex == SEX_MALE || sex == SEX_FEMALE){
            this.sex = sex;
        }else{
            this.sex = "f";
        }
        this.pulse = -1;
    }

    public Sheep(int id, int birthdate, int healthflags, int mileage, int farmerid, int pulse, String name, String sex) {
        //this.age = age;
        this.id = id;
        this.mileage = mileage;
        this.farmerid = farmerid;
        this.healthflags = healthflags;
        this.birthdate = birthdate;
        this.name = name;
        this.pulse = pulse;

        if(sex == SEX_MALE || sex == SEX_FEMALE){
            this.sex = sex;
        }else{
            this.sex = "f";
        }
    }

    public boolean hasFlag(int flag){
        return (healthflags & flag) > 0;
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

    /*public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }*/

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

    public int getFarmerid() {
        return farmerid;
    }

    public void setFarmerid(int farmerid) {
        this.farmerid = farmerid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
