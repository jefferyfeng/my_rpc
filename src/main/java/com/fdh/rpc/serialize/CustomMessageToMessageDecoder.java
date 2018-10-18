package com.fdh.rpc.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 解码 将byteBuf类型的对象解码为Object
 *
 * @author Jeffery
 */
public class CustomMessageToMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        LOGGER.info("Decode..");

        //创建bytes数组用来存储序列化后的对象的数据
        int i = byteBuf.readableBytes();
        byte[] bytes = new byte[i];
        byteBuf.readBytes(bytes);

        //解码回Object对象
        Object o = SerializationUtils.deserialize(bytes);

        //将解码数据放入数据帧中
        list.add(o);
    }
}
