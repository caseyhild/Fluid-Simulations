import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class FluidSimulation3D extends JFrame implements Runnable
{
    private int frame;
    private final Thread thread;
    private boolean running;

    private final int scale;

    private final FluidCube fluidCube;

    public FluidSimulation3D()
    {
        //set size of screen
        int width = 512;
        int height = 512;

        //set initial frame
        frame = 0;

        //what will be displayed to the user
        thread = new Thread(this);

        int size = 16;
        scale = 256/ size;

        double diffusion = 0;
        double viscosity = 0.0000001;
        double dt = 0.2;
        fluidCube = new FluidCube(size, diffusion, viscosity, dt);

        //setting up the window
        setSize(width, height + 28);
        setResizable(false);
        setTitle("Fluid Simulation 3D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setBackground(new Color(0));

        //start the Simulation
        start();
    }

    private synchronized void start()
    {
        //starts game
        running = true;
        thread.start();
    }

    private void update()
    {
        //updates everything
        if(frame % 100 == 0)
        {
            fluidCube.addDensity(1, 1, 1, 50);
            Vector3D vel = new Vector3D(50);
            vel.setAngleXY(45);
            vel.setAngleXZ(45);
            vel.setAngleYZ(45);
            fluidCube.addVelocity(1, 1, 1, vel.x, vel.y, vel.z);
        }
        fluidCube.step();
        for(int z = 0; z < fluidCube.N; z++)
        {
            for(int y = 0; y < fluidCube.N; y++)
            {
                for(int x = 0; x < fluidCube.N; x++)
                {
                    fluidCube.density[z * fluidCube.N * fluidCube.N + y * fluidCube.N + x] *= 0.98;
                }
            }
        }
    }

    private void render()
    {
        //sets up graphics
        BufferStrategy bs = getBufferStrategy();
        if(bs == null)
        {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.translate(0, 28);

        int depth = 4;
        for(int z = 0; z < fluidCube.N; z++)
        {
            for(int y = 0; y < fluidCube.N; y++)
            {
                for(int x = 0; x < fluidCube.N; x++)
                {
                    double density = fluidCube.density[z * fluidCube.N * fluidCube.N + y * fluidCube.N + x];
                    int min = (int) Math.min(Math.max(0, 255 * density), 255);
                    int color = RGB(0, (int) Math.min(Math.max(0, 255 * density/2), 255), min);
                    g.setColor(new Color(R(color), G(color), B(color), min));
                    g.fillRect(x * scale + depth * z + 128, y * scale + depth * z + 128, scale, scale);
                }
            }
        }
        g.setColor(new Color(255, 255, 255));
        g.drawLine(128, 128, fluidCube.N * scale + 128, 128);
        g.drawLine(128, 128, 128, fluidCube.N * scale + 128);
        g.drawLine(128, 128, depth * fluidCube.N + 128, depth * fluidCube.N + 128);
        g.drawLine(fluidCube.N * scale + 128, fluidCube.N * scale + 128, 128, fluidCube.N * scale + 128);
        g.drawLine(fluidCube.N * scale + 128, fluidCube.N * scale + 128, fluidCube.N * scale + 128, 128);
        g.drawLine(fluidCube.N * scale + 128, fluidCube.N * scale + 128, fluidCube.N * scale + depth * fluidCube.N + 128, fluidCube.N * scale + depth * fluidCube.N + 128);
        g.drawLine(fluidCube.N * scale + depth * fluidCube.N + 128, depth * fluidCube.N + 128, depth * fluidCube.N + 128, depth * fluidCube.N + 128);
        g.drawLine(fluidCube.N * scale + depth * fluidCube.N + 128, depth * fluidCube.N + 128, fluidCube.N * scale + depth * fluidCube.N + 128, fluidCube.N * scale + depth * fluidCube.N + 128);
        g.drawLine(fluidCube.N * scale + depth * fluidCube.N + 128, depth * fluidCube.N + 128, fluidCube.N * scale + 128, 128);
        g.drawLine(depth * fluidCube.N + 128, fluidCube.N * scale + depth * fluidCube.N + 128, fluidCube.N * scale + depth * fluidCube.N + 128, fluidCube.N * scale + depth * fluidCube.N + 128);
        g.drawLine(depth * fluidCube.N + 128, fluidCube.N * scale + depth * fluidCube.N + 128, depth * fluidCube.N + 128, depth * fluidCube.N + 128);
        g.drawLine(depth * fluidCube.N + 128, fluidCube.N * scale + depth * fluidCube.N + 128, 128, fluidCube.N * scale + 128);

        //display all the graphics
        bs.show();
    }

    public void run()
    {
        //main game loop
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 60.0; //60 times per second
        double delta = 0;
        requestFocus();
        while(running)
        {
            //updates time
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            frame++;
            while(delta >= 1) //Make sure update is only happening 60 times a second
            {
                delta--;
            }
            //update
            update();
            //display to the screen
            render();
        }
    }

    private int RGB(int r, int g, int b)
    {
        return r << 16 | g << 8 | b;
    }

    private int R(int color)
    {
        return color >> 16;
    }

    private int G(int color)
    {
        return color >> 8 & 255;
    }

    private int B(int color)
    {
        return color & 255;
    }

    public static void main(String [] args)
    {
        new FluidSimulation3D();
    }
}