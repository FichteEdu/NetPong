import java.io.*;
import java.net.*;
import java.util.Random;

public class GameSocket implements Runnable {

    private Socket socket;
    private ServerSocket listener;
    private String host;
    private int port = 1100;

    private PrintWriter out;
    private BufferedReader in;

    private short lastPos = 0;
    static Random r = new Random();

    GameSocket() throws IOException {
        listener = new ServerSocket(this.port);
        socket = new Socket();

    }

    
    public static void main(String[] args) throws IOException, InterruptedException {
        GameSocket s = new GameSocket();
        s.connect("localhost");
        while(!s.isConnected()) {
            System.out.println("Waiting for connection");
            Thread.sleep(100);
        }
        while(true) {
            int a = r.nextInt(8);
            System.out.println("Writing: " + a);
            s.writePosition((short)a);
            System.out.println("Got: " + s.getPosition());
            Thread.sleep(100);
        }
    }


    
    public void connect(String host) throws IOException {
        this.host = host;
        new Thread(this).start();
    }


    public boolean isConnected() {
        return socket.isConnected();
    }


    public void run() {
        try {
            while(true) {
                try {
                    socket.connect(new InetSocketAddress(this.host, this.port));
                } catch (ConnectException e) {
                    Thread.sleep(1000);
                    continue;
                }
                
                System.out.println("Timeout: " + socket.getSoTimeout());
                //waiting for accept
                Socket tmp = listener.accept();
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(tmp.getInputStream())); 
            }
        } catch (Exception e) {

        }
    }


    public void writePosition(short pos) {
        out.write(pos);
        out.flush();
    }

    public short getPosition() throws IOException {
        if (in.ready()) {
            short pos = (short)in.read();
            if (pos == -1) return lastPos;
            return (lastPos = pos);
        } else {
            return lastPos;
        }
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

}
