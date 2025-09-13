import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class Simulation extends JFrame implements Runnable, MouseListener, MouseMotionListener, KeyListener
{  
    private static final long serialVersionUID = 1L;
    private int width; //width of screen
    private int height; //height of screen
    private int frame; //current frame of program
    private Thread thread; //thread of execution for the program
    private boolean running; //true while program is running
    private BufferedImage image; //allows array of pixels to be drawn to the screen
    private int[] pixels; //array of pixels on screen (pixel (x, y) = pixels[y * width + x])

    private FluidCube fluidCube;

    private int mouseX; //x coordinate of mouse
    private int mouseY; //y coordinate of mouse
    private int oldMouseX; //old x coordinate of mouse
    private int oldMouseY; //old y coordinate of mouse
    private boolean mousePressed; //whether or not mouse is being pressed (still or moving)
    private boolean keyPressed; //whether or not any key is being pressed
    private boolean keyReleased; //true immediately after key is released
    private boolean keyTyped; //true if key is pressed and a valid unicode character is generated
    private KeyEvent key; //key currently pressed (or last one pressed if none are pressed)

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

        //make the fluid
        fluidCube = new FluidCube(0.0000001, 0.0000001, 0.2);

        //keyboard input
        keyPressed = false;
        keyReleased = false;
        keyTyped = false;
        key = new KeyEvent(new JFrame(), 0, 0, 0, 0, KeyEvent.CHAR_UNDEFINED);
        addKeyListener(this);

        //mouse input
        mouseX = 0;
        mouseY = 0;
        mousePressed = false;
        addMouseListener(this);
        addMouseMotionListener(this);

        //setting up the window
        setSize(width + 16, height + 39);
        setResizable(false);
        setTitle("Program");
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
        
        //reset key states
        if(keyReleased)
            keyReleased = false;
        if(keyTyped)
            keyTyped = false;
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
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                int shade = (int) (255 * fluidCube.density[y * fluidCube.size/height][x * fluidCube.size/width]);
                shade = Math.max(0, Math.min(shade, 255));
                pixels[y * width + x] = RGB(shade, shade, shade);
            }
        }
        g.drawImage(image, 8, 31, image.getWidth(), image.getHeight(), null);

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
            frame++;
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
        mouseX = me.getX() - 8;
        mouseY = me.getY() - 31;
    }

    public void mouseMoved(MouseEvent me)
    {
        mousePressed = false;
        mouseX = me.getX() - 8;
        mouseY = me.getY() - 31;
    }

    public void keyPressed(KeyEvent key)
    {
        keyPressed = !keyTyped;
        this.key = key;
    }

    public void keyReleased(KeyEvent key)
    {
        keyPressed = false;
        keyReleased = true;
        this.key = key;
    }

    public void keyTyped(KeyEvent key)
    {
        keyTyped = true;
    }

    public static void main(String [] args)
    {
        Simulation s = new Simulation();
    }
}