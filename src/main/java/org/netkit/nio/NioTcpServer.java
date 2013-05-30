package org.netkit.nio;

/**
 * Date: 13-5-25
 * Time: 下午2:13
 */
public class NioTcpServer {
    private IoEventLoop[] eventLoops;
    private AcceptEventLoop acceptEventLoop;

    private int currentLoop = -1;

    private EventLoopListener listener;

    public NioTcpServer(int threads,EventLoopListener listener,int port){
        this.listener = listener;
        for(int i = 0 ; i < threads ; i++){
            eventLoops[i] = new IoEventLoop(listener);
        }
        acceptEventLoop = new AcceptEventLoop(port,listener,eventLoops);
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