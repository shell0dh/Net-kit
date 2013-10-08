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
import java.util.concurrent.atomic.AtomicBoolean;

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

    private TimeTask idleWorker;

    private Queue<WriteRequest> writeQueue = new LinkedList<WriteRequest>();

    private AtomicBoolean writingOps = new AtomicBoolean(false);

    public IoConnection(SocketChannel c, IoSupport s, NioEventLoop e) throws IOException {
        this.socketChannel = c;
        this.eventLoop = e;
        this.handler = s.getHandler();
        this.executor = s.getExecutor();
        this.idleWorker = s.getIdleWorker();
        this.socketChannel.configureBlocking(false);
    }

    public AttributeMap getAttributes(){
        return attributes;
    }

    public void setSelectionKey(SelectionKey key) {
        this.selkey = key;
    }

    public SocketChannel channel() {
        return socketChannel;
    }

    public void processIdle(){
        LOG.info("idle nowTime : {}",System.currentTimeMillis());
        handler.connectionIdle(this);
    }

    public int writeDirect(Object message){
        try{
            if(!isWritingOps()){
                return socketChannel.write((ByteBuffer)message);
            }else{
                return -1;
            }
        }catch (IOException e){
            e.printStackTrace();
            processException(e);
            return -1;
        }
    }

    private void restWriteOps(){
        if(writingOps.get()){
            writingOps.getAndSet(false);
        }
    }


    private boolean isWritingOps(){
        return writingOps.get();
    }


    public void processSent(IoConnection connection,Object messsage){
        LOG.info("processSent....");
    }

    public void processWrite() throws IOException {
        try {
            do{
                WriteRequest request = writeQueue.peek();

                ByteBuffer messageBuf = (ByteBuffer)request.getMessage();

                socketChannel.write(messageBuf);

                if(messageBuf.remaining() == 0){
                    writeQueue.poll();
                    if(request.getFuture() != null){
                        request.getFuture().setResult(request);
                    }

                    processSent(this,request.getOriginalMessage());
                }else{
                    //write nothing,will be process into next selectLoop.
                    break;
                }

            }while(!writeQueue.isEmpty());

            synchronized (writeQueue){
                if(writeQueue.isEmpty()){
                    if(isClose()){
                        close();
                    }else{
                        //stop write event in eventLoop
                        restWriteOps();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            processException(e);
        }
    }

    private boolean isClose(){
        return false;
    }


    private volatile long writeByteCount = 0;

    private long tcpbuffsize = 1024;

    private void copyBuf(WriteRequest request){
        ByteBuffer buf = (ByteBuffer)request.getMessage();
        ByteBuffer tbuf = ByteBuffer.allocate(buf.remaining());
        tbuf.put(buf);
        request.setMessage(tbuf);
    }

    public WriteRequest enqueneWriteRequest(WriteRequest request)throws IOException{
        ByteBuffer messageBuf = (ByteBuffer)request.getMessage();
        if(writeQueue.isEmpty()){
            int writeSize = writeDirect(messageBuf);
            if(writeSize > 0){
                writeByteCount += writeSize;
            }

            int remaining = messageBuf.remaining();

            if(writeSize < 0 || remaining > 0){
                writeQueue.add(request);
                if(writingOps.getAndSet(true)){
                    //register write event in eventLoop
                }
            }

        }else{
            writeQueue.add(request);
        }
        return request;
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
//        LOG.debug("processException:"+t.getMessage();
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

}
