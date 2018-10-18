package com.fdh.rpc.registry;

import com.fdh.rpc.protocol.HostAndPort;

import java.util.List;

/**
 * 注册中心
 *
 * @author Jeffery
 */
public interface Registry {

    /**
     * 注册服务
     * @param targetInterface 目标服务类
     * @param hostAndPort 提供服务的服务端ip+port
     */
    void registService(Class targetInterface,HostAndPort hostAndPort);

    /**
     * 发现服务
     * @param targetInterface 目标服务类
     * @return 提供服务的服务列表
     */
    List<HostAndPort> discoverService(Class targetInterface);

    /**
     * 订阅服务,用于更新服务列表
     * @param targetInterface 目标服务类
     * @param hostAndPorts 提供服务的服务端列表
     */
    void subscribeService(Class targetInterface,List<HostAndPort> hostAndPorts);

    /**
     * 关闭资源
     */
    void close();
}
