

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
public class TestConnection {
    private static final Logger LOG = LoggerFactory.getLogger(TestConnection.class);

    public static void main(String[] strings)throws Exception{
        LOG.info("zze");
        List<PrintWriter> outs = new ArrayList<PrintWriter>();
        List<BufferedReader> ins = new ArrayList<BufferedReader>();
        for(int i = 0 ;i < 1000; i++){
            Socket socket = new Socket("localhost",12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ins.add(in);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            outs.add(out);
        }
        int w = 0;
        for(;;){
            w++;
            for(PrintWriter p : outs){
                p.write(w);
                p.flush();
            }

            for(BufferedReader b: ins){
                System.out.println(b.readLine());
            }
        }
    }
}
