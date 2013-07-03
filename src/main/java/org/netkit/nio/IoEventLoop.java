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

    public void initEventLoop()throws Exception{
        this.selector = Selector.open();
    }


    @Override
    public void registerEvent(NEvent e) {
        eventQ.add(e);
    }

    @Override
    public void unregister(SocketChannel socketChannel) {
        final Selector sel = this.selector;
        SelectionKey key = socketChannel.keyFor(sel);
        close(key);
    }

    public void run() {
        final Selector sel = this.selector;
        System.out.println("runing.ioEventLoop");
        while (runing()) {
            try{
                processEvent(sel);
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
            }catch (ClosedChannelException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    private void handlerEvent(SelectionKey key){
        if(!key.isValid()){
            SocketChannel s = channelFor(key);
            close(key);
        }
        NServerConnection connection = (NServerConnection)key.attachment();
        connection.ioNotify(key.isReadable(),key.isWritable(),this);
    }


    private void processEvent(final Selector selector) throws ClosedChannelException {
        while (!eventQ.isEmpty()) {
            NEvent<NServerConnection> e = eventQ.poll();
            SocketChannel channel = e.getAttachment().channel();
            SelectionKey selectionKey = channel.register(selector,e.eventOps(),e.getAttachment());
            e.getAttachment().setSelectionKey(selectionKey);
        }
    }
}
