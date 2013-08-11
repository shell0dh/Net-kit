package org.netkit.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
  * User: shell0dh
 * Date: 13-8-3
 * Time: 下午3:04
  */
public class NioTcpClient{
    private static final Logger LOG = LoggerFactory.getLogger(NioTcpClient.class);

    private IoSupport support;
    private NioEventLoop connectEventLoop;
    private NioSelectPool selectPool;

    public NioTcpClient(NioEventLoop c,NioSelectPool pool,IoSupport s){
        this.support = s;
        this.connectEventLoop = c;
        this.selectPool = pool;

    }

    public NioTcpClient(IoSupport s){
        this.support = s;
        this.connectEventLoop = new NioEventLoop("connectEventLoop",0);
        this.selectPool = new NioSelectPool("readWriteEventLoop",2);
    }

    public IoConnection connect(SocketAddress address)throws IOException{
        SocketChannel clientChanel = null;
        clientChanel = SocketChannel.open();
        clientChanel.configureBlocking(false);
        NioEventLoop readwriteEventLoop = selectPool.getNextLoop();
        final IoConnection connection = new IoConnection(clientChanel,support,readwriteEventLoop);
        final TimeTask worker = support.getIdleWorker();
        boolean isConnect = clientChanel.connect(address);
        LOG.info("isConnect:{}",isConnect);
        if(isConnect){
            IoEvent e = new IoEvent(SelectionKey.OP_READ,connection,clientChanel,new RegCallback<SelectionKey>() {
                @Override
                public void done(SelectionKey key) {
                    LOG.info("done Connect isConnect = true: {}",key);
                    connection.setSelectionKey(key);
                    worker.processReadIdle(connection,System.currentTimeMillis());
                    worker.processWriteIdle(connection,System.currentTimeMillis());
                }
            });
            readwriteEventLoop.register(e);
        }else{
            IoEvent e = new IoEvent(SelectionKey.OP_CONNECT,connection,clientChanel,new RegCallback<SelectionKey>() {
                @Override
                public void done(SelectionKey key) {
                    LOG.info("done Connect isConnect = false : {}",key);
                    connection.setSelectionKey(key);
                    worker.processReadIdle(connection,System.currentTimeMillis());
                    worker.processWriteIdle(connection,System.currentTimeMillis());                }
            });
            connectEventLoop.register(e);
            connection.processConnectionOpen();
        }
        return connection;
    }
}
