package org.netkit.nio;

import java.nio.channels.SelectableChannel;

/**
 * User: shell0dh
 * Date: 13-5-30
 * Time: 下午11:18
 */
public class IoEvent{
    private int ops;
    private SelectableChannel channel;
    private NioEventListener listener;
    private RegCallback callback;


    public IoEvent(int o,NioEventListener a,SelectableChannel c,RegCallback cb){
        this.ops = o;
        this.listener = a;
        this.channel = c;
        this.callback = cb;
    }

    public NioEventListener getListener(){
        return listener;
    }

    public SelectableChannel ch(){
        return channel;
    }

    public RegCallback cb(){
        return callback;
    }

    public int Ops(){
        return ops;
    }
}
