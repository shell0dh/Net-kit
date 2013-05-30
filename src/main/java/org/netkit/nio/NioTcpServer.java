package org.netkit.nio;

/**
 * Date: 13-5-25
 * Time: 下午2:13
 */
public class NioTcpServer {
    private IoEventLoop[] eventLoops;
    private AcceptEventLoop acceptEventLoop;

    private int currentLoop = -1;

    private TcpConnectionSupport support;

    public NioTcpServer(int threads,TcpConnectionSupport connectionSupport,int port){
        this.support = connectionSupport;
        for(int i = 0 ; i < threads ; i++){
            eventLoops[i] = new IoEventLoop(connectionSupport);
        }
        acceptEventLoop = new AcceptEventLoop(port,connectionSupport,eventLoops);
    }

    public void start(){
        for(EventLoop p : eventLoops)
            p.startEventLoop();
        acceptEventLoop.start();
    }

    public void stopEventLoop(){
        acceptEventLoop.stopEventLoop();
        for(EventLoop p : eventLoops)
            p.stopEventLoop();
    }
}