package org.netkit.nio;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date: 13-5-30
 * Time: 下午3:59
  */
public class IoSupport {

    private IoHandler[] ioHandlers;

    private Map<Integer,IoConnection> connectionMap = new ConcurrentHashMap<Integer, IoConnection>();

    private AtomicInteger next_Id = new AtomicInteger(0);

    public IoSupport(){
    }

    public void setFilter(IoHandler ... handlers){
        this.ioHandlers = handlers;
    }

    public void registerConnection(IoConnection connection){
        Integer id = next_Id.incrementAndGet();
        connectionMap.put(id,connection);
    }

}
