package com.fdh.rpc.client;

/**
 * 测试服务
 *
 * @author Jeffery
 */
public interface DemoService {
    /**
     * demo
     * @param a 参数1
     * @param b 参数2
     * @return 参数1和参数2的和
     */
    int sum(int a,int b);
}
