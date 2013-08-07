package org.netkit.nio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * Date: 13-5-30
 * Time: 下午4:12
 */
public class IoConnection implements NioEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(NioTcpServer.class);


    private SocketChannel socketChannel;

    private NioEventLoop eventLoop;

    private SelectionKey selkey;

    private AttributeMap attributes = new AttributeMap();

    private Executor executor;

    private IoHandler handler;

    private Queue<ByteBuffer> writeQueue = new LinkedList<ByteBuffer>();

    public IoConnection(SocketChannel c, IoSupport s, NioEventLoop e) throws IOException {
        this.socketChannel = c;
        this.eventLoop = e;
        this.handler = s.getHandler();
        this.executor = s.getExecutor();
        this.socketChannel.configureBlocking(false);
    }

    public void setSelectionKey(SelectionKey key) {
        this.selkey = key;
    }

    public SocketChannel channel() {
        return socketChannel;
    }

    public void processDirectWrite(ByteBuffer writeBuffer) {
        try {
            int size = socketChannel.write(writeBuffer);
//            LOG.info("write size:"+size);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
            processException(e);
        }
    }

    public void processWrite() throws IOException {
        while (!writeQueue.isEmpty()) {
            ByteBuffer t = writeQueue.poll();
            t.flip();
            socketChannel.write(t);
        }
    }

    public void close() {
        //LOG.info("close Connection");
        eventLoop.unregister(this, socketChannel);
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            processException(e);
        }
    }

    public void messageReceived(ByteBuffer readbuffer) {
//        LOG.debug("messageReceived readbuffer :"+readbuffer);
        handler.messageReceived(this, readbuffer);
    }


    public void processException(Exception t) {
//        LOG.debug("processException:"+t.getMessage());
        handler.exceptionCaught(this, t);
    }

    public void processConnectionOpen() {
//        LOG.info("processConnectionOpen....");
        handler.connctionOpen(this);
    }

    public void processRead(final ByteBuffer tbuf) {
        try {
            final int readByteCount = socketChannel.read(tbuf);
            if (readByteCount > 0) {
//                LOG.info("read = "+tbuf.remaining());
                tbuf.flip();
//                LOG.info("read1 = "+tbuf.remaining());
                messageReceived(tbuf);
                tbuf.clear();
            } else if (readByteCount < 0) {
                // LOG.info("process read close channel.");
                close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            processException(e);
        }
    }


    @Override
    public void ioReady(boolean isReadable, boolean isWriteable, boolean ig, boolean connect, final ByteBuffer byteBuffer) {
        if (LOG.isInfoEnabled()) {
            // LOG.info("{ isReadable="+isReadable+",isWriteable="+isWriteable+",isAcceptable="+ig+",connect="+connect+",readBuffer="+byteBuffer+"}");
        }
        try {
            if (isReadable) {
                processRead(byteBuffer);
            }
            if (isWriteable) {
                processWrite();
            }
            if (connect) {
                boolean isConnect = socketChannel.finishConnect();
                if (!isConnect) {
                    processException(new Exception("not connect.."));
                } else {
                    selkey.cancel();
                    selkey.attach(null);
                    eventLoop.register(new IoEvent(SelectionKey.OP_READ, this, socketChannel, new RegCallback<SelectionKey>() {
                        @Override
                        public void done(SelectionKey key) {
                            setSelectionKey(key);
                            processConnectionOpen();
                        }
                    }));
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            processException(e);
        }
    }


    public void write(Object message) {
        LOG.info("write : " + message);
        processDirectWrite((ByteBuffer) message);
    }


}
