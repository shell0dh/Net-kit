package org.netkit.nio;


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
public class IoConnection extends AbstractIoFilter implements NioEventListener{

    private SocketChannel socketChannel;

    private NioEventLoop eventLoop;

    private SelectionKey selkey;

    private Executor executor;

    private IoHandler handler;

    private Queue<ByteBuffer> writeQueue = new LinkedList<ByteBuffer>();

    public IoConnection(SocketChannel c,IoSupport s,NioEventLoop e)throws IOException{
        this.socketChannel = c;
        this.eventLoop = e;
        this.handler = s.getHandler();
        this.executor = s.getExecutor();
        this.socketChannel.configureBlocking(false);
    }

    public void setSelectionKey(SelectionKey key){
        this.selkey = key;
    }

    public SocketChannel channel(){
        return socketChannel;
    }

    public void processDirectWrite(ByteBuffer writeBuffer){
        System.out.println("processDirectWrite..");
        try {
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("proessDirectWrite..end");
    }

    public void syncWrite(ByteBuffer byteBuffer){
        writeQueue.add(byteBuffer);
    }

    public void processWrite()throws IOException{
        while(!writeQueue.isEmpty()){
            ByteBuffer t = writeQueue.poll();
            t.flip();
            socketChannel.write(t);
        }
    }

    public void close(){
        eventLoop.unregister(this,socketChannel);
    }

    public void messageReceived(ByteBuffer readbuffer){
        System.out.println(new String(readbuffer.array()));
        processDirectWrite(readbuffer);
    }

    public void processRead(ByteBuffer tbuf){
        try {
            int readByteCount = socketChannel.read(tbuf);
            if(readByteCount < 0){
               // processClose();
            }else if(readByteCount > 0){
                messageReceived(tbuf);
            }
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    @Override
    public void ioReady(boolean isReadable, boolean isWriteable, boolean ig, boolean connect,ByteBuffer byteBuffer) throws IOException {
        if(isReadable){
            processRead(byteBuffer);
        }
        if(isWriteable){
            processWrite();
        }
        if(connect){
        }
    }


    @Override
    public void open(IoConnection ioConnect) {
        System.out.println("first open Connection");
    }

    @Override
    public void close(IoConnection ioConnect) {
        close();
    }

    @Override
    public void ioConnectIdle(IoConnection ioConnect, int status) {
        System.out.println("status Idle");
    }

    @Override
    public void messageReceived(IoConnection ioConnect) {
    }

    @Override
    public void messageSent(IoConnection ioConnect) {
    }


    public void processMessageWriting(){
    }


    @Override
    public void messageWriting(IoConnection ioConnect) {

    }
}
