package util;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/9/13
 * Time: 6:14 PM
 * To change this template use File | Settings | File Templates.
 */
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
}
