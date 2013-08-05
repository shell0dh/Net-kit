

import org.netkit.nio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;


/**
  * Date: 13-6-5
 * Time: 下午6:11
  */
public class TestConnection2 {
    private static final Logger LOG = LoggerFactory.getLogger(TestConnection2.class);

    public static void main(String[] strings)throws Exception{
        IoConfig config = new IoConfig() {
            @Override
            public Integer getReadBufferSize() {
                return null;
            }

            @Override
            public void setReadBufferSize(Integer size) {

            }

            @Override
            public void setReuseAddress(boolean reuseAddress) {

            }

            @Override
            public Boolean isReuseAddress() {
                return null;
            }

            @Override
            public Integer getTimeout() {
                return null;
            }

            @Override
            public void setTimeout(int timeOut) {

            }
        };
        IoSupport support = new IoSupport(new IoHandler() {
            @Override
            public void connctionOpen(IoConnection connection) {
                LOG.info("client connectOpen..");
            }

            @Override
            public void messageReceived(IoConnection connection, Object message) {
                LOG.info(message.toString());
            }

            @Override
            public void exceptionCaught(IoConnection connection, Exception e) {
                LOG.error(e.getMessage());
            }
        },null,config);
        NioTcpClient client = new NioTcpClient(support);
        SocketAddress address = new InetSocketAddress(12345);
        client.connect(address);
    }
}
