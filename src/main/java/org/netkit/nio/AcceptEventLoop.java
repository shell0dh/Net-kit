package org.netkit.nio;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
    private int port;

    private int currentLoop = -1;

    public AcceptEventLoop(int port,TcpConnectionSupport support,IoEventLoop[] loops){
        super(support);
        this.eventLoops = loops;
    }

    public void initEventLoop()throws Exception{
        this.selector = Selector.open();
        ServerSocketChannel serverSocketChannel;
        serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), port);
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }


    private void accept(SelectionKey acceptkey,int process) {
        final Selector s = this.selector;
        try {
            Socket ssocket = ((ServerSocketChannel) acceptkey.channel()).accept().socket();
            SocketChannel socketChannel = ssocket.getChannel();
            IoEventLoop e = eventLoops[process];
            //todo;;regitster event Accept
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        final Selector sel = this.selector;
        int nKeys = 0;
        SelectionKey selkey = null;
        while (runing()) {
            try {
                nKeys = this.selector.select();
                if (nKeys > 0) {
                    Set selectedKeys = sel.selectedKeys();
                    Iterator<SelectionKey> selKeys = selectedKeys.iterator();
                    while (selKeys.hasNext()) {
                        selkey = selKeys.next();
                        selKeys.remove();
                        if (selkey.isAcceptable()) {
                            currentLoop = (currentLoop+1)% eventLoops.length;
                            accept(selkey,currentLoop);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
