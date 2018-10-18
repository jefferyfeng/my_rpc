package com.fdh.rpc.server;

import com.fdh.rpc.client.DemoService;

/**
 * 测试 服务器端 服务的实现
 *
 * @author Jeffery
 */
public class DemoServiceImpl implements DemoService{
    @Override
    public int sum(int a, int b) {
        return a + b;
    }
}
