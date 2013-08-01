package org.netkit.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

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

    private int port;

    private CountDownLatch serverAwait = new CountDownLatch(1);

    public NioTcpServer(int threads,IoSupport s,int p){
        this.port = p;
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
        acceptEventLoop.registerEvent(new IoEvent(SelectionKey.OP_ACCEPT,this,serverSocketChannel,null));
    }

    private void accept()throws IOException{
        SocketChannel socketChannel = serverSocketChannel.accept();
        IoEvent event = new IoEvent(SelectionKey.OP_READ,buildIoConnection(socketChannel),socketChannel,null);
        selectPool.getNextLoop().registerEvent(event);
    }

    private IoConnection buildIoConnection(SocketChannel socketChannel){
        return new IoConnection(socketChannel,support);
    }

    @Override
    public void ioReady(boolean igRead, boolean igWrite, boolean isAccept, boolean igConnect) throws IOException {
        if(isAccept){
            accept();
        }
    }

    public static void main(String[] string)throws Exception{
        IoSupport support = new IoSupport();
        int threads = Runtime.getRuntime().availableProcessors();
        final NioTcpServer server = new NioTcpServer(threads,support,12345);
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
        server.stop();
    }
}