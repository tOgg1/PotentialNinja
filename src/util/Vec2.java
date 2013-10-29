package util;

public class Vec2 {
    public double x, y;

    public Vec2(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void add(Vec2 vec){
        this.x += vec.x;
        this.y += vec.y;
    }
}
