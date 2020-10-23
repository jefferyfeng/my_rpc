package com.fdh.rpc.loadbalance;

import com.fdh.rpc.protocol.HostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:  轮询
 * @date: 2020/10/23 15:27
 * @author: fdh
 */
public class RoundRobinLoadBalance implements LoadBalance {

    private static final Logger log = LoggerFactory.getLogger(RoundRobinLoadBalance.class);

    private AtomicInteger totalTimes = new AtomicInteger(0);

    @Override
    public HostAndPort loadBalance(List<HostAndPort> hostAndPorts) {
        int expect;
        int next;
        do {
            expect = totalTimes.get();
            next = expect > Integer.MAX_VALUE ? 0 : expect + 1;
        } while (!totalTimes.compareAndSet(expect, next));

        log.info("第{}此请求服务..", next);

        // 无注册服务列表，返回空服务
        if( hostAndPorts == null || hostAndPorts.isEmpty() ){
            return null;
        }
        int index = next % hostAndPorts.size();
        return hostAndPorts.get(index);
    }
}
