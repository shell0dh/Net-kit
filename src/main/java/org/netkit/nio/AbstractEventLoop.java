package org.netkit.nio;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: yfliuyu
 * Date: 13-5-24
 */
public class AbstractEventLoop extends Thread implements EventLoop {

    private AtomicBoolean runing = new AtomicBoolean(false);

    private NConnectionSupport support;

    public AbstractEventLoop(NConnectionSupport nConnectionSupport){
        setDaemon(false);
        this.support = nConnectionSupport;
    }

    public SocketChannel channelFor(SelectionKey key){
        return (SocketChannel) key.channel();
    }

    public boolean runing(){
        return runing.get();
    }

    public void close(SelectionKey key){
        SocketChannel socketChannel = channelFor(key);
        Socket s = socketChannel.socket();
        if(s != null){
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        key.attach(null);
        key.cancel();
    }


    @Override
    public void initEventLoop()throws Exception{
    }

    @Override
    public void startEventLoop() {
        runing.set(true);
        start();
    }

    @Override
    public void registerEvent(NEvent e) {
    }

    @Override
    public void unregister(SocketChannel socketChannel) {

    }

    public void stopEventLoop(){
        runing.getAndSet(false);
    }

    public NConnectionSupport getConnectionSupport() {
        return support;
    }
}
