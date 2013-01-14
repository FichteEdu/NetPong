import java.io.*;
import java.net.*;
import java.util.Random;
import java.nio.ByteBuffer;

public class GameSocket implements Runnable {

    private Socket socket;
    private ServerSocket listener;
    private String host;
    private int port = 1100;

    private OutputStream out;
    private InputStream in;

    private int lastPos = 0;
    private boolean hasCollided = false;
    private boolean isCancelled = false;
    static Random r = new Random();

    GameSocket() throws IOException {
        listener = new ServerSocket(this.port);
        socket = new Socket();

    }

    
    // for testing purposes only
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
            s.writePosition(a, true);
            System.out.println("Got: " + s.getPosition());
            Thread.sleep(100);
        }
    }


    // attempts to connect to the host
    // does not block until a connection is established
    public void connect(String host) {
        isCancelled = false;
        this.host = host;
        new Thread(this).start();
    }


    // returns true if the socket is connected
    public boolean isConnected() {
        return socket.isConnected();
    }


    // does the actual connecting
    public void run() {
        try {
            while(!isCancelled) {
                try {
                    socket.connect(new InetSocketAddress(this.host, this.port));
                } catch (ConnectException e) {
                    Thread.sleep(1000);
                    continue;
                }
                //waiting for accept
                Socket tmp = listener.accept();
                out = socket.getOutputStream();
                in = tmp.getInputStream();
            }
        } catch (Exception e) {

        }
    }



    // cancels the current connection attempt
    public void cancel() {
        isCancelled = true;
    }


    // writes the position of the player's bar and whether the ball has collided with it in the stream and flushes it
    public void writePosition(int pos, boolean hasCollided) {
        try {
            out.write(ByteBuffer.allocate(4).putInt(hasCollided ? pos  | Integer.MIN_VALUE : pos).array());
            out.flush();
        } catch (IOException e) {
            // will probably never happen
        }
    }


    // returns the current position of the other player's bar
    public int getPosition() {
        try {
            while(in.available() >= 4) {
                byte[] b = new byte[4];
                if(in.read(b) < 4)
                    return lastPos;
                ByteBuffer pos = ByteBuffer.wrap(b);
                // lastPos = pos.getInt() & 0xF;
                lastPos = pos.getInt();
                hasCollided = lastPos < 0;
                if(hasCollided)
                    lastPos &= 0xF;
            }
        } catch (IOException e) {
            return lastPos;
        }
        return lastPos;
    }


    // returns whether or not the ball has collided with the other player's bar
    public boolean hasCollided() {
        if(hasCollided) {
            hasCollided = false;
            return true;
        }
        return hasCollided;
    }


    // disconnects
    public void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {

        }
    }

}
