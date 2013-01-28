import java.lang.Math;
import java.awt.Rectangle;

public class Controller {
    String version = "0.1 PRE-ALPHA";

    int punkte = 0;
    ListenForKeys keyListener = new ListenForKeys();

    private Interface inter;
    private GameSocket socket;
    private Rectangle field;

    private final double barSpeed = 10; // movementspeed of the bars
    private boolean gameRunning = true; // set to false for pause
    private Vector2D ballVect;
    private double gameSpeed = 6;
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
            }

            // communication
            writePositions();
            readPositions();

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
            inter.bars[1].setY(socket.getBlock());
        else {
            double pos[] = socket.getPositions();
            inter.ball.setLocation(pos[0], pos[1]);
            inter.bars[0].setY(pos[2]);
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
    
    private void writePositions() {
        if (socket.isHost())
            socket.writePositions(inter.ball.x, inter.ball.y, inter.bars[0].y);
        else
            socket.writeBar(inter.bars[1].y);
    }

    private void checkCollisions() {
        // check if the ball will be in the horizontal bounds
        if (    inter.ball.y         + ballVect.y < field.y ||
                inter.ball.getMaxY() + ballVect.y > field.getMaxY())
            ballVect.y *= -1;

        // check if the ball will intersect a bar
        // move the ball and move it back to use awesome `intersects()` method
        moveBall(1);

        boolean collides = inter.ball.intersects(inter.bars[0])
                        || inter.ball.intersects(inter.bars[1]);
        if (collides)
            ballVect.x *= -1;
        else {
            // if the ball is out of bounds
            if (inter.ball.x < inter.bars[0].getMaxX()) {
                inter.score[1]++;
                initBall();
            }
            if (inter.ball.getMaxX() > inter.bars[1].x) {
                inter.score[0]++;
                initBall();
            }
        }

        // revert changes
        moveBall(-1);
    }

    private void moveBall(int times) {
        inter.ball.shiftLocation(times * ballVect.x, times * ballVect.y);
    }

    private void startBall() {
        // TODO: possibly add checks for right direction
        double[] dir = randDirection(7, 12);
        ballVect = new Vector2D(dir[0], dir[1]).normalized();
        ballVect.scale(gameSpeed);
        ballVect.print();
    }

    private void initBall() {
        inter.ball.setLocation(inter.getHeight() / 2, inter.getWidth() / 2);
        startBall();
    }

    private void moveBar() {
        DoubleFillRect bar = inter.bars[socket.isHost() ? 0 : 1];

        if (keyListener.isUp()) {
            if (bar.y - barSpeed > field.y)
                bar.shiftLocation(0, -barSpeed);
        }

        if (keyListener.isDown()) {
            if (bar.getMaxY() + barSpeed < field.getMaxY())
                bar.shiftLocation(0, barSpeed);
            // else jiggle(move up 9px or something)
            // maybe jiggle but that would be hard(er)
        }
    }
}
