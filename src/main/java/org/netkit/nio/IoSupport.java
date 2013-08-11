package org.netkit.nio;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date: 13-5-30
 * Time: 下午3:59
  */
public class IoSupport {

    private IoFilter[] filters;

    private IoHandler ioHandler;

    private Executor executor;

    private IoConfig config;

    private TimeTask task;

    private Map<Integer,IoConnection> connectionMap = new ConcurrentHashMap<Integer, IoConnection>();

    private AtomicInteger next_Id = new AtomicInteger(0);


    public TimeTask getIdleWorker(){
        return task;
    }


    public Executor getExecutor(){
        return executor;
    }

    public IoSupport(IoHandler handler,Executor e,IoConfig c,TimeTask t){
        this.executor = e;
        this.ioHandler = handler;
        this.config = c;
        this.task =t;
    }

    public void setFilter(IoFilter ... s){
        this.filters = s;
    }

    public void registerConnection(IoConnection connection){
        Integer id = next_Id.incrementAndGet();
        connectionMap.put(id,connection);
    }

    public IoHandler getHandler(){
        return ioHandler;
    }

    public IoFilter[] getIoFilters(){
        return filters;
    }

}
