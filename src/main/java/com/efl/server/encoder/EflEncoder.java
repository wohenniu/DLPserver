package com.efl.server.encoder;

import com.efl.server.agreement.EflMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
public class EflEncoder extends MessageToByteEncoder<EflMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, EflMessage msg, ByteBuf byteBuf) throws Exception {
        //消息头+消息长度+类型+内容
        byteBuf.writeInt(msg.getHeader());
        byteBuf.writeInt(msg.getContentLength());
        byteBuf.writeInt(msg.getType());
        byteBuf.writeBytes(msg.getContent());
    }
}











