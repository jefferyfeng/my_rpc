package com.fdh.rpc.server;

import com.fdh.rpc.protocol.HostAndPort;
import com.fdh.rpc.protocol.RpcRequest;
import com.fdh.rpc.protocol.RpcResponse;
import com.fdh.rpc.registry.Registry;
import com.fdh.rpc.serialize.CustomMessageToMessageDecoder;
import com.fdh.rpc.serialize.CustomMessageToMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NettyProvider
 *
 * @author Jeffery
 */
public class NettyProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    private Registry registry;
    private HostAndPort hostAndPort;

    private Map<Class,Object> beanMap;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(20);


    public NettyProvider(Registry registry,int port) {
        this.registry = registry;
        try {
            //hostAndPort = new HostAndPort(InetAddress.getLocalHost().getHostAddress(),port);
            hostAndPort = new HostAndPort("127.0.0.1",port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //创建启动引导
        serverBootstrap = new ServerBootstrap();
        //创建线程池组
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        //配置线程池组
        serverBootstrap.group(boss,worker);
        //设置服务器实现
        serverBootstrap.channel(NioServerSocketChannel.class);
    }

    public void start(){
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                //初始化通信管道
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                        pipeline.addLast(new CustomMessageToMessageDecoder());
                        pipeline.addLast(new LengthFieldPrepender(2));
                        pipeline.addLast(new CustomMessageToMessageEncoder());
                        pipeline.addLast(new ChannelHandlerAdapter(){
                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                LOGGER.error("Error："+cause.getCause());
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                LOGGER.info("Current Client : "+ctx.channel().remoteAddress().toString().substring(1));
                                LOGGER.info("From Client Request : ");

                                RpcRequest request = (RpcRequest) msg;
                                LOGGER.info("==> ServiceName : " + request.getTargetIntergface().getName());
                                LOGGER.info("==> MethodName : " + request.getMethodName());

                                StringBuilder parameterTypes = new StringBuilder();
                                for (Class<?> clazz : request.getParameterType()) {
                                    parameterTypes.append(clazz).append("  ");
                                }
                                LOGGER.info("==> ParameterTypes : "+ parameterTypes);

                                StringBuilder parameterArgs = new StringBuilder();
                                for (Object o : request.getParamterArgs()) {
                                    parameterArgs.append(o).append("  ");
                                }
                                LOGGER.info("==> ParameterArgs : " + parameterArgs);

                                Object o = beanMap.get(request.getTargetIntergface());
                                Method method = o.getClass().getMethod(request.getMethodName(), request.getParameterType());
                                RpcResponse response = new RpcResponse();
                                try {
                                    Object result = method.invoke(o, request.getParamterArgs());
                                    response.setResult(result);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    response.setError(e);
                                }

                                LOGGER.info("To Client response : ");
                                LOGGER.info("==> Result : "+response.getResult());
                                LOGGER.info("==> Error : "+response.getError());

                                ChannelFuture channelFuture = ctx.writeAndFlush(response);

                                channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                                channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                                //关闭通道（由服务器端关闭）
                                channelFuture.addListener(ChannelFutureListener.CLOSE);
                            }
                        });
                    }
                });
                try {
                    //绑定监听端口
                    ChannelFuture channelFuture = serverBootstrap.bind(hostAndPort.getHost(),hostAndPort.getPort()).sync();
                    LOGGER.info("Server waiting..");
                    channelFuture.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //注册服务
        for (Class clazz : beanMap.keySet()) {
            try {
                registry.registService(clazz, hostAndPort);
                LOGGER.info("Service "+clazz.getName()+" regist success！");
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("Service "+clazz.getName()+" regist error : "+e.getMessage());
            }
        }
    }

    public void setBeanMap(Map<Class, Object> beanMap) {
        this.beanMap = beanMap;
    }

    public void close(){
        registry.close();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
