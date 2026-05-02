public class FluidCube
{
    public final int size = 42;
    private final double dt;
    private final double diffusion;

    private final double[][] pressure;
    public double[][] density;

    public Vector2D[][] velocity;

    public FluidCube(double diffusion, double dt)
    {
        this.dt = dt;
        this.diffusion = diffusion;

        pressure = new double[size][size];
        density = new double[size][size];
        velocity = new Vector2D[size][size];
        for(int y = 0; y < size; y++)
        {
            for(int x = 0; x < size; x++)
                velocity[y][x] = new Vector2D();
        }
    }

    public void step()
    {
        velocity = new Vector2D[size][size];
        for(int y = 0; y < size; y++)
        {
            for(int x = 0; x < size; x++)
            {
                double speed = 0.002;
                velocity[y][x] = new Vector2D(-(y - size/2.0) * speed, (x - size/2.0) * speed);
            }
        }
        diffusePD(pressure, density, diffusion, dt);
        advectPD(density, velocity, dt);
    }

    public void addDensity(int x, int y, double amount)
    {
        density[y][x] += amount;
    }

    public void addVelocity(int x, int y, double amountX, double amountY)
    {
        velocity[y][x].add(new Vector2D(amountX, amountY));
    }

    public void lin_solvePD(double[][] pressure, double[][] density, double a, double c)
    {
        int iterations = 1;
        for(int k = 0; k < iterations; k++)
        {
            for(int m = 1; m < size - 1; m++)
            {
                for(int j = 1; j < size - 1; j++)
                {
                    for(int i = 1; i < size - 1; i++)
                    {
                        double pressureSum = pressure[j][i + 1] + pressure[j][i - 1] + pressure[j + 1][i] + pressure[j - 1][i];
                        pressure[j][i] = (density[j][i] + a * pressureSum) * c;
                    }
                }
            }
        }
    }

    public void diffusePD(double[][] pressure, double[][] density, double diffusion, double dt)
    {
        double a = dt * diffusion * (size - 2) * (size - 2);
        lin_solvePD(pressure, density, a, 1/(1 + 4 * a));
    }

    public void advectPD(double[][] density, Vector2D[][] velocity, double dt)
    {
        double i0, i1, j0, j1;

        double dtx = dt * (size - 2);
        double dty = dt * (size - 2);

        double s0, s1, t0, t1;
        double tmp1, tmp2, x, y;

        double sizefloat = size;
        double ifloat, jfloat;
        int i, j;

        for(j = 1, jfloat = 1; j < size - 1; j++, jfloat++)
        {
            for(i = 1, ifloat = 1; i < size - 1; i++, ifloat++)
            {
                tmp1 = dtx * velocity[j][i].x;
                tmp2 = dty * velocity[j][i].y;
                x = ifloat - tmp1;
                y = jfloat - tmp2;

                if(x < 0.5)
                    x = 0.5;
                if(x > sizefloat - 1.5)
                    x = sizefloat - 1.5;
                i0 = Math.floor(x);
                i1 = i0 + 1;
                if(y < 0.5)
                    y = 0.5;
                if(y > sizefloat - 1.5)
                    y = sizefloat - 1.5;
                j0 = Math.floor(y);
                j1 = j0 + 1;

                s1 = x - i0;
                s0 = 1 - s1;
                t1 = y - j0;
                t0 = 1 - t1;

                int i0i = (int) i0;
                int i1i = (int) i1;
                int j0i = (int) j0;
                int j1i = (int) j1;

                density[j][i] =
                s0 * (t0 * density[j0i][i0i] + t1 * density[j1i][i0i]) +
                s1 * (t0 * density[j0i][i1i] + t1 * density[j1i][i1i]);
            }
        }
        //set_bndPD(density);
    }
}