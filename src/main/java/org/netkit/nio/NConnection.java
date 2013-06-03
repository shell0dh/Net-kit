package org.netkit.nio;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
   * Date: 13-5-30
 * Time: 下午4:12
  */
public class NConnection implements IoEventListener{

    private SocketChannel socketChannel;

    private IoEventLoop eventLoop;

    private SelectionKey selkey;

    private NConnectionSupport connectionSupport;

    public NConnection(SocketChannel s,IoEventLoop e,NConnectionSupport support){
        this.eventLoop = e;
        this.socketChannel = s;
        this.connectionSupport = support;
    }

    public void setSelectionKey(SelectionKey key){
        this.selkey = key;
    }

    public SocketChannel channel(){
        return socketChannel;
    }

    public void processWrite(){

    }

    public void processClose(){
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onException(Exception e){
        if(e != null){
        }
    }

    public void messageReceived(ByteBuffer readbuffer){
        
    }

    public void processRead(){
        try {
            ByteBuffer tbuf = ByteBuffer.allocate(1024);
            int readByteCount = socketChannel.read(tbuf);
            if(readByteCount < 0){
                processClose();
            }else if(readByteCount > 0){
                messageReceived(tbuf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ioNotify(boolean isReadable,boolean isWriteable,IoEventLoop e){
        if(isReadable){
            processRead();
        }
        if(isWriteable){
            processWrite();
        }
    }
}
