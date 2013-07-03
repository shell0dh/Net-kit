package org.netkit.nio;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * User: shell0dh
 * Date: 13-5-21
 * Time: 下午4:40
 */
public class AcceptEventLoop extends AbstractEventLoop<ServerSocketChannel> {

    private Selector selector;
    private IoEventListener ioAcceptListener;
    private ServerSocketChannel serverSocketChannel;
    private int port;

    private int currentLoop = -1;

    public AcceptEventLoop(int p,NConnectionSupport support){
        super(support);
        this.port = p;
    }

    public void initEventLoop()throws Exception{
        this.selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
    }

    @Override
    public void registerEvent(NEvent<ServerSocketChannel> e) {

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
                if (nKeys > 0) {
                    Set selectedKeys = sel.selectedKeys();
                    Iterator<SelectionKey> selKeys = selectedKeys.iterator();
                    while (selKeys.hasNext()) {
                        selkey = selKeys.next();
                        selKeys.remove();
                        if (selkey.isAcceptable()) {
                            ioAcceptListener.ioNotify(false,false,true,false,null);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
