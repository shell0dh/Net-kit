package org.netkit.nio;

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
        boolean isConnect = clientChanel.connect(address);
        if(isConnect){
            IoEvent e = new IoEvent(SelectionKey.OP_READ,connection,clientChanel,new RegCallback<SelectionKey>() {
                @Override
                public void done(SelectionKey key) {
                    connection.setSelectionKey(key);
                }
            });

            readwriteEventLoop.register(e);
        }else{
            IoEvent e = new IoEvent(SelectionKey.OP_CONNECT,connection,clientChanel,new RegCallback<SelectionKey>() {
                @Override
                public void done(SelectionKey key) {
                    connection.setSelectionKey(key);
                }
            });
            connectEventLoop.register(e);
            connection.processConnectionOpen();
        }
        return connection;
    }
}
