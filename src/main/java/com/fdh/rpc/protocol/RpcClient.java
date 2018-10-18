package com.fdh.rpc.protocol;

/**
 * rpc客户端
 *
 * @author Jeffery
 */
public interface RpcClient {
    /**
     * rpc调用
     * @param request 请求信息
     * @param hostAndPort 调用的服务提供者
     * @return 请求服务者后得到的响应信息
     */
    RpcResponse call(RpcRequest request,HostAndPort hostAndPort);

    /**
     * 资源释放
     */
    void close();
}
