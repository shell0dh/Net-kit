package org.netkit.nio;

/**
 * Date: 13-5-30
 * Time: 下午6:24
 */
public class HandlerAdapter{

    private IoHandler[] ioHandlerChain = new IoHandler[0];

    public HandlerAdapter(IoHandler[] filters){
        this.ioHandlerChain = filters;
    }


    public void onReadEvent(NConnection connection){

    }

    public void onWriteEvent(NConnection connection){

    }
}
