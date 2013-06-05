
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
  * Date: 13-6-5
 * Time: 下午6:11
  */
public class TestConnection {
    public static void main(String[] strings)throws Exception{
        Socket socket = new Socket();
        socket.connect(InetAddress.getLocalHost(),12345);
        SocketAddress address
    }
}
