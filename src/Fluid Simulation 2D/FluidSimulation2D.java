import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class FluidSimulation2D extends JFrame implements Runnable
{
    private int frame;
    private final Thread thread;
    private boolean running;

    private final FluidCube fluidCube;

    public FluidSimulation2D()
    {
        //set size of screen
        int width = 512;
        int height = 512;

        //set initial frame
        frame = 0;

        //what will be displayed to the user
        thread = new Thread(this);

        fluidCube = new FluidCube(0.0000001, 0.0000001, 0.2);

        //setting up the window
        setSize(width, height + 28);
        setResizable(false);
        setTitle("Fluid Simulation 2D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

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
        fluidCube.addDensity(fluidCube.N/2, fluidCube.N/2, 50);
        Vector2D vel = new Vector2D(10, 0);
        vel.setAngleXY(2 * frame);
        fluidCube.addVelocity(fluidCube.N/2, fluidCube.N/2, vel.x, vel.y);
        fluidCube.step();
        for(int y = 0; y < fluidCube.N; y++)
        {
            for(int x = 0; x < fluidCube.N; x++)
            {
                fluidCube.density[y * fluidCube.N + x] *= 0.995;
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
        
        for(int y = 0; y < fluidCube.N; y++)
        {
            for(int x = 0; x < fluidCube.N; x++)
            {
                double density = fluidCube.density[y * fluidCube.N + x];
                int color = RGB(0, (int) Math.min(Math.max(0, 255 * density/2), 255), (int) Math.min(Math.max(0, 255 * density), 255));
                g.setColor(new Color(color));
                g.fillRect(x * 4, y * 4, 4, 4);
            }
        }

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

    public static void main(String [] args)
    {
        new FluidSimulation2D();
    }
}