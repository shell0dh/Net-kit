package org.netkit.nio;

/**
  * Date: 13-5-30
 * Time: 下午4:07
  */
public class NEvent {

    public enum NEventType{
        READABLE,
        WRITEABLE,
        ACCEPT;
    }

    private NConnection connection;

    public NConnection getConnection() {
        return connection;
    }

    public void setConnection(NConnection connection) {
        this.connection = connection;
    }
}
