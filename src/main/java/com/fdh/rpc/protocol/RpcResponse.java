package com.fdh.rpc.protocol;

import java.io.Serializable;

/**
 * 响应的数据对象
 *
 * @author Jeffery
 */
public class RpcResponse implements Serializable{
    /**
     * 返回结果
     */
    private Object result;
    /**
     * 异常
     */
    private Throwable error;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
