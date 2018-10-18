package com.fdh.rpc.registry;

import com.fdh.rpc.protocol.HostAndPort;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 注册中心实现
 * zookeeper注册中心
 */
public class ZookeeperRegistry implements Registry {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private ZkClient zkClient;

    //初始化zkClient
    public ZookeeperRegistry(HostAndPort hostAndPort) {
        LOGGER.info("Connect zookeeper..");
        try {
            zkClient = new ZkClient(hostAndPort.getHost(),hostAndPort.getPort());
            LOGGER.info("Connect zookeeper success!");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Connect zookeeper error : "+ e.getMessage());
        }

    }

    @Override
    public void registService(Class targetInterface, HostAndPort hostAndPort) {
        //创建服务基结点
        String basePath = createBasePath(targetInterface);
        //创建当前服务节点
        String servicePath = basePath + "/" + hostAndPort.getHost() + ":" + hostAndPort.getPort();
        if(zkClient.exists(servicePath)){
            zkClient.delete(servicePath);
        }
        zkClient.createEphemeral(servicePath,hostAndPort);
    }

    @Override
    public List<HostAndPort> discoverService(Class targetInterface) {
        String basePath = createBasePath(targetInterface);
        //获取基结点下的服务
        List<String> children = zkClient.getChildren(basePath);
        List<HostAndPort> hostAndPorts = new CopyOnWriteArrayList<>();
        for (String child : children) {
            //获取节点下数据
            HostAndPort hostAndPort = (HostAndPort)zkClient.readData(basePath + "/" + child);
            hostAndPorts.add(hostAndPort);
        }
        return hostAndPorts;
    }

    @Override
    public void subscribeService(Class targetInterface, final List<HostAndPort> hostAndPorts) {
        final String basePath = createBasePath(targetInterface);
        //监听服务结点变化
        zkClient.subscribeChildChanges(basePath, new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {
                //服务节点改变时清空服务列表
                hostAndPorts.clear();
                //重新更新服务列表
                for (String child : list) {
                    HostAndPort hostAndPort = (HostAndPort) zkClient.readData(basePath + child);
                    hostAndPorts.add(hostAndPort);
                }
            }
        });
    }

    @Override
    public void close() {
        zkClient.close();
    }

    //创建服务基结点
    private String createBasePath(Class targetInterface){
        //服务基节点  /fdh/com.fdh.DemoService/providers
        String basePath = RegistryConstant.PREFFIX + "/" + targetInterface.getName() + RegistryConstant.SUFFIX;
        if(!zkClient.exists(basePath)){
            //创建基节点  true 递归创建父级节点
            zkClient.createPersistent(basePath,true);
        }
        return basePath;
    }
}
