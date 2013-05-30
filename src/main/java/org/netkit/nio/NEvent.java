package org.netkit.nio;

/**
 * User: shell0dh
 * Date: 13-5-30
 * Time: 下午11:18
 */
public class NEvent {
    private int ops;
    private NConnection connection;

    public NEvent(int o,NConnection conn){
        this.ops = o;
        this.connection = conn;
    }

    public NConnection getConnection(){
        return  connection;
    }

    public int eventOps(){
        return ops;
    }
}
