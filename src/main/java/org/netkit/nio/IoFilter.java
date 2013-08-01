package org.netkit.nio;

/**
 * User: shell0dh
 * Date: 13-8-2
 * Time: 上午12:15
 */
public interface IoFilter {
    void open(IoConnection ioConnect);
    void close(IoConnection ioConnect);
    void ioConnectIdle(IoConnection ioConnect,int status);
    void messageReceived(IoConnection ioConnect);
    void messageSent(IoConnection ioConnect);
    void messageWriting(IoConnection ioConnect);
}
