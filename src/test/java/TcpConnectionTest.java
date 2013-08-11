

import org.netkit.nio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;


/**
  * Date: 13-6-5
 * Time: 下午6:11
  */
public class TcpConnectionTest {
    private static final Logger LOG = LoggerFactory.getLogger(TcpConnectionTest.class);

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
                //connection.write(message);
            }

            @Override
            public void exceptionCaught(IoConnection connection, Exception e) {
                LOG.error(e.getMessage());
            }

            @Override
            public void connectionIdle(IoConnection connection) {
                LOG.info("idle : {}",System.currentTimeMillis());
                ByteBuffer me = ByteBuffer.allocate(1);
                me.putInt(1);
                try{
                    connection.write(me);
                }catch (Exception e){
                    e.printStackTrace();
                    connection.processException(e);
                }
            }
        },null,config,new TimeTask());
        NioTcpClient client = new NioTcpClient(support);
        SocketAddress address = new InetSocketAddress(12345);
        client.connect(address);
    }
}
