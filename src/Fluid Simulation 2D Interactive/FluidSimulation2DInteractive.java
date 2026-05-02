import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class FluidSimulation2DInteractive extends JFrame implements Runnable, MouseListener, MouseMotionListener
{
    private final int width; //width of screen
    private final int height; //height of screen
    private final Thread thread; //thread of execution for the program
    private boolean running; //true while program is running
    private final BufferedImage image; //allows array of pixels to be drawn to the screen
    private final int[] pixels; //array of pixels on screen (pixel (x, y) = pixels[y * width + x])

    private final FluidCube fluidCube;

    private int mouseX; //x coordinate of mouse
    private int mouseY; //y coordinate of mouse
    private int oldMouseX; //old x coordinate of mouse
    private int oldMouseY; //old y coordinate of mouse
    private boolean mousePressed; //whether mouse is being pressed (still or moving)

    public FluidSimulation2DInteractive()
    {
        //set size of screen
        width = 512;
        height = 512;

        //what will be displayed to the user
        thread = new Thread(this);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        //make the fluid
        fluidCube = new FluidCube(0.0000001, 0.0000001, 0.2);

        //mouse input
        mouseX = 0;
        mouseY = 0;
        mousePressed = false;
        addMouseListener(this);
        addMouseMotionListener(this);

        //setting up the window
        setSize(width, height + 28);
        setResizable(false);
        setTitle("Fluid Simulation 2D Interactive");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        //start the program
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

        if(mousePressed && mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height)
        {
            fluidCube.addDensity(mouseX * fluidCube.size/width, mouseY * fluidCube.size/height, 10);
            Vector2D vel = new Vector2D(mouseX - oldMouseX, mouseY - oldMouseY);
            fluidCube.addVelocity(mouseX * fluidCube.size/width, mouseY * fluidCube.size/height, vel.x, vel.y);
        }

        fluidCube.step();
        for(int y = 0; y < fluidCube.size; y++)
        {
            for(int x = 0; x < fluidCube.size; x++)
            {
                fluidCube.density[y][x] *= 0.995;
            }
        }

        oldMouseX = mouseX;
        oldMouseY = mouseY;
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

        //draws pixel array to screen
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                int shade = (int) (255 * fluidCube.density[y * fluidCube.size/height][x * fluidCube.size/width]);
                shade = Math.max(0, Math.min(shade, 255));
                pixels[y * width + x] = RGB(shade, shade, shade);
            }
        }
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

        //display all the graphics
        bs.show();
    }

    public void run()
    {
        //main program loop
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
            while(delta >= 1) //Make sure update is only happening 60 times a second
            {
                //update
                update();
                delta--;
            }
            //display to the screen
            render();
        }
    }

    private int RGB(int r, int g, int b)
    {
        return r << 16 | g << 8 | b;
    }

    public void mouseClicked(MouseEvent me)
    {

    }

    public void mouseEntered(MouseEvent me)
    {

    }

    public void mouseExited(MouseEvent me)
    {

    }

    public void mousePressed(MouseEvent me)
    {
        mousePressed = true;
    }

    public void mouseReleased(MouseEvent me)
    {
        mousePressed = false;
    }

    public void mouseDragged(MouseEvent me)
    {
        mousePressed = true;
        mouseX = me.getX() - 1;
        mouseY = me.getY() - 31;
    }

    public void mouseMoved(MouseEvent me)
    {
        mousePressed = false;
        mouseX = me.getX() - 1;
        mouseY = me.getY() - 31;
    }

    public static void main(String [] args)
    {
        new FluidSimulation2DInteractive();
    }
}