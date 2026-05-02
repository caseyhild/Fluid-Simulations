public class Vector3D
{
    public double x;
    public double y;
    public double z;
    
    public Vector3D(double a)
    {
        this.x = a;
        this.y = a;
        this.z = a;
    }

    public double mag()
    {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public void setAngleXY(double angle)
    {
        double mag = mag();
        x = mag * Math.cos(Math.toRadians(angle));
        y = mag * Math.sin(Math.toRadians(angle));
    }
    
    public void setAngleXZ(double angle)
    {
        double mag = mag();
        x = mag * Math.cos(Math.toRadians(angle));
        z = mag * Math.sin(Math.toRadians(angle));
    }
    
    public void setAngleYZ(double angle)
    {
        double mag = mag();
        y = mag * Math.cos(Math.toRadians(angle));
        z = mag * Math.sin(Math.toRadians(angle));
    }
    public String toString()
    {
        return x + " " + y + " " + z;
    }
}