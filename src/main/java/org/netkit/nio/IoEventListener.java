package org.netkit.nio;

/**
  * User: shell0dh
 * Date: 13-6-2
 * Time: 上午12:56
  */
public interface IoEventListener {
    void ioNotify(boolean read,boolean write,boolean accept,boolean connect,IoEventLoop e)throws Exception;
}
