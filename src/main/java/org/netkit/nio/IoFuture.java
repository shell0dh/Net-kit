package org.netkit.nio;

import java.util.concurrent.Future;

/**
 * Date: 8/13/13
 * Time: 1:53 PM
 */
public interface IoFuture<V> extends Future<V>{
    void setResult(V r);
}
