package org.netkit.nio;

/**
  * User: shell0dh
 * Date: 13-8-3
 * Time: 下午10:02
  */
public interface IoConfig {
    Integer getReadBufferSize();
    void setReadBufferSize(Integer size);
    void setReuseAddress(boolean reuseAddress);
    Boolean isReuseAddress();
    Integer getTimeout();
    void setTimeout(int timeOut);
}
