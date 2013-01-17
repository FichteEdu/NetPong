import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
  * ToDoc
  */

public class Interface extends JFrame {
    private static final long serialVersionUID = -426887329375649342L;

    // colors
    private Color bgcolor = new Color(0x1b1b1b);
    private Color fgcolor = new Color(0xbdfab8);
    
    // define some constants
    private int width   = 800,
                height  = 600,
                vmargin =  50,
                hmargin =  50,
                borderw =  15;
    
    private int ballSize = 10;
    
    private Dimension blockDim     = new Dimension(15, 80),
                      separatorDim = new Dimension(10, 10);

    // dynamic components
    /**
     * Extended Rectangle2D.Double class for easy drawing. Use
     *     {@code ball.setLocation(double x, double y)}
     * to modify the ball's position.
     */
    public DoubleFillRect ball = new DoubleFillRect(
            vmargin + 50, // default ball position
            (height - ballSize) / 2,
            ballSize,
            ballSize);
    /**
     * Array of extended Rectangle2D.Double class for easy drawing. Use
     *     {@code blocks[n].setLocation(double x, double y)}
     * to modify the ball's position, while n being either 1 or 2.
     */
    public DoubleFillRect blocks[] = {
            new DoubleFillRect(
                    hmargin,
                    (height - blockDim.height) / 2,
                    blockDim.width,
                    blockDim.height),
            new DoubleFillRect(
                    width - hmargin - blockDim.width,
                    (height - blockDim.height) / 2,
                    blockDim.width,
                    blockDim.height)
    };

    public Interface(String title) {
        super(title);
        // stuff
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(width, height);
        setResizable(false);
        setLayout(null);

        // center the window
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width  - getSize().width ) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
    }

    @Override
    public void paint(Graphics g) {
        // TODO: Double buffering
        super.paint(g); // I don't know if this is necessary
        Rectangle bounds = getFieldBounds();
        
        // background
        g.setColor(bgcolor);
        g.fillRect(0, 0, width, height);
        
        g.setColor(fgcolor);
        
        // border boxes
        g.fillRect(
                hmargin,
                vmargin,
                bounds.width,
                borderw);
        g.fillRect(
                hmargin,
                dtoi(bounds.getMaxY()),
                bounds.width,
                borderw);
        
        // dotted seperator line
        int h = separatorDim.height,
            x = (width + separatorDim.width) / 2;
        ;
        for (int y = bounds.y + h; y < height - vmargin - borderw - h; y += h * 2)
            g.fillRect(x, y, separatorDim.width, h);
        
        // dynamic content
        ball.paint(g);
        for (DoubleFillRect b : blocks)
            b.paint(g);
        
        // TODO: score
    }

    public void dispose() {
        // exit when window is closed
        super.dispose();
        System.exit(0);
    }

    /**
     * @return The actual bounds of the playable field without borders
     */
    public Rectangle getFieldBounds() {
        int top = vmargin + borderw;
        return new Rectangle(
                hmargin,
                top,
                width - hmargin * 2,
                height - top * 2);
    }
    
    private int dtoi(double d) {
        return (int) Math.round(d);
    }

    /* keep this to test things */
    public static void main(String[] args) {
        Interface inter = new Interface("NetPong 0.1");
        inter.setVisible(true);
    }
}

/**
 * Extends Rectangle2D.Double by a function to "fill" a rect on a Graphics object.
 * Removes the necessity for casting/rounding the values to integers.
 *  
 */
class DoubleFillRect extends Rectangle2D.Double {
    private static final long serialVersionUID = -1144349081899068748L;

    public DoubleFillRect(int i, int j, int width, int height) {
        super(i, j, width, height);
    }

    /**
     * Calls {@code g.fillRect()} with the rounded extends of this rectangle.
     * 
     * @param g Graphics object to be drawn onto.
     */
    public void paint(Graphics g) {
        g.fillRect(dtoi(x), dtoi(y), dtoi(width), dtoi(height));
    }
    
    /**
     * Set new x and y coordinates.
     * 
     * @param x New x position.
     * @param y New y position.
     */
    public void setLocation(double x, double y) {
        setRect(x, y, width, height);
    }
    
    /**
     * Set new y coordinate.
     * 
     * @param y New y position.
     */
    public void setY(double y) {
        setRect(x, y, width, height);
    }
    
    /**
     * Shift the current x and y coordinates by byx and byy.
     * 
     * @param byx To shift x coordinate by.
     * @param byy To shift y coordinate by.
     */
    public void shiftLocation(double byx, double byy) {
        setRect(x + byx, y + byy, width, height);
    }
    
    private int dtoi(double d) {
        return (int) Math.round(d);
    }
}