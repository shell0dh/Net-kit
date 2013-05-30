package org.netkit.nio;

/**
 * User: shell0dh
 * Date: 13-5-21
 * Time: 下午9:57
  */
public interface EventLoop{
    public void initEvenLoop()throws Exception;
    public void startEventLoop();
    public void registerEvent(NEvent e);
    public void stopEventLoop();
    public TcpConnectionSupport getConnectionSupport();
}
