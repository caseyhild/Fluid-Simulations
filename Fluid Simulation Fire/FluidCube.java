public class FluidCube
{
    private final int iter = 1;
    public int N;
    private double dt;
    private double diff;
    private double visc;

    private double[] s;
    public double[][] density;

    private double[] Vx;
    public double[] Vy;
    private double[] Vx0;
    private double[] Vy0;

    public FluidCube(int numColors, int size, double diffusion, double viscosity, double dt)
    {
        N = size;
        this.dt = dt;
        diff = diffusion;
        visc = viscosity;

        s = new double[N * N];
        density = new double[numColors][N * N];
        Vx = new double[N * N];
        Vy = new double[N * N];
        Vx0 = new double[N * N];
        Vy0 = new double[N * N];
    }

    public void step()
    {
        diffuse(1, Vx0, Vx, visc, dt);
        diffuse(2, Vy0, Vy, visc, dt);

        project(Vx0, Vy0, Vx, Vy);

        advect(1, Vx, Vx0, Vx0, Vy0, dt);
        advect(2, Vy, Vy0, Vx0, Vy0, dt);

        project(Vx, Vy, Vx0, Vy0);

        for(int i = 0; i < density.length; i++)
        {
            diffuse(0, s, density[i], diff, dt);
            advect(0, density[i], s, Vx, Vy, dt);
        }
    }

    public void addDensity(int color, int x, int y, double amount)
    {
        int index = IX(x, y);
        density[color][index] += amount;
    }

    public void addVelocity(int x, int y, double amountX, double amountY)
    {
        int index = IX(x, y);
        Vx[index] += amountX;
        Vy[index] += amountY;
    }

    public void set_bnd(int b, double[] x)
    {
        for(int i = 1; i < N - 1; i++)
        {
            x[IX(i, 0)] = b == 2 ? -x[IX(i, 1)] : x[IX(i, 1)];
            x[IX(i, N - 1)] = b == 2 ? -x[IX(i, N - 2)] : x[IX(i, N - 2)];
        }
        for(int j = 1; j < N - 1; j++)
        {
            x[IX(0, j)] = b == 1 ? -x[IX(1, j)] : x[IX(1, j)];
            x[IX(N - 1, j)] = b == 1 ? -x[IX(N - 2, j)] : x[IX(N - 2, j)];
        }

        x[IX(0, 0)] = 0.5 * (x[IX(1, 0)] + x[IX(0, 1)]);
        x[IX(0, N - 1)] = 0.5 * (x[IX(1, N - 1)] + x[IX(0, N - 2)]);
        x[IX(N - 1, 0)] = 0.5 * (x[IX(N - 2, 0)] + x[IX(N - 1, 1)]);
        x[IX(N - 1, N - 1)] = 0.5 * (x[IX(N - 2, N - 1)] + x[IX(N - 1, N - 2)]);
    }

    public void lin_solve(int b, double[] x, double[] x0, double a, double c)
    {
        double cRecip = 1.0/c;
        for(int k = 0; k < iter; k++)
        {
            for(int j = 1; j < N - 1; j++)
            {
                for(int i = 1; i < N - 1; i++)
                {
                    x[IX(i, j)] = (x0[IX(i, j)] + a * (
                            x[IX(i + 1, j)]
                            +x[IX(i - 1, j)]
                            +x[IX(i, j + 1)]
                            +x[IX(i, j - 1)]
                        )) * cRecip;
                }
            }
            set_bnd(b, x);
        }
    }

    public void diffuse(int b, double[] x, double[] x0, double diff, double dt)
    {
        double a = dt * diff * (N - 2) * (N - 2);
        lin_solve(b, x, x0, a, 1 + 4 * a);
    }

    public void project(double[] velX, double[] velY, double[] p, double[] div)
    {
        for(int j = 1; j < N - 1; j++)
        {
            for(int i = 1; i < N - 1; i++)
            {
                div[IX(i, j)] = -0.5 * (
                    velX[IX(i + 1, j)]
                    -velX[IX(i - 1, j)]
                    +velY[IX(i, j + 1)]
                    -velY[IX(i, j - 1)]
                ) / N;
                p[IX(i, j)] = 0;
            }
            }

        set_bnd(0, div);
        set_bnd(0, p);
        lin_solve(0, p, div, 1, 4);

        for(int j = 1; j < N - 1; j++)
        {
            for(int i = 1; i < N - 1; i++)
            {
                velX[IX(i, j)] -= 0.5 * (p[IX(i + 1, j)] - p[IX(i - 1, j)]) * N;
                velY[IX(i, j)] -= 0.5 * (p[IX(i, j + 1)] - p[IX(i, j - 1)]) * N;
            }
        }
        set_bnd(1, velX);
        set_bnd(2, velY);
    }

    public void advect(int b, double[] d, double[] d0, double[] velX, double[] velY, double dt)
    {
        double i0, i1, j0, j1;

        double dtx = dt * (N - 2);
        double dty = dt * (N - 2);

        double s0, s1, t0, t1;
        double tmp1, tmp2, x, y;

        double Nfloat = N;
        double ifloat, jfloat;
        int i, j;

        for(j = 1, jfloat = 1; j < N - 1; j++, jfloat++)
        {
            for(i = 1, ifloat = 1; i < N - 1; i++, ifloat++)
            {
                tmp1 = dtx * velX[IX(i, j)];
                tmp2 = dty * velY[IX(i, j)];
                x = ifloat - tmp1;
                y = jfloat - tmp2;

                if(x < 0.5)
                    x = 0.5;
                if(x > Nfloat - 1.5)
                    x = Nfloat - 1.5;
                i0 = Math.floor(x);
                i1 = i0 + 1;
                if(y < 0.5)
                    y = 0.5;
                if(y > Nfloat - 1.5)
                    y = Nfloat - 1.5;
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

                d[IX(i, j)] = 
                s0 * (t0 * d0[IX(i0i, j0i)] + t1 * d0[IX(i0i, j1i)]) +
                s1 * (t0 * d0[IX(i1i, j0i)] + t1 * d0[IX(i1i, j1i)]);
            }
        }
        set_bnd(b, d);
    }

    public int IX(int x, int y)
    {
        return x + y * N;
    }
}