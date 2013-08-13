package org.netkit.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
   */
public abstract class AbstractIoFuture<V> implements IoFuture<V>{

    private static final Logger LOG = LoggerFactory.getLogger(NioEventLoop.class);

    private final Sync sync = new Sync();

    private AtomicReference<Object> resultObject = new AtomicReference<Object>();

    @Override
    public void setResult(V r) {
        synchronized (sync){
            resultObject.set(r);
            sync.tryReleaseShared(0);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        CancellationException c = new CancellationException();
        synchronized (sync){
            resultObject.set(c);
            sync.tryReleaseShared(0);
        }
        return sync.getSyncStatus() == 1;
    }

    @Override
    public boolean isCancelled() {
        return resultObject.get() instanceof CancellationException;
    }

    @Override
    public boolean isDone() {
        return sync.getSyncStatus() == 1;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        sync.tryAcquireShared(0);
        if(isCancelled()){
            LOG.info("throw Cancellation");
            throw (CancellationException)resultObject.get();
        }
        return (V)resultObject.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        sync.tryAcquireSharedTimeOut(timeout,unit);
        if(isCancelled()){
            throw (CancellationException)resultObject.get();
        }
        return (V)resultObject.get();
    }

    private class Sync extends AbstractQueuedSynchronizer{

        @Override
        protected boolean tryReleaseShared(int ig) {
            setState(1);
            return true;
        }

        @Override
        protected int tryAcquireShared(int arg) {
            return (getState() == 1 ? 1 : -1);
        }

        protected int getSyncStatus(){
            return getState();
        }

        protected boolean tryAcquireSharedTimeOut(long timeout,TimeUnit unit)throws InterruptedException{
            long t = timeout;
            if(unit == TimeUnit.MILLISECONDS){
                t = (t*1000*1000);
            }else if(unit == TimeUnit.MICROSECONDS){
                t = (t*1000);
            }
            return tryAcquireSharedNanos(0,t);
        }
    }
}
