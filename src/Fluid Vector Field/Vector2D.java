public class Vector2D
{
    public double x;
    public double y;

    public Vector2D()
    {
        x = 0; 
        y = 0;
    }

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    public void add(Vector2D v)
    {
        x += v.x;
        y += v.y;
    }

    public String toString()
    {
        return x + " " + y;
    }
}