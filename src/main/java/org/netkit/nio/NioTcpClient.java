package org.netkit.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
  * User: shell0dh
 * Date: 13-8-3
 * Time: 下午3:04
  */
public class NioTcpClient{
    private IoSupport support;
    private NioEventLoop connectEventLoop;
    private NioSelectPool selectPool;

    public NioTcpClient(){
        this.connectEventLoop = new NioEventLoop("connectEventLoop",0);
        this.selectPool = new NioSelectPool("readWriteEventLoop",2);
    }

    public NioTcpClient(NioSelectPool pool){
        this.connectEventLoop = pool.getNextLoop();
        this.selectPool = pool;
    }

    public IoConnection connect(SocketAddress address){
        SocketChannel clientChanel = null;
        try{
            clientChanel = SocketChannel.open();
            clientChanel.configureBlocking(false);
        }catch (IOException e){
            e.printStackTrace();
        }

        IoConnection connection = null;
        try{
            connection = new IoConnection(clientChanel,support,selectPool.getNextLoop());
        }catch (IOException e){
            e.printStackTrace();
        }

        boolean isConnect = false;
        try{
            isConnect = clientChanel.connect(address);
        }catch (IOException e){
            e.printStackTrace();
        }

        if(isConnect){
        }
        return null;
    }
}
