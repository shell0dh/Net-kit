package org.netkit.nio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: shell0dh
 * Date: 13-5-24
 * Time: 上午11:33
 */
public class NioEventLoop {

    private static final Logger LOG = LoggerFactory.getLogger(NioEventLoop.class);

    private Queue<IoEvent> queue = new ConcurrentLinkedQueue<IoEvent>();

    private Selector selector;

    private ByteBuffer bufferCache = ByteBuffer.allocate(64 * 1024);

    private IoWorker worker;

    private volatile boolean runing = true;


    public NioEventLoop(String name, int index){
        try{
            this.selector = Selector.open();
        }catch (IOException e){
            LOG.error("can't open selector."+e.getMessage());
        }
        worker = new IoWorker(name, index);
        worker.start();
    }

    public void shutdown() {
        runing = false;
        worker.interrupt();
    }


    public class IoWorker extends Thread {

        public IoWorker(String prefix, int index) {
            super(prefix + "-" + index);
        }

        @Override
        public void run() {
            final Selector sel = NioEventLoop.this.selector;
            if (LOG.isDebugEnabled())
                LOG.debug(getName() + " thread runing......");
            while (runing) {//fixme: I have good a idea. set thread daemon true and change "while(runing)" to "for(;;)"
                try {
                    int ready = sel.select(500);
                    if(LOG.isDebugEnabled()){
                        LOG.debug("ready keys:"+ready);
                    }
                    if (ready > 0) {
                        final Iterator<SelectionKey> selkeys = sel.selectedKeys().iterator();
                        while (selkeys.hasNext()) {
                            SelectionKey key = selkeys.next();
                            Object att = key.attachment();
                            if(att == null){
                                LOG.error("key is not attachment.");
                            }else{
                                NioEventListener listener = (NioEventListener)att;
                                listener.ioReady(key.isReadable(),key.isWritable(),key.isAcceptable(),key.isConnectable(),bufferCache);
                            }
                            selkeys.remove();
                        }
                    }
                    processEvent(sel);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }//todo:add root Execption
            }
            if (LOG.isDebugEnabled())
                LOG.debug(getName() + " thread stop......");
        }
    }

    public void wakeup() {
        selector.wakeup();
    }


    public void processEvent(Selector sel) throws ClosedChannelException {
        while (!queue.isEmpty()) {
            IoEvent e = queue.poll();
            SelectionKey key = e.ch().register(sel, e.Ops(), e.getListener());
            if(e.cb() != null){
                e.cb().done(key);
            }
        }
    }



    public void register(IoEvent e) {
        queue.add(e);
/*        int ops = 0;

        if (accept) {
            ops |= SelectionKey.OP_ACCEPT;
        }

        if (connect) {
            ops |= SelectionKey.OP_CONNECT;
        }

        if (read) {
            ops |= SelectionKey.OP_READ;
        }

        if (write) {
            ops |= SelectionKey.OP_WRITE;
        }
        */
        wakeup();
    }

    public void unregister(final NioEventListener listener,final SelectableChannel ch){
        //LOG.info("unregister listener : "+listener);
        final SelectionKey key = ch.keyFor(selector);
        key.cancel();
        key.attach(null);
    }



}
