package org.netkit.nio;

import java.util.concurrent.CountDownLatch;

/**
 * Date: 13-5-25
 * Time: 下午2:13
 */
public class NioTcpServer {
    private IoEventLoop[] eventLoops;
    private AcceptEventLoop acceptEventLoop;

    private NConnectionSupport support;

    private CountDownLatch serverAwait = new CountDownLatch(1);

    public NioTcpServer(int threads,NConnectionSupport connectionSupport,int port){
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
        acceptEventLoop = new AcceptEventLoop(port,connectionSupport,eventLoops);
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

    public static void main(String[] string)throws Exception{
        NConnectionSupport support = new NConnectionSupport();
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