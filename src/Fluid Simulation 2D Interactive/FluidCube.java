public class FluidCube
{
    public final int size = 56;
    private final int iterations = 1;
    private final double dt;
    private final double diffusion;
    private final double viscosity;

    private final double[][] pressure;
    public double[][] density;

    private final Vector2D[][] velocity;
    private final Vector2D[][] prevVelocity;

    public FluidCube(double diffusion, double viscosity, double dt)
    {
        this.dt = dt;
        this.diffusion = diffusion;
        this.viscosity = viscosity;

        pressure = new double[size][size];
        density = new double[size][size];
        velocity = new Vector2D[size][size];
        prevVelocity = new Vector2D[size][size];
        for(int y = 0; y < size; y++)
        {
            for(int x = 0; x < size; x++)
            {
                velocity[y][x] = new Vector2D();
                prevVelocity[y][x] = new Vector2D();
            }
        }
    }

    public void step()
    {
        diffuseVel(prevVelocity, velocity, viscosity, dt);

        project(prevVelocity, velocity);

        advectVel(velocity, prevVelocity, dt);

        project(velocity, prevVelocity);

        diffusePD(pressure, density, diffusion, dt);
        advectPD(density, pressure, velocity, dt);
    }

    public void addDensity(int x, int y, double amount)
    {
        density[y][x] += amount;
    }

    public void addVelocity(int x, int y, double amountX, double amountY)
    {
        velocity[y][x].add(new Vector2D(amountX, amountY));
    }

    public void set_bndVel(Vector2D[][] velocity)
    {
        for(int i = 1; i < size - 1; i++)
        {
            velocity[0][i].y = -velocity[1][i].y;
            velocity[0][i].x = velocity[1][i].x;
            velocity[size - 1][i].y = -velocity[size - 2][i].y;
            velocity[size - 1][i].x = velocity[size - 2][i].x;
        }
        for(int j = 1; j < size - 1; j++)
        {
            velocity[j][0].x = -velocity[j][1].x;
            velocity[j][0].y = velocity[j][1].y;
            velocity[j][size - 1].x = -velocity[j][size - 2].x;
            velocity[j][size - 1].y = velocity[j][size - 2].y;
        }

        velocity[0][0] = Vector2D.mult(Vector2D.add(velocity[0][1], velocity[1][0]), 0.5);
        velocity[size - 1][0] = Vector2D.mult(Vector2D.add(velocity[size - 1][1], velocity[size - 2][0]), 0.5);
        velocity[0][size - 1] = Vector2D.mult(Vector2D.add(velocity[0][size - 2], velocity[1][size - 1]), 0.5);
        velocity[size - 1][size - 1] = Vector2D.mult(Vector2D.add(velocity[size - 1][size - 2], velocity[size - 2][size - 1]), 0.5);
    }

    public void set_bndPD(double[][] pd)
    {
        for(int i = 1; i < size - 1; i++)
        {
            pd[0][i] = pd[1][i];
            pd[size - 1][i] = pd[size - 2][i];
        }
        for(int j = 1; j < size - 1; j++)
        {
            pd[j][0] = pd[j][1];
            pd[j][size - 1] = pd[j][size - 2];
        }

        pd[0][0] = 0.5 * (pd[0][1] + pd[1][0]);
        pd[0][size - 1] = 0.5 * (pd[size - 1][1] + pd[size - 2][0]);
        pd[size - 1][0] = 0.5 * (pd[0][size - 2] + pd[1][size - 1]);
        pd[size - 1][size - 1] = 0.5 * (pd[size - 1][size - 2] + pd[size - 2][size - 1]);
    }

    public void lin_solveVel(Vector2D[][] velocity1, Vector2D[][] velocity2, double a, double c)
    {
        for(int k = 0; k < iterations; k++)
        {
            for(int m = 1; m < size - 1; m++)
            {
                for(int j = 1; j < size - 1; j++)
                {
                    for(int i = 1; i < size - 1; i++)
                    {
                        Vector2D velocitySum = new Vector2D();
                        velocitySum.add(velocity1[j][i + 1]);
                        velocitySum.add(velocity1[j][i - 1]);
                        velocitySum.add(velocity1[j + 1][i]);
                        velocitySum.add(velocity1[j - 1][i]);
                        velocity1[j][i] = Vector2D.mult(Vector2D.add(velocity2[j][i], Vector2D.mult(velocitySum, a)), c);
                    }
                }
            }
            set_bndVel(velocity1);
        }
    }
    
    public void lin_solve1Vel(Vector2D[][] velocity, double a, double c)
    {
        for(int k = 0; k < iterations; k++)
        {
            for(int m = 1; m < size - 1; m++)
            {
                for(int j = 1; j < size - 1; j++)
                {
                    for(int i = 1; i < size - 1; i++)
                    {
                        double xSum = velocity[j][i + 1].x + velocity[j][i - 1].x + velocity[j + 1][i].x + velocity[j - 1][i].x;
                        velocity[j][i].x = (velocity[j][i].y + a * xSum) * c;
                    }
                }
            }
            double[][] x = new double[size][size];
            for(int j = 0; j < size; j++)
            {
                for(int i = 0; i < size; i++)
                {
                    x[j][i] = velocity[j][i].x;
                }
            }
            set_bndPD(x);
        }
    }

    public void lin_solvePD(double[][] pressure, double[][] density, double a, double c)
    {
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
            set_bndPD(pressure);
        }
    }

    public void diffuseVel(Vector2D[][] prevVelocity, Vector2D[][] velocity, double viscosity, double dt)
    {
        double a = dt * viscosity * (size - 2) * (size - 2);
        lin_solveVel(prevVelocity, velocity, a, 1/(1 + 4 * a));
    }

    public void diffusePD(double[][] pressure, double[][] density, double diffusion, double dt)
    {
        double a = dt * diffusion * (size - 2) * (size - 2);
        lin_solvePD(pressure, density, a, 1/(1 + 4 * a));
    }

    public void project(Vector2D[][] velocity1, Vector2D[][] velocity2)
    {
        for(int k = 1; k < size - 1; k++)
        {
            for(int j = 1; j < size - 1; j++)
            {
                for(int i = 1; i < size - 1; i++)
                {
                    velocity2[j][i].y = -0.5 * (
                        velocity1[j][i + 1].x
                        -velocity1[j][i - 1].x
                        +velocity1[j + 1][i].y
                        -velocity1[j - 1][i].y
                    ) / size;
                    velocity2[j][i].x = 0;
                }
            }
        }

        set_bndVel(velocity2);
        lin_solve1Vel(velocity2, 1, 0.25);

        for(int j = 1; j < size - 1; j++)
        {
            for(int i = 1; i < size - 1; i++)
            {
                velocity1[j][i].x -= 0.5 * (velocity2[j][i + 1].x - velocity2[j][i - 1].x) * size;
                velocity1[j][i].y -= 0.5 * (velocity2[j + 1][i].x - velocity2[j - 1][i].x) * size;
            }
        }
        set_bndVel(velocity1);
    }

    public void advectVel(Vector2D[][] velocity, Vector2D[][] prevVelocity, double dt)
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
                tmp1 = dtx * prevVelocity[j][i].x;
                tmp2 = dty * prevVelocity[j][i].y;
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

                velocity[j][i] = Vector2D.add(
                    Vector2D.mult(Vector2D.add(Vector2D.mult(prevVelocity[j0i][i0i], t0), Vector2D.mult(prevVelocity[j1i][i0i], t1)), s0),
                    Vector2D.mult(Vector2D.add(Vector2D.mult(prevVelocity[j0i][i1i], t0), Vector2D.mult(prevVelocity[j1i][i1i], t1)), s1)
                );
            }
        }
        set_bndVel(velocity);
    }

    public void advectPD(double[][] density, double[][] pressure, Vector2D[][] velocity, double dt)
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
                s0 * (t0 * pressure[j0i][i0i] + t1 * pressure[j1i][i0i]) + 
                s1 * (t0 * pressure[j0i][i1i] + t1 * pressure[j1i][i1i]);
            }
        }
        set_bndPD(density);
    }
}