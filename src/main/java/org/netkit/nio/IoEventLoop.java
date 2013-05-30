package org.netkit.nio;


import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: yfliuyu
 * Date: 13-5-24
 * Time: 上午11:33
 */
public class IoEventLoop extends AbstractEventLoop {

    private Queue<NEvent> eventQ = new LinkedBlockingQueue<NEvent>();

    private Selector selector;

    private NConnectionSupport connectionSupport;

    public IoEventLoop(NConnectionSupport support){
        super(support);
    }

    public void init()throws Exception{
        this.selector = Selector.open();
    }


    @Override
    public void registerEvent(NEvent e) {
        eventQ.add(e);
    }

    public void run() {
        while (runing()) {
            final Selector sel = this.selector;
            try{
                int ready = sel.select(500);
                if (ready <= 0) continue;
                Iterator<SelectionKey> selkeys = sel.selectedKeys().iterator();
                try {
                    while(selkeys.hasNext()){
                        SelectionKey key = null;
                        key = selkeys.next();
                        selkeys.remove();
                        handlerEvent(key);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                processEvent(sel);
            }catch (ClosedChannelException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    private void handlerEvent(SelectionKey key){
        if(!key.isValid()){
            close(key);
        }
        if(key.isReadable()){

        }
        if(key.isWritable()){

        }
    }



    private void processEvent(final Selector selector) throws ClosedChannelException {
        while (!eventQ.isEmpty()) {
            NEvent e = eventQ.poll();
            SocketChannel channel = e.getConnection().channel();
            SelectionKey selKey = channel.register(selector,e.eventOps());
            selKey.attach(e.getConnection());
        }
    }
}
