public class Vector2D
{
    public double x;
    public double y;

    public Vector2D()
    {
        x = 0; 
        y = 0;
    }
    
    public Vector2D(double a)
    {
        x = a;
        y = a;
    }

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D v)
    {
        x = v.x;
        y = v.y;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public void add(Vector2D v)
    {
        x += v.x;
        y += v.y;
    }

    public static Vector2D add(Vector2D v1, Vector2D v2)
    {
        Vector2D v = new Vector2D();
        v.setX(v1.x + v2.x);
        v.setY(v1.y + v2.y);
        return v;
    }

    public void sub(Vector2D v)
    {
        x -= v.x;
        y -= v.y;
    }

    public static Vector2D sub(Vector2D v1, Vector2D v2)
    {
        Vector2D v = new Vector2D();
        v.setX(v1.x - v2.x);
        v.setY(v1.y - v2.y);
        return v;
    }

    public void mult(double n)
    {
        x *= n;
        y *= n;
    }

    public static Vector2D mult(Vector2D v1, double n)
    {
        Vector2D v = new Vector2D();
        v.setX(v1.x * n);
        v.setY(v1.y * n);
        return v;
    }

    public void div(double n)
    {
        x /= n;
        y /= n;
    }

    public static Vector2D div(Vector2D v1, double n)
    {
        Vector2D v = new Vector2D();
        v.setX(v1.x/n);
        v.setY(v1.y/n);
        return v;
    }

    public double mag()
    {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize()
    {
        if(mag() > 0)
            div(mag());
    }

    public static Vector2D normalize(Vector2D v1)
    {
        Vector2D v = new Vector2D();
        if(v1.mag() > 0)
        {
            v.setX(v1.x/v1.mag());
            v.setY(v1.y/v1.mag());
        }
        return v;
    }

    public void limit(double max)
    {
        if(mag() >= max)
        {
            normalize();
            mult(max);
        }
    }

    public static Vector2D limit(Vector2D v1, double max)
    {
        Vector2D v = new Vector2D();
        if(v1.mag() >= max)
        {
            v = normalize(v1);
            v = mult(v1, max);
        }
        else
            v = v1;
        return v;
    }

    public void abs()
    {
        x = Math.abs(x);
        y = Math.abs(y);
    }

    public static Vector2D abs(Vector2D v)
    {
        return new Vector2D(Math.abs(v.x), Math.abs(v.y));
    }

    public void min(Vector2D v)
    {
        x = Math.min(x, v.x);
        y = Math.min(y, v.y);
    }
    
    public void setMin(Vector2D v)
    {
        max(v);
    }

    public static Vector2D min(Vector2D v1, Vector2D v2)
    {
        return new Vector2D(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y));
    }

    public void max(Vector2D v)
    {
        x = Math.max(x, v.x);
        y = Math.max(y, v.y);
    }
    
    public void setMax(Vector2D v)
    {
        min(v);
    }

    public static Vector2D max(Vector2D v1, Vector2D v2)
    {
        return new Vector2D(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y));
    }

    public void mix(Vector2D v, double a)
    {
        x = x * (1 - a) + v.x * a;
        y = y * (1 - a) + v.y * a;
    }

    public static Vector2D mix(Vector2D v1, Vector2D v2, double a)
    {
        return new Vector2D(v1.x * (1 - a) + v2.x * a, v1.y * (1 - a) + v2.y * a);
    }

    public double dist(Vector2D v)
    {
        return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y));
    }

    public double getAngleXY()
    {
        if(x < -0.000001)
            return 180 + Math.atan(y/x) * 180/Math.PI;
        else if(x > 0.000001 && y >= -0.000001)
            return Math.atan(y/x) * 180/Math.PI;
        else if(x > 0.000001 && y < -0.000001)
            return 360 + Math.atan(y/x) * 180/Math.PI;
        else if(Math.abs(x) <= 0.000001 && Math.abs(y) <= 0.000001)
            return 0;
        else if(Math.abs(x) <= 0.000001 && y >= -0.000001)
            return 90;
        else
            return 270;
    }

    public void setAngleXY(double angle)
    {
        double mag = mag();
        x = mag * Math.cos(Math.toRadians(angle));
        y = mag * Math.sin(Math.toRadians(angle));
    }

    public void rotate(double theta, Vector2D origin) 
    {
        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));
        double xCopy = x - origin.x;
        double yCopy = y - origin.y;
        x = origin.x + xCopy * cosTheta - yCopy * sinTheta;
        y = origin.y + yCopy * cosTheta + xCopy * sinTheta;
    }

    public static double dotProduct(Vector2D v1, Vector2D v2)
    {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public boolean equals(Vector2D v)
    {
        return x == v.x && y == v.y;
    }

    public String toString()
    {
        return x + " " + y;
    }
}