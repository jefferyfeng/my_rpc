package com.fdh.rpc.protocol;

import java.io.Serializable;

/**
 * 请求的数据对象
 *
 * @author Jeffery
 */
public class RpcRequest implements Serializable{
    private Class<?> targetIntergface;
    private String methodName;
    private Class<?>[] parameterType;
    private Object[] paramterArgs;

    public Class<?> getTargetIntergface() {
        return targetIntergface;
    }

    public void setTargetIntergface(Class<?> targetIntergface) {
        this.targetIntergface = targetIntergface;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?>[] parameterType) {
        this.parameterType = parameterType;
    }

    public Object[] getParamterArgs() {
        return paramterArgs;
    }

    public void setParamterArgs(Object[] paramterArgs) {
        this.paramterArgs = paramterArgs;
    }
}
