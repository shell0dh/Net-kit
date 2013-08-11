package org.netkit.nio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
  * Date: 8/6/13
 * Time: 10:18 AM
  */
public class TimeTask {

    private static final Logger LOG = LoggerFactory.getLogger(TimeTask.class);

    private final long TICK_TIME = 1000; //秒

    private final int WHEELSIZE = 100;

    private final long MAX_IDLE_TIME_IN_MS = WHEELSIZE * 1000L;

    private long lastCheckTime = System.currentTimeMillis();

    private final Set<IoConnection>[] readTrace = new Set[WHEELSIZE];

    private final Set<IoConnection>[] writeTrace = new Set[WHEELSIZE];

    private static final AttributeKey<Integer> READ_IDLE_INDEX = AttributeKey.createKey(Integer.class,"idle.read.index");

    private static final AttributeKey<Integer> WRITE_IDLE_INDEX = AttributeKey.createKey(Integer.class,"idle.write.index");

    private final TickWorker worker = new TickWorker();

    private final long idleTime = 5;

    private volatile boolean runing = true;

    public TimeTask(){
        start();
    }


    public void start(){
        worker.start();
    }

    public void stop(){
        runing = false;
        try{
            worker.interrupt();
            worker.join();
        }catch (InterruptedException e){
            //ig
        }
    }


    private void processIndex(int index,int status){
        final Set<IoConnection>[] ioConnectionSet = status == 0 ? readTrace : writeTrace;
        Set<IoConnection> connections = ioConnectionSet[index];
        if(connections != null){
            for(IoConnection connection : connections){
                connection.processIdle();
            }
        }
        ioConnectionSet[index] = null;
    }

    public void processWriteIdle(IoConnection connection,long nowTime){
        int index = (int)(nowTime/1000L)%WHEELSIZE;
        index += idleTime;
        if(writeTrace[index] == null){
            writeTrace[index] = Collections.newSetFromMap(new ConcurrentHashMap<IoConnection, Boolean>());
        }
        connection.getAttributes().setAttribute(WRITE_IDLE_INDEX,index);
        writeTrace[index].add(connection);
    }


    public void processReadIdle(IoConnection connection,long nowTime){
        int index = (int)(nowTime/1000L)%WHEELSIZE;
        index += idleTime;
        if(readTrace[index] == null){
            readTrace[index] = Collections.newSetFromMap(new ConcurrentHashMap<IoConnection, Boolean>());
        }
        connection.getAttributes().setAttribute(READ_IDLE_INDEX,index);
        readTrace[index].add(connection);
    }


    private int processIdleConnection(long nowTime){
        long delta = nowTime - lastCheckTime;
        if(delta < TICK_TIME){
            LOG.info("not a second between the last checks,abort");
            return 0;
        }

        int startIndex = (int)(lastCheckTime/1000L) % WHEELSIZE;

        int endIndex = (int)(nowTime/1000L) % WHEELSIZE;

        LOG.info("startIndex : {} endIndex : {}",startIndex,endIndex);

        do{
            processIndex(startIndex,0);
            processIndex(startIndex,1);
            startIndex++;
        }while(startIndex == endIndex);

        lastCheckTime = nowTime;
        return -1;
    }


    class TickWorker extends Thread{
        public TickWorker(){
            super("TickWorker-Thread");
        }

        @Override
        public void run() {
            while(runing){
                try{
                    sleep(TICK_TIME);
                    processIdleConnection(System.currentTimeMillis());
                }catch (Exception e){
                    LOG.error("tickWorker", e.getMessage());
                }
            }
        }
    }

    public static void main(String[] s){
        TimeTask tickWorker = new TimeTask();
        tickWorker.start();
    }
}
