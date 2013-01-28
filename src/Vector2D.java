/** A primitive 2D Vector */

public class Vector2D {
    public double x, y;

    public Vector2D(Vector2D from) {
        x = from.x;
        y = from.y;
    }

    public Vector2D(double x2, double y2) {
        this.x = x2;
        this.y = y2;
    }

    public Vector2D copy() {
        return new Vector2D(x, y);
    }

    public void scale(double d) {
        x *= d;
        y *= d;
    }

    public void add(Vector2D vec2) {
        x += vec2.x;
        y += vec2.y;
    }

    public void sub(Vector2D vec2) {
        x -= vec2.x;
        y -= vec2.y;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public void print() {
        System.out.println(this);
    }

    public String toString() {
        return "Vector2D[" + x + "," + y + "]";
    }

    public void normalize() {
        scale(1.0 / length());
    }

    public Vector2D normalized() {
        Vector2D v = copy();
        v.normalize();
        return v;
    }
}