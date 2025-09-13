public class Vector3D
{
    public double x;
    public double y;
    public double z;

    public Vector3D()
    {
        x = 0; 
        y = 0;
        z = 0;
    }
    
    public Vector3D(double a)
    {
        this.x = a;
        this.y = a;
        this.z = a;
    }

    public Vector3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3D(Vector3D v)
    {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }
    
    public double getZ()
    {
        return z;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }
    
    public void setZ(double z)
    {
        this.z = z;
    }

    public void add(Vector3D v)
    {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public static Vector3D add(Vector3D v1, Vector3D v2)
    {
        Vector3D v = new Vector3D();
        v.setX(v1.x + v2.x);
        v.setY(v1.y + v2.y);
        v.setZ(v1.z + v2.z);
        return v;
    }

    public void sub(Vector3D v)
    {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    public static Vector3D sub(Vector3D v1, Vector3D v2)
    {
        Vector3D v = new Vector3D();
        v.setX(v1.x - v2.x);
        v.setY(v1.y - v2.y);
        v.setZ(v1.z - v2.z);
        return v;
    }

    public void mult(double n)
    {
        x *= n;
        y *= n;
        z *= n;
    }

    public static Vector3D mult(Vector3D v1, double n)
    {
        Vector3D v = new Vector3D();
        v.setX(v1.x * n);
        v.setY(v1.y * n);
        v.setZ(v1.z * n);
        return v;
    }
    
    public void mult(Vector3D v)
    {
        x *= v.x;
        y *= v.y;
        z *= v.z;
    }

    public static Vector3D mult(Vector3D v1, Vector3D v2)
    {
        Vector3D v = new Vector3D();
        v.setX(v1.x * v2.x);
        v.setY(v1.y * v2.y);
        v.setZ(v1.z * v2.z);
        return v;
    }

    public void div(double n)
    {
        x /= n;
        y /= n;
        z /= n;
    }

    public static Vector3D div(Vector3D v1, double n)
    {
        Vector3D v = new Vector3D();
        v.setX(v1.x / n);
        v.setY(v1.y / n);
        v.setZ(v1.z / n);
        return v;
    }
    
    public void div(Vector3D v)
    {
        x /= v.x;
        y /= v.y;
        z /= v.z;
    }

    public static Vector3D div(Vector3D v1, Vector3D v2)
    {
        Vector3D v = new Vector3D();
        v.setX(v1.x / v2.x);
        v.setY(v1.y / v2.y);
        v.setZ(v1.z / v2.z);
        return v;
    }

    public double mag()
    {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize()
    {
        if(mag() > 0)
            div(mag());
    }

    public static Vector3D normalize(Vector3D v1)
    {
        Vector3D v = new Vector3D();
        if(v1.mag() > 0)
        {
            v.setX(v1.x/v1.mag());
            v.setY(v1.y/v1.mag());
            v.setZ(v1.z/v1.mag());
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

    public static Vector3D limit(Vector3D v1, double max)
    {
        Vector3D v = new Vector3D();
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
        z = Math.abs(z);
    }
    
    public static Vector3D abs(Vector3D v)
    {
        return new Vector3D(Math.abs(v.x), Math.abs(v.y), Math.abs(v.z));
    }
    
    public void min(Vector3D v)
    {
        x = Math.min(x, v.x);
        y = Math.min(y, v.y);
        z = Math.min(z, v.z);
    }
    
    public void setMin(Vector3D v)
    {
        max(v);
    }
    
    public static Vector3D min(Vector3D v1, Vector3D v2)
    {
        return new Vector3D(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z));
    }
    
    public void max(Vector3D v)
    {
        x = Math.max(x, v.x);
        y = Math.max(y, v.y);
        z = Math.max(z, v.z);
    }
    
    public void setMax(Vector3D v)
    {
        min(v);
    }
    
    public static Vector3D max(Vector3D v1, Vector3D v2)
    {
        return new Vector3D(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
    }
    
    public void mix(Vector3D v, double a)
    {
        x = x * (1 - a) + v.x * a;
        y = y * (1 - a) + v.y * a;
        z = z * (1 - a) + v.z * a;
    }
    
    public static Vector3D mix(Vector3D v1, Vector3D v2, double a)
    {
        return new Vector3D(v1.x * (1 - a) + v2.x * a, v1.y * (1 - a) + v2.y * a, v1.z * (1 - a) + v2.z * a);
    }

    public double dist(Vector3D v)
    {
        return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z));
    }
    
    public static double dist(Vector3D v1, Vector3D v2)
    {
        return Math.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y) + (v1.z - v2.z) * (v1.z - v2.z));
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
    
    public double getAngleXZ()
    {
        if(x < -0.000001)
            return 180 + Math.atan(z/x) * 180/Math.PI;
        else if(x > 0.000001 && z >= -0.000001)
            return Math.atan(z/x) * 180/Math.PI;
        else if(x > 0.000001 && z < -0.000001)
            return 360 + Math.atan(z/x) * 180/Math.PI;
        else if(Math.abs(x) <= 0.000001 && Math.abs(z) <= 0.000001)
            return 0;
        else if(Math.abs(x) <= 0.000001 && z >= -0.000001)
            return 90;
        else
            return 270;
    }
    
    public double getAngleYZ()
    {
        if(y < -0.000001)
            return 180 + Math.atan(z/y) * 180/Math.PI;
        else if(y > 0.000001 && z >= -0.000001)
            return Math.atan(z/y) * 180/Math.PI;
        else if(y > 0.000001 && z < -0.000001)
            return 360 + Math.atan(z/y) * 180/Math.PI;
        else if(Math.abs(y) <= 0.000001 && Math.abs(z) <= 0.000001)
            return 0;
        else if(Math.abs(y) <= 0.000001 && z >= -0.000001)
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
    
    public void rotateZ3D(double theta, Vector3D origin) 
    {
        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));
        double xCopy = x - origin.x;
        double yCopy = y - origin.y;
        x = origin.x + xCopy * cosTheta - yCopy * sinTheta;
        y = origin.y + yCopy * cosTheta + xCopy * sinTheta;
    }

    public void rotateY3D(double theta, Vector3D origin)
    { 
        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));
        double xCopy = x - origin.x;
        double zCopy = z - origin.z;
        x = origin.x + xCopy * cosTheta - zCopy * sinTheta;
        z = origin.z + zCopy * cosTheta + xCopy * sinTheta;
    }

    public void rotateX3D(double theta, Vector3D origin)
    { 
        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));
        double yCopy = y - origin.y;
        double zCopy = z - origin.z;
        y = origin.y + yCopy * cosTheta - zCopy * sinTheta;
        z = origin.z + zCopy * cosTheta + yCopy * sinTheta;
    }
    
    public static double dotProduct(Vector3D v1, Vector3D v2)
    {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
    
    public static Vector3D crossProduct(Vector3D v1, Vector3D v2)
    {
        return new Vector3D(v1.y * v2.z - v1.z * v2.y, v1.z * v1.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }

    public boolean equals(Vector3D v)
    {
        if(x == v.x && y == v.y && z == v.z)
            return true;
        else
            return false;
    }

    public String toString()
    {
        return x + " " + y + " " + z;
    }
}