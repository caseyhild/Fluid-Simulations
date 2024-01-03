import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;

public class Simulation extends JFrame implements Runnable
{  
    private static final long serialVersionUID = 1L;
    private int width;
    private int height;
    private int frame;
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private int[] pixels;

    private FluidCube fluidCube;

    public Simulation()
    {
        //set size of screen
        width = 512;
        height = 512;

        //set initial frame
        frame = 0;

        //what will be displayed to the user
        thread = new Thread(this);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        fluidCube = new FluidCube(0.0000001, 0.0000001, 0.2);

        //setting up the window
        setSize(width + 16, height + 39);
        setResizable(false);
        setTitle("Simulation");
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

    private synchronized void stop()
    {
        //stops game
        running = false;
        try
        {
            thread.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
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

        //draws pixel array to screen
        //g.drawImage(image, 8, 31, image.getWidth(), image.getHeight(), null);

        g.translate(8, 31);
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
        Simulation s = new Simulation();
    }
}