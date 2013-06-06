package org.netkit.nio;


import java.nio.channels.SocketChannel;

/**
 * User: shell0dh
 * Date: 13-5-21
 * Time: 下午9:57
  */
public interface EventLoop{
    public void initEventLoop()throws Exception;
    public void startEventLoop();
    public void registerEvent(NEvent e);
    public void unregister(SocketChannel socketChannel);
    public void stopEventLoop();
}
