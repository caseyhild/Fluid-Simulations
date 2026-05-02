import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;

public class FireSimulation extends JFrame implements Runnable
{
    private final int width;
    private final int height;
    private int frame;
    private final Thread thread;
    private boolean running;

    private final int size;
    private final int scale;

    private final FluidCube fluidCube;
    private final ArrayList<Integer> colors;

    public FireSimulation()
    {
        //set size of screen
        width = 512;
        height = 512;

        //set initial frame
        frame = 0;

        //what will be displayed to the user
        thread = new Thread(this);

        size = 128;
        scale = 512/size;

        double diffusion = 0;
        double viscosity = 0.0000001;
        double dt = 0.2;
        fluidCube = new FluidCube(3, size, diffusion, viscosity, dt);

        colors = new ArrayList<>();
        colors.add(RGB(255, 0, 0));
        colors.add(RGB(192, 128, 0));
        colors.add(RGB(255, 128, 0));

        //setting up the window
        setSize(width, height + 28);
        setResizable(false);
        setTitle("Fire Simulation");
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
        for(int x = size/3; x < 2 * size/3; x++)
        {
            int random = (int) (Math.random() * fluidCube.density.length);
            fluidCube.addDensity(random, x, size - 2, 1);
            Vector2D vel = new Vector2D(0.25, 0);
            vel.setAngleXY(Math.random() * 60 + 240);
            fluidCube.addVelocity(x, size - 2, vel.x, vel.y);
        }
        fluidCube.step();
        for(int y = 0; y < size; y++)
        {
            for(int x = 0; x < size; x++)
            {
                for(int i = 0; i < fluidCube.density.length; i++)
                    fluidCube.density[i][y * size + x] *= 0.9;
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
        for(int y = 0; y < size; y++)
        {
            for(int x = 0; x < size; x++)
            {
                int red = 0;
                int green = 0;
                int blue = 0;
                for(int i = 0; i < fluidCube.density.length; i++)
                {
                    double density = fluidCube.density[i][y * size + x];
                    red += (int) (R(colors.get(i)) * density);
                    green += (int) (G(colors.get(i)) * density);
                    blue += (int) (B(colors.get(i)) * density);
                }
                red /= fluidCube.density.length;
                green /= fluidCube.density.length;
                blue /= fluidCube.density.length;
                g.setColor(new Color(Math.min(Math.max(0, red), 255), Math.min(Math.max(0, green), 255), Math.min(Math.max(0, blue), 255)));
                g.fillRect(x * scale, y * scale - 50, scale, scale);
            }
        }

        g.setColor(new Color(128, 128, 128));
        g.fillRect(width/3 - 10, height - 50 - scale, width/3 + 20, 50 + scale);
        g.setColor(new Color(64, 64, 64));
        g.fillRect(width/3 - 10, height - 50 - scale, width/3 + 20, 10);
        g.fillRect(width/3 - 10, height - 10, width/3 + 20, 10);

        //display all the graphics
        bs.show();
    }

    public void run()
    {
        //main game loop
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 120.0; //60 times per second
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
            if(frame % 3 == 0)
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
        new FireSimulation();
    }
}