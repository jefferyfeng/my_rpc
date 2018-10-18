package com.fdh.rpc.protocol;

import com.fdh.rpc.loadbalance.LoadBalance;
import com.fdh.rpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * rpc代理
 * @param <T> 服务类
 * @author Jeffery
 */
public class RpcProxy<T> implements InvocationHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Class targetInterface;
    private List<HostAndPort> hostAndPorts;
    private RpcClient client;
    private Registry registry;
    private LoadBalance loadBalance;

    public T createProxy(Class targetInterface) {
        this.targetInterface = targetInterface;
        hostAndPorts = registry.discoverService(targetInterface);
        //监听服务列表 更新服务
        registry.subscribeService(targetInterface,hostAndPorts);
        return (T)Proxy.newProxyInstance(RpcProxy.class.getClassLoader(), new Class[]{targetInterface}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装请求对象
        RpcRequest request = new RpcRequest();
        request.setTargetIntergface(targetInterface);
        request.setMethodName(method.getName());
        request.setParameterType(method.getParameterTypes());
        request.setParamterArgs(args);

        //负载均衡策略
        HostAndPort hostAndPort = loadBalance.loadBalance(hostAndPorts);
        LOGGER.info("Provider : " + hostAndPort.getHost()+":"+hostAndPort.getPort());

        LOGGER.info("To Server request : ");
        LOGGER.info("==> ServiceName : " + request.getTargetIntergface().getName());
        LOGGER.info("==> MethodName : " + request.getMethodName());
        StringBuilder parameterTypes = new StringBuilder();
        for (Class<?> clazz : request.getParameterType()) {
            parameterTypes.append(clazz).append("  ");
        }
        LOGGER.info("==> ParameterTypes : "+parameterTypes);

        StringBuilder parameterArgs = new StringBuilder();
        for (Object o : request.getParamterArgs()) {
            parameterArgs.append(o).append("  ");
        }
        LOGGER.info("==> ParameterArgs : "+parameterArgs);

        RpcResponse response = client.call(request, hostAndPort);

        if(response.getError()!=null){
            throw response.getError();
        }

        client.close();

        return response.getResult();
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
        LOGGER.info("LoadBalance : "+ loadBalance.getClass().getSimpleName());
    }

    public void setClient(RpcClient client) { this.client = client; }
}
