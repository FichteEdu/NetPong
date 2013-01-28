// taken from
// http://code.google.com/p/pongjava/source/browse/trunk/pongjava/Pong/src/Keylistener.java?r=5

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ListenForKeys implements KeyListener {

    boolean up = false;
    boolean down = false;
    boolean space = false;

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP)
            up = true;

        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            down = true;

        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            space = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP)
            up = false;

        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            down = false;

        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            space = false;
    }

    @Override
    public void keyTyped(KeyEvent arg0) { }

    public boolean isUp() {
        return up;
    }
    public boolean isDown() {
        return down;
    }

    public boolean isSpace() {
        return this.space;
    }
}