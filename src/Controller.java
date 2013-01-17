import java.util.Random;
import java.util.Vector;
import java.lang.Math;
import java.awt.Rectangle;
import java.awt.event.KeyListener;

public class Controller {

    Random randNr = new Random();
    int punkte = 0;
    KeyListener ListenForKeys = new ListenForKeys(); // ListenForKeys gets new instance
    Interface inter;
    GameSocket socket;
    Rectangle field;

    final double barSpeed = 20; // movementspeed of the bars
    boolean gameRunning = true; // set to false for pause
    double ballDelta[] = new double[2];
    double ballPosition[] = new double[2]; // [0] is x-delty [1] is y-delta
    double barPosition[] = new double[2]; // 0 is x and 1 is y
    int barHeight = 40;
    boolean host;
    double gameSpeed = 12;
    double allPositions[] = new double[3]; //contains ball-x ball-y and opponent-bar-y

    public static void main(String[] args) {
        new Controller();
    }

    public Controller() {
        // establish connection
        Connector c = new Connector();
        socket = c.connect();

        // initialize game interface
        inter = new Interface("NetPong 0.1 PRE-ALPHA");
        field = inter.getFieldBounds();
        inter.setVisible(true);
        startBall(gameSpeed);

        while (gameRunning) {
            if (host) {
                moveBars();
                checkCollision();
                moveBall();
                setPosArray();
                socket.writePositions(allPositions);
                inter.repaint();
            } else {
                getCoords();
                inter.repaint();
            }
        }
    }

    private boolean inRange(int index) {
        if (ballPosition[1] >= barPosition[index]
                && ballPosition[1] <= barPosition[index] + barHeight)
            return true;
        else
            return false;
    }

    private void getCoords() {
     ballPosition[0] = socket.getPositions()[0];
     ballPosition[1] = socket.getPositions()[1];
     barPosition[1] = socket.getPositions()[2];
    }

    private void setPosArray() {
        allPositions[0] = ballPosition[0];
        allPositions[1] = ballPosition[1];
        allPositions[2] = barPosition[1];
       }

    private void checkCollision() {
        int ballSize = (int) inter.ball.height;
        if (ballPosition[0] + ballSize + ballDelta[0] >= 760 && inRange(1)
                || ballPosition[0] + ballDelta[0] <= 40 && inRange(0)) {
            ballDelta[0] = ballDelta[0] * -1;
        }

        if (ballPosition[1] + ballSize + ballDelta[1] >= 600
                || ballPosition[1] + ballDelta[1] <= 0) {
            ballDelta[1] = ballDelta[1] * -1;
        }
    }

    private void moveBall() {
        ballPosition[0] = ballPosition[0] + ballDelta[0];
        ballPosition[1] = ballPosition[1] + ballDelta[1];
    }

    private void startBall(double maxSpeed) {
        ballDelta[0] = Math.random() * maxSpeed;
        ballDelta[1] = Math.random() * maxSpeed; // possibly add checks for right direction
    }

    private void moveBars() {
        if (up) {
            if (barPosition[1] - barSpeed > field.y) {
                barPosition[1] = barPosition[1] - barSpeed;
            }
        }

        if (down) {
            if (barPosition[1] + barSpeed < field.getMaxY()) {
                barPosition[1] = barPosition[1] + barSpeed;
            }
            // else jiggle(move up 9px or something)
            // maybe jiggle but that would be hard(er)

        }
    }
}
