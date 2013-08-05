

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
  * Date: 13-6-5
 * Time: 下午6:11
  */
public class TestConnection {
    private static final Logger LOG = LoggerFactory.getLogger(TestConnection.class);

    public static void main(String[] strings)throws Exception{
        final List<PrintWriter> outs = new ArrayList<PrintWriter>();
        final List<BufferedReader> ins = new ArrayList<BufferedReader>();
        List<Socket> sockets = new ArrayList<Socket>();
        for(int i = 0 ;i < 10; i++){
            Socket socket = new Socket("localhost",12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ins.add(in);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            outs.add(out);
            sockets.add(socket);
        }

        new Thread(){
            @Override
            public void run() {
                for(PrintWriter p : outs){
                    p.println("client send");
                    p.flush();
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                while (true)
                for(BufferedReader b : ins){
                    try {
                        System.out.println("server send :"+b.readLine());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        LOG.info("create socket connection done.");
    }
}
