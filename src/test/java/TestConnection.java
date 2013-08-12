

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;


/**
  * Date: 13-6-5
 * Time: 下午6:11
  */
public class TestConnection {
    private static final Logger LOG = LoggerFactory.getLogger(TestConnection.class);
    public static void main(String[] strings)throws Exception{
        final absFuture<Integer> future = new absFuture<Integer>();
        new Thread(){
            @Override
            public void run() {
                try{
                    Thread.sleep(10000);
                }catch (InterruptedException e){
                }
                future.setResult(1);
            }
        }.start();
        LOG.info("future thread starting....");
        LOG.info("future test result :{}",future.get());
    }
}

class absFuture<V> implements Future<V>{
    private final Sync sync = new Sync();
    private V result;

    private class Sync extends AbstractQueuedSynchronizer{
        @Override
        protected int tryAcquireShared(int ig) {
            return (getState() == 1 ? 1 : -1);
        }

        @Override
        protected boolean tryReleaseShared(int ig) {
            setState(1);
            return true;
        }
    }

    private void signal(){
        sync.releaseShared(0);
    }

    private void await()throws InterruptedException{
        sync.acquireSharedInterruptibly(0);
    }

    public void setResult(V r){
        this.result = r;
        signal();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        await();
        return result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
