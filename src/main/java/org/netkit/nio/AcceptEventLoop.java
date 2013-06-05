package org.netkit.nio;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * User: shell0dh
 * Date: 13-5-21
 * Time: 下午4:40
 */
public class AcceptEventLoop extends AbstractEventLoop {

    private Selector selector;
    private IoEventLoop[] eventLoops;
    private ServerSocketChannel serverSocketChannel;
    private int port;

    private int currentLoop = -1;

    public AcceptEventLoop(int p,NConnectionSupport support,IoEventLoop[] loops){
        super(support);
        this.port = p;
        this.eventLoops = loops;
    }

    public void initEventLoop()throws Exception{
        this.selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress(port);
        System.out.println("bind");
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("initEventLoop end");
    }


    private void accept(int process) {
        final Selector sel = this.selector;
        try {
            IoEventLoop e = eventLoops[process];
            NEvent event = new NEvent(SelectionKey.OP_READ,createConnection(serverSocketChannel.accept(),e));
            e.registerEvent(event);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    public NServerConnection createConnection(SocketChannel socketChannel,IoEventLoop e){
        return new NServerConnection(socketChannel,e,getConnectionSupport());
    }

    @Override
    public void run() {
        System.out.println("begin acceptEvent listener");
        final Selector sel = this.selector;
        int nKeys = 0;
        SelectionKey selkey = null;
        while (runing()) {
            try {
                nKeys = sel.select(500);
                System.out.println("nkeys = "+nKeys);
                if (nKeys > 0) {
                    Set selectedKeys = sel.selectedKeys();
                    Iterator<SelectionKey> selKeys = selectedKeys.iterator();
                    while (selKeys.hasNext()) {
                        selkey = selKeys.next();
                        selKeys.remove();
                        if (selkey.isAcceptable()) {
                            currentLoop = (currentLoop+1)% eventLoops.length;
                            accept(currentLoop);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
