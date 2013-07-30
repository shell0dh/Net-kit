package org.netkit.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private IoEventLoop[] eventLoops;
    private AcceptEventLoop acceptEventLoop;
    private ServerSocketChannel serverSocketChannel;
    private int currentLoop = -1;

    private IoSupport support;

    private CountDownLatch serverAwait = new CountDownLatch(1);

    public NioTcpServer(int threads,IoSupport connectionSupport){
        this.support = connectionSupport;
        this.eventLoops = new IoEventLoop[threads];
        for(int i = 0 ; i < threads ; i++){
            eventLoops[i] = new IoEventLoop(connectionSupport);
            try {
                eventLoops[i].initEventLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        acceptEventLoop = new AcceptEventLoop(port,connectionSupport);
        try {
            acceptEventLoop.initEventLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start()throws Exception{
        System.out.println("startEventloop");
        for(EventLoop p : eventLoops)
            p.startEventLoop();
        acceptEventLoop.startEventLoop();
        serverAwait.await();
    }

    public void stopEventLoop(){
        System.out.println("stopEventLoop....");
        acceptEventLoop.stopEventLoop();
        for(EventLoop p : eventLoops)
            p.stopEventLoop();
        serverAwait.countDown();
    }

    public void bind(int port)throws Exception{
        System.out.println("bind");
        serverSocketChannel = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress(port);
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void accept(){
        currentLoop = (currentLoop+1)% eventLoops.length;
        IoEventLoop e = eventLoops[process];
        NEvent event = new NEvent(SelectionKey.OP_READ,createConnection(serverSocketChannel.accept(),e));
        e.registerEvent(event);
    }

    private NServerConnection createConnection(SocketChannel socketChannel,IoEventLoop eventLoop) throws Exception{
        return new NServerConnection(socketChannel,eventLoop,support);
    }


    @Override
    public void ioNotify(boolean read, boolean write,boolean accept,boolean connect,IoEventLoop e) throws Exception {
        if(accept){
            accept();
        }
    }

    public static void main(String[] string)throws Exception{
        IoSupport support = new IoSupport();
        int threads = Runtime.getRuntime().availableProcessors();
        final NioTcpServer server = new NioTcpServer(threads,support,12345);
        Thread serverThread = new Thread(){
            @Override
            public void run() {
                try {
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        serverThread.start();
        Thread.sleep(100000);
        server.stopEventLoop();
    }
}