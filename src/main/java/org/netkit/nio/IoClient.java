package org.netkit.nio;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * User: shell0dh
 * Date: 13-8-2
 * Time: 上午12:21
 */
public interface IoClient {
    public IoConnection connect(SocketAddress address)throws IOException;
}
