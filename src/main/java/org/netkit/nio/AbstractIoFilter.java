package org.netkit.nio;

/**
  * User: shell0dh
 * Date: 13-8-2
 * Time: 上午1:25
  */
public class AbstractIoFilter implements IoFilter{

    @Override
    public void open(IoConnection ioConnect) {

    }

    @Override
    public void close(IoConnection ioConnect) {

    }

    @Override
    public void ioConnectIdle(IoConnection ioConnect, int status) {

    }

    @Override
    public void messageReceived(IoConnection ioConnect) {

    }

    @Override
    public void messageSent(IoConnection ioConnect) {

    }

    @Override
    public void messageWriting(IoConnection ioConnect) {

    }
}
