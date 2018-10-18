package com.fdh.rpc.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * 解码 将Object类型的对象编码为byteBuf
 *
 * @author Jeffery
 */
public class CustomMessageToMessageEncoder extends MessageToMessageEncoder<Object> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
        LOGGER.info("Encode..");

        //获取byteBuf对象
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();

        //将对象序列后的数据写入buf中
        byte[] bytes = SerializationUtils.serialize((Serializable) o);
        byteBuf.writeBytes(bytes);

        //存入数据帧
        list.add(byteBuf);
    }
}
