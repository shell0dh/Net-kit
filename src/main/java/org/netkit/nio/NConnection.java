package org.netkit.nio;

import java.nio.channels.SocketChannel;

/**
   * Date: 13-5-30
 * Time: 下午4:12
  */
public class NConnection {

    private SocketChannel socketChannel;


    public NConnection(SocketChannel s){
        this.socketChannel = s;
    }

    public SocketChannel channel(){
        return socketChannel;
    }
}
