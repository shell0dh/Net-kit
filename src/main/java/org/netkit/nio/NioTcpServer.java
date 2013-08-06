package org.netkit.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


/**
 * Date: 13-5-25
 * Time: 下午2:13
 */
public class NioTcpServer implements TcpServer, NioEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(NioTcpServer.class);


    private NioSelectPool selectPool;
    private NioEventLoop acceptEventLoop;
    private ServerSocketChannel serverSocketChannel;

    private IoSupport support;

    public NioTcpServer(IoSupport s) {
        this.support = s;
        this.acceptEventLoop = new NioEventLoop("AcceptEventLoop", 0);
        this.selectPool = new NioSelectPool("IoEventLoop");
    }

    public void bind(int port) throws IOException{
        serverSocketChannel = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress(port);
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.configureBlocking(false);
        acceptEventLoop.register(new IoEvent(SelectionKey.OP_ACCEPT, this, serverSocketChannel, null));
        if (LOG.isInfoEnabled())
            LOG.info("bind server " + serverSocketChannel.getLocalAddress() + ":" + port);
    }

    private void accept() throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        NioEventLoop eventLoop = selectPool.getNextLoop();
        final IoConnection connection = buildIoConnection(socketChannel,eventLoop);
        IoEvent event = new IoEvent(SelectionKey.OP_READ,connection, socketChannel,new RegCallback<SelectionKey>() {
            @Override
            public void done(SelectionKey key) {
                connection.setSelectionKey(key);
                connection.processConnectionOpen();
            }
        });
        eventLoop.register(event);
    }

    private IoConnection buildIoConnection(SocketChannel socketChannel, NioEventLoop e) throws IOException {
        return new IoConnection(socketChannel, support, e);
    }

    @Override
    public void ioReady(boolean igRead, boolean igWrite, boolean isAccept, boolean igConnect, ByteBuffer ig) throws IOException {
        if (isAccept) {
            accept();
        }
    }


}