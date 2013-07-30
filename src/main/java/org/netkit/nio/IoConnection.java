package org.netkit.nio;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

/**
   * Date: 13-5-30
 * Time: 下午4:12
  */
public class IoConnection implements IoEventListener{

    private SocketChannel socketChannel;

    private IoEventLoop eventLoop;

    private SelectionKey selkey;

    private IoSupport connectionSupport;

    private Queue<ByteBuffer> writeQueue = new LinkedList<ByteBuffer>();

    public NServerConnection(SocketChannel s, IoEventLoop e, IoSupport support){
        this.eventLoop = e;
        this.socketChannel = s;
        this.connectionSupport = support;
        try {
            this.socketChannel.configureBlocking(false);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
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

    public void processWrite(){
        while(!writeQueue.isEmpty()){

        }
    }

    public void close(){
       eventLoop.unregister(socketChannel);
    }

    public void onException(Exception e){
        e.printStackTrace();
    }

    public void messageReceived(ByteBuffer readbuffer){
        System.out.println(new String(readbuffer.array()));
        processDirectWrite(readbuffer);
    }

    public void processRead(){
        try {
            ByteBuffer tbuf = ByteBuffer.allocate(1024);
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
    public void ioNotify(boolean isReadable, boolean isWriteable, boolean accept, boolean connect, IoEventLoop e) throws Exception {
        if(isReadable){
            processRead();
        }
        if(isWriteable){
            processWrite();
        }
    }

}
