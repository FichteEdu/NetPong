//taken from http://code.google.com/p/pongjava/source/browse/trunk/pongjava/Pong/src/Keylistener.java?r=5

/* basically check for events and then set up/down/space to true */
//space might be used to start the game
// yes, it is mostly copy paste but still, why not?

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class Keylistener implements KeyListener {
  
  boolean up = false;
  boolean down = false;
  boolean space = false;
  
  @Override
  public void keyPressed(KeyEvent e) {
    // TODO Auto-generated method stub
    
    if(e.getKeyCode() == KeyEvent.VK_UP)
    up = true;
    
    if(e.getKeyCode() == KeyEvent.VK_DOWN)
    down = true;
    
    if(e.getKeyCode() == KeyEvent.VK_SPACE)
    space = true;
    
  }
  
  @Override
  public void keyReleased(KeyEvent e) {
    // TODO Auto-generated method stub
    
    if(e.getKeyCode() == KeyEvent.VK_UP)
    up = false;
    
    if(e.getKeyCode() == KeyEvent.VK_DOWN)
    down = false;
    
    if(e.getKeyCode() == KeyEvent.VK_SPACE)
    space = false;
    
  }
  
  @Override
  public void keyTyped(KeyEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  
  
  public boolean isUp() {
    return up;
  }
  
  public void setUp(boolean up) {
    this.up = up;
  }
  
  public boolean isDown() {
    return down;
  }
  
  public void setDown(boolean down) {
    this.down = down;
  }
  
  public boolean isSpace()
  {
    return this.space;
  }
  
}