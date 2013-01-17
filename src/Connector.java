import java.awt.*;
import java.awt.event.*;
import java.io.IOException; 


/**
  *
  * Beschreibung
  *
  * @version 1.0 vom 17.01.2013
  * @author 
  */

public class Connector extends Frame {
  private boolean connecting = false;
  private GameSocket socket;
  // Anfang Attribute
  private TextField textField1 = new TextField();
  private Button button1 = new Button();
  private Label label1 = new Label();
  private Button button2 = new Button();
  // Ende Attribute
  
  public Connector(String title) { 
    // Frame-Initialisierung
    super(title);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) { dispose(); }
    });
    int frameWidth = 347; 
    int frameHeight = 92;
    setSize(frameWidth, frameHeight);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (d.width - getSize().width) / 2;
    int y = (d.height - getSize().height) / 2;
    setLocation(x, y);
    setResizable(false);
    Panel cp = new Panel(null);
    add(cp);
    // Anfang Komponenten
    
    textField1.setBounds(8, 8, 169, 25);
    textField1.setText("127.0.0.1");
    cp.add(textField1);
    button1.setBounds(184, 8, 73, 25);
    button1.setLabel("Connect");
    button1.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        button1_ActionPerformed(evt);
      }
    });
    cp.add(button1);
    label1.setBounds(8, 40, 323, 25);
    label1.setText("");
    cp.add(label1);
    button2.setBounds(272, 8, 57, 25);
    button2.setLabel("Host");
    button2.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        button2_ActionPerformed(evt);
      }
    });
    cp.add(button2);
    // Ende Komponenten
    
    setVisible(true);
  }
  
  // Anfang Methoden
  public void button1_ActionPerformed(ActionEvent evt) {
    if(connecting) {
      if(socket != null) {
        socket.cancel();
        textField1.setEnabled(true);
        button2.setEnabled(true);
      }
      button1.setLabel("Connect");
    } else {
      socket.connect(textField1.getText(), false);
      button1.setLabel("Abort");
      textField1.setEnabled(false);
      label1.setText("Trying to connect to " + textField1.getText());
      button2.setEnabled(false);
    }
    connecting = !connecting;
    
  }
  
  public void button2_ActionPerformed(ActionEvent evt) {
    socket.connect(textField1.getText(), true);
    button1.setLabel("Abort");
    textField1.setEnabled(false);
    button2.setEnabled(false);
    label1.setText("Waiting for opponent");
    connecting = true;
  }
  
  // Ende Methoden
  
  public static void main(String[] args) {
    Connector c = new Connector("Connector");
    GameSocket s = c.connect();
    System.out.println(((Object)s).getClass());
    System.out.println("LOLOLOL");
    c.dispose();
  }
  
  public GameSocket connect() {
    try {
      socket = new GameSocket();
      System.out.println(socket.isConnected());
      while(!socket.isConnected()) {
        Thread.sleep(100);
      }
      return socket;
    } catch (Exception e) {
      System.out.println(e.toString());
      return null;
    }
  }
  
  public void dispose() {
    super.dispose();
    System.exit(0);
  }
}
