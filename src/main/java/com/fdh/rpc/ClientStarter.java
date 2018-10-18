package com.fdh.rpc;

import com.fdh.rpc.client.DemoService;
import com.fdh.rpc.client.NettyPpcClient;
import com.fdh.rpc.loadbalance.RandomLoadBalance;
import com.fdh.rpc.protocol.HostAndPort;
import com.fdh.rpc.protocol.RpcProxy;
import com.fdh.rpc.registry.Registry;
import com.fdh.rpc.registry.ZookeeperRegistry;

/**
 * 模拟客户端
 *
 * @author Jeffery
 */
public class ClientStarter {
    public static void main(String[] args) {
        HostAndPort hostAndPort = new HostAndPort("120.78.156.224",2181);
        RpcProxy<DemoService> rpcProxy = new RpcProxy<DemoService>();

        //配置注册中心
        Registry registry = new ZookeeperRegistry(hostAndPort);
        rpcProxy.setRegistry(registry);
        rpcProxy.setLoadBalance(new RandomLoadBalance());
        rpcProxy.setClient(new NettyPpcClient());

        DemoService proxy = rpcProxy.createProxy(DemoService.class);
        int sum = proxy.sum(1, 2);
        System.out.println("Rpc result : "+sum);
    }
}
