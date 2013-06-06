

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



/**
  * Date: 13-6-5
 * Time: 下午6:11
  */
public class TestConnection {
    public static void main(String[] strings)throws Exception{
        Socket socket = new Socket("localhost",12345);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
        out.flush();
        System.out.println("readline start");
        System.out.println(in.readLine());
        System.out.println("readline end");
        Thread.sleep(100000);
        socket.close();
    }
}
