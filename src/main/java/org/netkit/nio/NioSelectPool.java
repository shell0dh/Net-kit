package org.netkit.nio;

import java.io.IOException;

/**
 * Date: 13-7-30
 * Time: 下午10:18
 */
public class NioSelectPool {

    private NioEventLoop[] pool;

    private int currentloop = -1;
    private int size;

    public NioSelectPool(String poolname,int s)throws IOException {
        pool = new NioEventLoop[s];
        for(int i=0 ;i < s;i++){
            pool[i] = new NioEventLoop(poolname,i);
        }
        this.size = s;
    }

    public NioSelectPool(String poolname)throws IOException{
        this(poolname,Runtime.getRuntime().availableProcessors());
    }

    public NioEventLoop getNextLoop(){
        return pool[(currentloop++)% size];
    }


    public void startup(){
        for(NioEventLoop e : pool){
        }

    }

    public void shutdown(){
        for(NioEventLoop e : pool);
    }
}
