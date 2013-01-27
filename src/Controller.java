import java.lang.Math;
import java.awt.Point;
import java.awt.Rectangle;

public class Controller {
    String version = "0.1 PRE-ALPHA";

    int punkte = 0;
    ListenForKeys keyListener = new ListenForKeys(); // ListenForKeys gets new
                                                       // instance
    private Interface inter;
    private GameSocket socket;
    private Rectangle field;

    private final double barSpeed = 20; // movementspeed of the bars
    private boolean gameRunning = true; // set to false for pause
    private Point ballDelta = new Point();
    private double gameSpeed = 12;
    private int sleep = 10;

    public static void main(String[] args) {
        new Controller();
    }

    public Controller() {
        // establish connection
        Connector c = new Connector("NetPong " + version + " - Connect");
        socket = c.connect();
        c.dispose();
        
        // initialize game interface
        inter = new Interface("NetPong " + version);
        inter.addKeyListener(keyListener);
        field = inter.getField();

        startBall();

        while (gameRunning) {
            // check for input
            moveBar();

            // do math iff host
            if (socket.isHost()) {
                checkCollisions();
                moveBall(1);
            }

            // communication
            writePositions();
            readPositions(); //reads AND SETS positions

            // update interface
            inter.repaint();

            try {
                Thread.sleep(sleep);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void readPositions() {
        if (socket.isHost())
            inter.blocks[1].setY(socket.getBlock());
        else {
            double pos[] = socket.getPositions();
            inter.ball.setLocation(pos[0], pos[1]);
            inter.blocks[0].setY(pos[2]);
        }
    }
    
    private void writePositions() {
        if (socket.isHost())
            socket.writePositions(inter.ball.x, inter.ball.y, inter.blocks[0].y);
        else
            socket.writeBar(inter.blocks[1].y);
    }

    private void checkCollisions() {
        // check if the ball will be in the horizontal bounds
        if (    inter.ball.y         + ballDelta.y < field.y &&
                inter.ball.getMaxY() + ballDelta.y < field.getMaxY())
            ballDelta.y *= -1;

        // check if the ball will intersect a block
        // move the ball and move it back to use awesome `intersects()` method
        moveBall(1);
        boolean collides = inter.ball.intersects(inter.blocks[0])
                        || inter.ball.intersects(inter.blocks[1]);
        moveBall(-1);
        if (collides)
            ballDelta.x *= -1;

        // TODO game over
        //if (!field.contains(inter.ball)) 
    }

    private void moveBall(int times) {
        inter.ball.shiftLocation(times * ballDelta.x, times * ballDelta.y);
    }

    private void startBall() {
        // possibly add checks for right direction
        ballDelta.setLocation(Math.random() * gameSpeed, Math.random() * gameSpeed);
    }

    private void moveBar() {
        DoubleFillRect block = inter.blocks[socket.isHost() ? 0 : 1];

        // TODO key inputs
        if (keyListener.isUp()) {
            if (block.y - barSpeed > field.y)
                block.shiftLocation(0, -barSpeed);
        }

        if (keyListener.isDown()) {
            if (block.getMaxY() + barSpeed < field.getMaxY())
                block.shiftLocation(0, barSpeed);
            // else jiggle(move up 9px or something)
            // maybe jiggle but that would be hard(er)
        }
    }
}
