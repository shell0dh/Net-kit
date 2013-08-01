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
public class IoConnection implements NioEventListener{

    private SocketChannel socketChannel;

    private NioEventLoop eventLoop;

    private SelectionKey selkey;

    private IoSupport support;

    private Queue<ByteBuffer> writeQueue = new LinkedList<ByteBuffer>();

    public IoConnection(SocketChannel c,IoSupport s){
        this.socketChannel = c;
        this.support = s;
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
    public void ioReady(boolean read, boolean write, boolean accept, boolean connect) throws IOException {
        if(read){
            processRead();
        }
        if(write){
            processWrite();
        }
    }

}
