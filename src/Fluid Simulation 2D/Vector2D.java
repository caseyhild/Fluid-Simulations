public class Vector2D
{
    public double x;
    public double y;

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double mag()
    {
        return Math.sqrt(x * x + y * y);
    }

    public void setAngleXY(double angle)
    {
        double mag = mag();
        x = mag * Math.cos(Math.toRadians(angle));
        y = mag * Math.sin(Math.toRadians(angle));
    }

    public String toString()
    {
        return x + " " + y;
    }
}