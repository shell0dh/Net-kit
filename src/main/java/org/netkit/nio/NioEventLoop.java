package org.netkit.nio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
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

    private IoWorker worker;


    public NioEventLoop(String name, int index) throws IOException {
        this.selector = Selector.open();
        worker = new IoWorker(name, index);
        worker.start();
    }

    public void shutdown() {
        worker.workerStop();
        worker.interrupt();
    }


    public class IoWorker extends Thread {
        private volatile boolean runing = true;

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
                    if (ready > 0) {
                        Iterator<SelectionKey> selkeys = sel.selectedKeys().iterator();
                        while (selkeys.hasNext()) {
                            SelectionKey key = null;
                            key = selkeys.next();
                            selkeys.remove();
                            Object att = key.attachment();
                            if(att == null){
                                LOG.error("key is not attachment.");
                            }else{
                                NioEventListener listener = (NioEventListener)att;
                                listener.ioReady(key.isReadable(),key.isWritable(),key.isAcceptable(),key.isConnectable());
                            }
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

        public void workerStop() {
            runing = false;
        }
    }

    public void wakeup() {
        selector.wakeup();
    }


    public void processEvent(Selector sel) throws ClosedChannelException {
        while (queue.peek() != null) {
            IoEvent e = queue.poll();
            e.ch().register(sel, e.Ops(), e.getListener());
            if(e.cb() != null){
                //todo;callback  handler
            }
        }
    }

    public void registerEvent(IoEvent e) {
        queue.add(e);
        wakeup();
    }


}