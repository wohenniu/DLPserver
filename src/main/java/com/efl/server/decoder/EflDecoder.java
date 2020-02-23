package com.efl.server.decoder;

import com.efl.server.agreement.ConstantValue;
import com.efl.server.agreement.EflMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.stereotype.Component;

import java.util.List;


public class EflDecoder extends ByteToMessageDecoder {
    public final int BASE_LENGTH = 8;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {

        if (in.readableBytes() >= BASE_LENGTH) {

            // 记录包头开始的index
            int beginReader;
            while (true) {
                // 获取包头开始的index
                beginReader = in.readerIndex();
                // 标记包头开始的index
                in.markReaderIndex();

                // 尝试读出一个Int，如读到协议头的开始标志，结束while循环，进行后续信息处理
                if (in.readInt() == ConstantValue.HEAD_DATA) {
                    break;
                }

                //读到的不符合包头，回撤ReaderIndex
                in.resetReaderIndex();
                //跳过一个字节，再去读取包头信息的开始标记
                in.readByte();

                // 当跳过一个字节后，数据包的长度又变的不满足，此时应该结束，等待后边数据流的到达
                if (in.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }

            // 代码到这里，说明已经读完了消息头并匹配成功

            // 读取消息长度
            int length = in.readInt();
            // 判断请求数据包是否到齐，若数据不齐，回退读指针至 开始读取消息头的位置
            if (in.readableBytes() < length) {
                // 还原读指针
                in.readerIndex(beginReader);
                return;
            }

            // 至此，可以读取一条完整报文

            // 读取数据的类型（图片 或 G代码）
            int type=in.readInt();
            // 开辟字节数组，读取消息内容
            byte[] data = new byte[length-4];
            in.readBytes(data);
            //封装eflMessage对象
            EflMessage eflMessage = new EflMessage(length,type,data);
            //加入list，传给下一个handler
            list.add(eflMessage);
        }
    }
}
