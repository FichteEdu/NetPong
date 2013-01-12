import java.io.*;
import java.net.*;
import java.util.Random;

public class GameSocket {

    private Socket socket;
    private ServerSocket listener;
    private String host;
    private int port = 11000;

    private PrintWriter out;
    private BufferedReader in;

    private short lastPos = 0;
    static Random r = new Random();

    GameSocket(String host) {
        this.host = host;
    }

    // TODO: wtfisthisidonteven
    // 
    public void connect() throws IOException {
        listener = new ServerSocket(this.port);
        socket = new Socket(this.host, this.port);
        //waiting for accept
        Socket tmp = listener.accept();
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(tmp.getInputStream()));
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