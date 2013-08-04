package org.netkit.nio;

/**
   * Date: 13-5-30
 * Time: 下午6:15
  */
public interface IoHandler {

    void connctionOpen(IoConnection connection);

    void messageReceived(IoConnection connection,Object message);

    void exceptionCaught(IoConnection connection,Exception e);
}
