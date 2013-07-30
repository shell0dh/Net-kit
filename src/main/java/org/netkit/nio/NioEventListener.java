package org.netkit.nio;

import java.io.IOException;

/**
  * User: shell0dh
 * Date: 13-6-2
 * Time: 上午12:56
  */
public interface NioEventListener {
    void ioReady(boolean read,boolean write,boolean accept,boolean connect)throws IOException;
}
