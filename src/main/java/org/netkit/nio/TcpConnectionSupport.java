package org.netkit.nio;

/**
 * Date: 13-5-30
 * Time: 下午3:59
  */
public class TcpConnectionSupport {
    private IoHandler[] ioHandlers;

    public TcpConnectionSupport(){
    }


    public void setFilter(IoHandler ... handlers){
        this.ioHandlers = handlers;
    }
}
