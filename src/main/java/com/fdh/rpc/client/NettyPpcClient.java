package com.fdh.rpc.client;

import com.fdh.rpc.protocol.HostAndPort;
import com.fdh.rpc.protocol.RpcClient;
import com.fdh.rpc.protocol.RpcRequest;
import com.fdh.rpc.protocol.RpcResponse;
import com.fdh.rpc.serialize.CustomMessageToMessageDecoder;
import com.fdh.rpc.serialize.CustomMessageToMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * rpcClient实现
 * NettpRpcClent
 *
 * @author jeffery
 */
public class NettyPpcClient implements RpcClient{

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Bootstrap bootstrap;
    private NioEventLoopGroup worker;

    public NettyPpcClient() {
        //创建服务启动引导
        bootstrap = new Bootstrap();
        //创建线程池组
        worker = new NioEventLoopGroup();
        //配置线程池组
        bootstrap.group(worker);
        //设置服务器实现
        bootstrap.channel(NioSocketChannel.class);
    }

    @Override
    public RpcResponse call(final RpcRequest request, HostAndPort hostAndPort) {
        //由于局部内部类访问外部成员变量时必须设置为final类型的，因此可以创建集合来装局部内部类的数据
        final List<RpcResponse> responses = new ArrayList<RpcResponse>();
        //初始化管道
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                //配置编解码 追加两个字节的前缀
                pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                pipeline.addLast(new CustomMessageToMessageDecoder());
                pipeline.addLast(new LengthFieldPrepender(2));
                pipeline.addLast(new CustomMessageToMessageEncoder());

                pipeline.addLast(new ChannelHandlerAdapter(){
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        LOGGER.info("Error : "+cause.getCause());
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ctx.writeAndFlush(request);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        LOGGER.info("From Server response :");
                        RpcResponse response = (RpcResponse) msg;
                        LOGGER.info("==> Result : "+response.getResult());
                        LOGGER.info("==> Error : "+response.getError());
                        responses.add(response);
                    }
                });
            }
        });
        LOGGER.info("Client request..");
        try {
            //连接端口启动服务
            ChannelFuture channelFuture = bootstrap.connect(hostAndPort.getHost(), hostAndPort.getPort()).sync();
            //关闭SocketChannel
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return responses.get(0);
    }

    @Override
    public void close() {
        //关闭线程资源
        worker.shutdownGracefully();
    }
}
