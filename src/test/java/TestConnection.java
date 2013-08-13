import org.netkit.nio.AbstractIoFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
  * Date: 13-6-5
 * Time: 下午6:11
  */
public class TestConnection {

    private static final Logger LOG = LoggerFactory.getLogger(TestConnection.class);

    public static void main(String[] strings)throws Exception{
        final TestFuture<Integer> future = new TestFuture<Integer>();
        Thread t = new Thread(){
            @Override
            public void run() {
                try{
                    Thread.sleep(60000);
                }catch (InterruptedException e){
                }
                future.setResult(1);
            }
        };
        t.setDaemon(true);
        t.start();

        LOG.info("future thread starting....");
        LOG.info("future isCancelled :{}",future.isCancelled());
        LOG.info("future isDone :{}",future.isDone());
        LOG.info("future canelled :{}",future.cancel(true));
        LOG.info("future isCancelled :{}",future.isCancelled());
        LOG.info("future test result :{}",future.get(10000, TimeUnit.MILLISECONDS));
        LOG.info("future test result isDone :{}",future.isDone());
        LOG.info("future test result isCancelled :{}",future.isCancelled());
    }
}

class TestFuture<V> extends AbstractIoFuture<V> {
}

