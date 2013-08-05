package org.netkit.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date: 13-7-30
 * Time: 下午10:18
 */
public class NioSelectPool {

    private static final Logger LOG = LoggerFactory.getLogger(NioSelectPool.class);

    private NioEventLoop[] pool;

    private AtomicInteger currentloop = new AtomicInteger(0);
    private int size;

    public NioSelectPool(String poolname,int s){
        pool = new NioEventLoop[s];
        for(int i=0 ;i < s;i++){
            pool[i] = new NioEventLoop(poolname,i);
        }
        this.size = s;
    }

    public NioSelectPool(String poolname){
        this(poolname,Runtime.getRuntime().availableProcessors());
    }

    public NioEventLoop getNextLoop(){
        return pool[(currentloop.getAndIncrement())% size];
    }



    public void shutdown(){
        for(NioEventLoop e : pool){
            e.shutdown();
        }
    }
}
