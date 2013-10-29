package util;

import java.util.ArrayList;

public class Vec2 {
    public float x, y;

    public Vec2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void add(Vec2 vec){
        this.x += vec.x;
        this.y += vec.y;
    }

    public ArrayList<Float> toList(){
        ArrayList<Float> list = new ArrayList<Float>();

        list.add(x);
        list.add(y);

        return list;
    }
}
