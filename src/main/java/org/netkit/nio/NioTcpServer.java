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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * Date: 13-5-25
 * Time: 下午2:13
 */
public class NioTcpServer implements NioEventListener{

    private static final Logger LOG = LoggerFactory.getLogger(NioTcpServer.class);

    private NioSelectPool selectPool;
    private NioEventLoop acceptEventLoop;
    private ServerSocketChannel serverSocketChannel;

    private IoSupport support;

    private CountDownLatch serverAwait = new CountDownLatch(1);

    public NioTcpServer(IoSupport s){
        this.support = s;
        this.acceptEventLoop = new NioEventLoop("AcceptEventLoop",0);
        this.selectPool = new NioSelectPool("IoEventLoop");
    }

    public void start()throws Exception{
        if(LOG.isInfoEnabled())
            LOG.info("starting NioTcpServer....");
        serverAwait.await();
    }

    public void stop(){
        if(LOG.isInfoEnabled())
            LOG.info("stoping NioTcpServer....");
        serverAwait.countDown();
    }

    public void bind(int port)throws Exception{
        if(LOG.isInfoEnabled())
            LOG.info("bind server "+serverSocketChannel.getLocalAddress()+":"+port);
        serverSocketChannel = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress(port);
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.configureBlocking(false);
        acceptEventLoop.register(new IoEvent(SelectionKey.OP_ACCEPT, this, serverSocketChannel, null));
    }

    private void accept()throws IOException{
        SocketChannel socketChannel = serverSocketChannel.accept();
        NioEventLoop eventLoop = selectPool.getNextLoop();
        IoEvent event = new IoEvent(SelectionKey.OP_READ,buildIoConnection(socketChannel,eventLoop),socketChannel,null);
        eventLoop.register(event);
    }

    private IoConnection buildIoConnection(SocketChannel socketChannel,NioEventLoop e)throws IOException{
        return new IoConnection(socketChannel,support,e);
    }

    @Override
    public void ioReady(boolean igRead, boolean igWrite, boolean isAccept, boolean igConnect,ByteBuffer ig) throws IOException {
        if(isAccept){
            accept();
        }
    }

    public static void main(String[] string)throws Exception{
        LOG.info("Test runing.");
        IoSupport support = new IoSupport(new IoHandler() {
        }, Executors.newCachedThreadPool());
        final NioTcpServer server = new NioTcpServer(support);
        server.bind(12345);
        System.out.println("server..start");
        new Thread(){
            @Override
            public void run() {
                try {
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Thread.sleep(10000);
        server.stop();
    }
}