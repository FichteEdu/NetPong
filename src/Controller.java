import java.lang.Math;
import java.awt.Point;
import java.awt.Rectangle;

public class Controller {
    String version = "0.1 PRE-ALPHA";

    int punkte = 0;
    ListenForKeys keyListener = new ListenForKeys(); // ListenForKeys gets new instance
    Calculations calc = new Calculations();
                                                     
    private Interface inter;
    private GameSocket socket;
    private Rectangle field;

    private final double barSpeed = 20; // movementspeed of the bars
    private boolean gameRunning = true; // set to false for pause
    private Point ballDelta = new Point();
    private double gameSpeed = 6;
    private int sleep = 10;
    
    private double directionDouble;
    

    public static void main(String[] args) {
        new Controller();
    }

    public Controller() {
        // establish connection
        Connector c = new Connector("NetPong " + version + " - Connect");
        socket = c.connect();
        c.dispose();
        
        // initialize game interface
        inter = new Interface("NetPong " + version + " - " + (socket.isHost() ? "Host" : "Client"));
        inter.addKeyListener(keyListener);
        field = inter.getField();

        startBall();

        while (gameRunning) {
            // check for input
            moveBar();

            // do math if host
            if (socket.isHost()) {
                checkCollisions();
                moveBall(1);
                checkScore();
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
    
    private double[] randDirection(double low, double high) {
        double direction[] = {
                Math.random() * (high - low) + low,
                Math.random() * (high - low) + low
        };

        if (Math.random() > 0.5)
            direction[0] *= -1;
        if (Math.random() > 0.5)
            direction[1] *= -1;

        return direction;
    }

    private void checkScore() {
        if (inter.ball.x < inter.blocks[0].getMinX()) {
            inter.score[1]++;
            initBall();
        }
        if (inter.ball.x > inter.blocks[1].getMaxX()) {
            inter.score[0]++;
            initBall();
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
        if (    inter.ball.y         + ballDelta.y < field.y ||
                inter.ball.getMaxY() + ballDelta.y > field.getMaxY())
            ballDelta.y *= -1;
            

        // check if the ball will intersect a block
        // move the ball and move it back to use awesome `intersects()` method
        moveBall(1);
        
        boolean collides = inter.ball.intersects(inter.blocks[0])
                        || inter.ball.intersects(inter.blocks[1]);
        moveBall(-1);
        if (collides)
            ballDelta.x *= -1;
    }

    private void moveBall(int times) {
        inter.ball.shiftLocation(times * ballDelta.x, times * ballDelta.y);
    }

    private void startBall() {
        // possibly add checks for right direction
        double[] direction = randDirection(7,12);
        ballDelta.setLocation(calc.normVector(direction)[0] * gameSpeed, calc.normVector(direction)[1] * gameSpeed);
        System.out.println(""+ ballDelta.getX() + "," + ballDelta.getY() + "," + directionDouble);
    }

    private void initBall() {
        inter.ball.setLocation(inter.getHeight() / 2, inter.getWidth() / 2);
        startBall();
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
