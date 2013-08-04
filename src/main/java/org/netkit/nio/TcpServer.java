package org.netkit.nio;

import java.io.IOException;

/**
 * User: shell0dh
 * Date: 13-8-3
 * Time: 下午9:56
 */
public interface TcpServer {
    void bind(int port)throws IOException;
}
