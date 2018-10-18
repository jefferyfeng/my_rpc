package com.fdh.rpc.loadbalance;

import com.fdh.rpc.protocol.HostAndPort;

import java.util.List;

/**
 * 负载均衡策略
 *
 * @author Jeffery
 */
public interface LoadBalance {
    /**
     * 负载均衡
     * @param hostAndPorts 提供服务的服务列表
     * @return 负载均衡策略得到的某个服务提供者
     */
    HostAndPort loadBalance(List<HostAndPort> hostAndPorts);
}
