package org.netkit.nio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


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

    private volatile boolean runing = true;

    public TimeTask(){
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

    private int processIdleConnection(long nowTime){
        long delta = nowTime - lastCheckTime;
        if(delta < TICK_TIME){
            LOG.info("not a second between the last checks,abort");
            return 0;
        }

        int startIndex = (int)(lastCheckTime/1000L) % WHEELSIZE;

        int startIndex2 = ((int)(Math.max(lastCheckTime,nowTime - (WHEELSIZE*1000L) + 1)/1000L)) % WHEELSIZE;

        int endIndex = (int)(nowTime/1000L) % WHEELSIZE;

        LOG.info("startIndex : {} endIndex : {} startIndex2:{}",startIndex,endIndex,startIndex2);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
