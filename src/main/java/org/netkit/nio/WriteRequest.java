package org.netkit.nio;

/**
 * Date: 13-9-30
 * Time: 下午10:20
 */
public class WriteRequest {
    private Object message;
    private Object originalMessage;

    private IoFuture<WriteRequest> future;


    public WriteRequest(Object orgMessage){
        this.originalMessage = orgMessage;
        this.message = orgMessage;
    }


    public Object getMessage(){
        return message;
    }


    public Object getOriginalMessage(){
        return originalMessage;
    }

    public void setMessage(Object m){
        this.message = m;
    }

    public void setFuture(final IoFuture<WriteRequest> f){
        this.future = f;
    }

    public IoFuture<WriteRequest> getFuture(){
        return future;
    }

    @Override
    public String toString() {
        return "WriteRequest{" +
                "message=" + message +
                ", originalMessage=" + originalMessage +
                ", future=" + future +
                '}';
    }
}
