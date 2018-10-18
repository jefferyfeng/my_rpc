package com.fdh.rpc;

import com.fdh.rpc.client.DemoService;
import com.fdh.rpc.protocol.HostAndPort;
import com.fdh.rpc.registry.Registry;
import com.fdh.rpc.registry.ZookeeperRegistry;
import com.fdh.rpc.server.DemoServiceImpl;
import com.fdh.rpc.server.NettyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟服务器端
 *
 * @author Jeffery
 */
public class ServerStarter {

    public static void main(String[] args) throws IOException {
        HostAndPort hostAndPort = new HostAndPort("120.78.156.224",2181);
        Registry registry = new ZookeeperRegistry(hostAndPort);
        Map<Class,Object> beanMap = new HashMap(2);

        beanMap.put(DemoService.class, new DemoServiceImpl());

        NettyProvider provider = new NettyProvider(registry,20880);

        provider.setBeanMap(beanMap);

        provider.start();
        System.in.read();
        provider.close();
    }
}
