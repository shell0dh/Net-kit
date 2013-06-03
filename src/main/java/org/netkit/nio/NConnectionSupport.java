package org.netkit.nio;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date: 13-5-30
 * Time: 下午3:59
  */
public class NConnectionSupport {

    private IoHandler[] ioHandlers;

    private Map<Integer,NConnection> connectionMap = new ConcurrentHashMap<Integer, NConnection>();

    private AtomicInteger next_Id = new AtomicInteger(0);

    public NConnectionSupport(){

    }

    public void setFilter(IoHandler ... handlers){
        this.ioHandlers = handlers;
    }

    public void registerConnection(NConnection connection){
        Integer id = next_Id.incrementAndGet();
        connectionMap.put(id,connection);
    }

}
