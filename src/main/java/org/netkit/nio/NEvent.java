package org.netkit.nio;

/**
 * User: shell0dh
 * Date: 13-5-30
 * Time: 下午11:18
 */
public class NEvent<T>{
    private int ops;
    private NServerConnection connection;
    private T attachment;

    public NEvent(int o,T a){
        this.ops = o;
        this.attachment = a;
    }

    public T getAttachment(){
        return  attachment;
    }

    public int eventOps(){
        return ops;
    }
}
