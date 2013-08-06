package org.netkit.nio;


import java.util.Set;


/**
  * Date: 8/6/13
 * Time: 10:18 AM
  */
public class IdleTask {
    private long delyTime; //秒
    private Set<IoConnection>[] wheel;

    public IdleTask(){
    }


    class TickWorker extends Thread{
        public TickWorker(){
            super("TickWorker-Thread");
        }

        @Override
        public void run() {
        }
    }
}
