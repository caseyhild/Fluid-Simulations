public class FluidCube
{
    private final int iter = 1;
    public int N;
    private double dt;
    private double diff;
    private double visc;

    private double[] s;
    public double[] density;

    private double[] Vx;
    private double[] Vy;
    private double[] Vz;
    private double[] Vx0;
    private double[] Vy0;
    private double[] Vz0;

    public FluidCube(int size, double diffusion, double viscosity, double dt)
    {
        N = size;
        this.dt = dt;
        diff = diffusion;
        visc = viscosity;

        s = new double[N * N * N];
        density = new double[N * N * N];
        Vx = new double[N * N * N];
        Vy = new double[N * N * N];
        Vz = new double[N * N * N];
        Vx0 = new double[N * N * N];
        Vy0 = new double[N * N * N];
        Vz0 = new double[N * N * N];
    }

    public void step()
    {
        diffuse(1, Vx0, Vx, visc, dt);
        diffuse(2, Vy0, Vy, visc, dt);
        diffuse(3, Vz0, Vz, visc, dt);

        project(Vx0, Vy0, Vz0, Vx, Vy);

        advect(1, Vx, Vx0, Vx0, Vy0, Vz0, dt);
        advect(2, Vy, Vy0, Vx0, Vy0, Vz0, dt);
        advect(3, Vz, Vz0, Vx0, Vy0, Vz0, dt);

        project(Vx, Vy, Vz, Vx0, Vy0);

        diffuse(0, s, density, diff, dt);
        advect(0, density, s, Vx, Vy, Vz, dt);
    }

    public void addDensity(int x, int y, int z, double amount)
    {
        int index = IX(x, y, z);
        density[index] += amount;
    }

    public void addVelocity(int x, int y, int z, double amountX, double amountY, double amountZ)
    {
        int index = IX(x, y, z);
        Vx[index] += amountX;
        Vy[index] += amountY;
        Vz[index] += amountZ;
    }

    public void set_bnd(int b, double[] x)
    {
        for(int j = 1; j < N - 1; j++)
        {
            for(int i = 1; i < N - 1; i++)
            {
                x[IX(i, j, 0)] = b == 3 ? -x[IX(i, j, 1)] : x[IX(i, j, 1)];
                x[IX(i, j, N - 1)] = b == 3 ? -x[IX(i, j, N - 2)] : x[IX(i, j, N - 2)];
            }
        }
        for(int k = 1; k < N - 1; k++)
        {
            for(int i = 1; i < N - 1; i++)
            {
                x[IX(i, 0, k)] = b == 2 ? -x[IX(i, 1, k)] : x[IX(i, 1, k)];
                x[IX(i, N - 1, k)] = b == 2 ? -x[IX(i, N - 2, k)] : x[IX(i, N - 2, k)];
            }
        }
        for(int k = 1; k < N - 1; k++)
        {
            for(int j = 1; j < N - 1; j++)
            {
                x[IX(0, j, k)] = b == 1 ? -x[IX(1, j, k)] : x[IX(1, j, k)];
                x[IX(N - 1, j, k)] = b == 1 ? -x[IX(N - 2, j, k)] : x[IX(N - 2, j, k)];
            }
        }

        x[IX(0, 0, 0)] = 0.33 * (x[IX(1, 0, 0)] + x[IX(0, 1, 0)] + x[IX(0, 0, 1)]);
        x[IX(0, N - 1, 0)] = 0.33 * (x[IX(1, N - 1, 0)] + x[IX(0, N - 2, 0)] + x[IX(0, N - 1, 1)]);
        x[IX(0, 0, N - 1)] = 0.33 * (x[IX(1, 0, N - 1)] + x[IX(0, 1, N - 1)] + x[IX(0, 0, N - 2)]);
        x[IX(0, N - 1, N - 1)] = 0.33 * (x[IX(1, N - 1, N - 1)] + x[IX(0, N - 2, N - 1)] + x[IX(0, N - 1, N - 2)]);
        x[IX(N - 1, 0, 0)] = 0.33 * (x[IX(N - 2, 0, 0)] + x[IX(N - 1, 1, 0)] + x[IX(N - 1, 0, 1)]);
        x[IX(N - 1, N - 1, 0)] = 0.33 * (x[IX(N - 2, N - 1, 0)] + x[IX(N - 1, N - 2, 0)] + x[IX(N - 1, N - 1, 1)]);
        x[IX(N - 1, 0, N - 1)] = 0.33 * (x[IX(N - 2, 0, N - 1)] + x[IX(N - 1, 1, N - 1)] + x[IX(N - 1, 0, N - 2)]);
        x[IX(N - 1, N - 1, N - 1)] = 0.33 * (x[IX(N - 2, N - 1, N - 1)] + x[IX(N - 1, N - 2, N - 1)] + x[IX(N - 1, N - 1, N - 2)]);
    }

    public void lin_solve(int b, double[] x, double[] x0, double a, double c)
    {
        double cRecip = 1.0/c;
        for(int k = 0; k < iter; k++)
        {
            for(int m = 1; m < N - 1; m++)
            {
                for(int j = 1; j < N - 1; j++)
                {
                    for(int i = 1; i < N - 1; i++)
                    {
                        x[IX(i, j, m)] = (x0[IX(i, j, m)] + a * (
                                x[IX(i + 1, j, m)]
                                +x[IX(i - 1, j, m)]
                                +x[IX(i, j + 1, m)]
                                +x[IX(i, j - 1, m)]
                                +x[IX(i, j, m + 1)]
                                +x[IX(i, j, m - 1)]
                            )) * cRecip;
                    }
                }
            }
            set_bnd(b, x);
        }
    }

    public void diffuse(int b, double[] x, double[] x0, double diff, double dt)
    {
        double a = dt * diff * (N - 2) * (N - 2);
        lin_solve(b, x, x0, a, 1 + 6 * a);
    }

    public void project(double[] velX, double[] velY, double[] velZ, double[] p, double[] div)
    {
        for(int k = 1; k < N - 1; k++)
        {
            for(int j = 1; j < N - 1; j++)
            {
                for(int i = 1; i < N - 1; i++)
                {
                    div[IX(i, j, k)] = -0.5 * (
                        velX[IX(i + 1, j, k)]
                        -velX[IX(i - 1, j, k)]
                        +velY[IX(i, j + 1, k)]
                        -velY[IX(i, j - 1, k)]
                        +velZ[IX(i, j, k + 1)]
                        -velZ[IX(i, j, k - 1)]
                    ) / N;
                    p[IX(i, j, k)] = 0;
                }
            }
        }
        set_bnd(0, div);
        set_bnd(0, p);
        lin_solve(0, p, div, 1, 6);

        for(int k = 1; k < N - 1; k++)
        {
            for(int j = 1; j < N - 1; j++)
            {
                for(int i = 1; i < N - 1; i++)
                {
                    velX[IX(i, j, k)] -= 0.5 * (p[IX(i + 1, j, k)] - p[IX(i - 1, j, k)]) * N;
                    velY[IX(i, j, k)] -= 0.5 * (p[IX(i, j + 1, k)] - p[IX(i, j - 1, k)]) * N;
                    velZ[IX(i, j, k)] -= 0.5 * (p[IX(i, j, k + 1)] - p[IX(i, j, k - 1)]) * N;
                }
            }
        }
        set_bnd(1, velX);
        set_bnd(2, velY);
        set_bnd(3, velZ);
    }

    public void advect(int b, double[] d, double[] d0, double[] velX, double[] velY, double[] velZ, double dt)
    {
        double i0, i1, j0, j1, k0, k1;

        double dtx = dt * (N - 2);
        double dty = dt * (N - 2);
        double dtz = dt * (N - 2);

        double s0, s1, t0, t1, u0, u1;
        double tmp1, tmp2, tmp3, x, y, z;

        double Nfloat = N;
        double ifloat, jfloat, kfloat;
        int i, j, k;

        for(k = 1, kfloat = 1; k < N - 1; k++, kfloat++)
        {
            for(j = 1, jfloat = 1; j < N - 1; j++, jfloat++)
            {
                for( i = 1, ifloat = 1; i < N - 1; i++, ifloat++)
                {
                    tmp1 = dtx * velX[IX(i, j, k)];
                    tmp2 = dty * velY[IX(i, j, k)];
                    tmp3 = dtz * velZ[IX(i, j, k)];
                    x = ifloat - tmp1;
                    y = jfloat - tmp2;
                    z = kfloat - tmp3;

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
                    if(z < 0.5)
                        z = 0.5;
                    if(z > Nfloat - 1.5)
                        z = Nfloat - 1.5;
                    k0 = Math.floor(z);
                    k1 = k0 + 1;

                    s1 = x - i0;
                    s0 = 1 - s1;
                    t1 = y - j0;
                    t0 = 1 - t1;
                    u1 = z - k0;
                    u0 = 1 - u1;

                    int i0i = (int) i0;
                    int i1i = (int) i1;
                    int j0i = (int) j0;
                    int j1i = (int) j1;
                    int k0i = (int) k0;
                    int k1i = (int) k1;

                    d[IX(i, j, k)] = 
                    s0 * (t0 * (u0 * d0[IX(i0i, j0i, k0i)] + u1 * d0[IX(i0i, j0i, k1i)])
                        + (t1 * (u0 * d0[IX(i0i, j1i, k0i)] + u1 * d0[IX(i0i, j1i, k1i)])))
                    + s1 * (t0 * (u0 * d0[IX(i1i, j0i, k0i)] + u1 * d0[IX(i1i, j0i, k1i)])
                        + (t1 * (u0 * d0[IX(i1i, j1i, k0i)] + u1 * d0[IX(i1i, j1i, k1i)])));
                }
            }
        }
        set_bnd(b, d);
    }

    public int IX(int x, int y, int z)
    {
        return x + y * N + z * N * N;
    }
}