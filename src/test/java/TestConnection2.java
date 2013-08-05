

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
  * Date: 13-6-5
 * Time: 下午6:11
  */
public class TestConnection2 {
    private static final Logger LOG = LoggerFactory.getLogger(TestConnection2.class);

    public static void main(String[] strings)throws Exception{
        Socket socket = new Socket("localhost",12345);
        LOG.info("create socket connection done.");
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println(in.read());
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println("zzzzkill");
        out.flush();
        System.out.println("z"+in.readLine());
        out.println("zzzzkill");
        out.flush();
        System.out.println("z"+in.readLine());
        out.println("zzzzkill");
        out.flush();
        System.out.println("z"+in.readLine());
        socket.close();
    }
}
