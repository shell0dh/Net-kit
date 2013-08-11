import org.netkit.nio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Date: 8/6/13
 * Time: 9:56 AM
 */
public class TcpServerTest {
    private static final Logger LOG = LoggerFactory.getLogger(TcpServerTest.class);

    public static void main(String[] string) throws Exception {
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
                return true;
            }


            @Override
            public Integer getTimeout() {
                return 1000;
            }

            @Override
            public void setTimeout(int timeOut) {
            }
        };

        IoSupport support = new IoSupport(new IoHandler() {
            @Override
            public void connctionOpen(IoConnection connection) {
                ByteBuffer message = ByteBuffer.wrap("hello world !\n".getBytes());
                LOG.info("open message = "+message.remaining());
                connection.write(message);
            }

            @Override
            public void messageReceived(IoConnection connection, Object message) {
                LOG.info("connection received message : "+new String(((ByteBuffer)message).array()));
                connection.write(message);
            }

            @Override
            public void exceptionCaught(IoConnection connection, Exception e) {
                LOG.error("connection Exception : "+e.getMessage());
            }

            @Override
            public void connectionIdle(IoConnection connection) {
                LOG.info("Idle : {}",System.currentTimeMillis());
            }
        }, null,config,new TimeTask());
        final NioTcpServer server = new NioTcpServer(support);
        server.bind(12345);
    }
}
