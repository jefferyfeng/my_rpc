package com.fdh.rpc.loadbalance;

import com.fdh.rpc.protocol.HostAndPort;

import java.util.List;
import java.util.Random;

/**
 * 随机 负载均衡策略
 *
 * @author Jeffery
 */
public class RandomLoadBalance implements LoadBalance {
    private Random random = new Random();
    @Override
    public HostAndPort loadBalance(List<HostAndPort> hostAndPorts) {
        int i = random.nextInt(hostAndPorts.size());
        return hostAndPorts.get(i);
    }
}
