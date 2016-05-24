import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Lehyu on 2016/5/23.
 */
public class Server {
    public static void main(String[] args) {
        try {
            final ServerSocket server = new ServerSocket(NetConfig.PORT);
            Executor service = Executors.newCachedThreadPool();
            while (true) {
                Socket client = server.accept();
                Date date = new Date();
                System.out.println("client connected:" + date.toString());
                service.execute(new ServerThread(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
