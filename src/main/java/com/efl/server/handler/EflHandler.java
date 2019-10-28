package com.efl.server.handler;

import com.efl.server.agreement.ConstantValue;
import com.efl.server.agreement.EflMessage;
import com.efl.server.dlpServer.DlpServer;
import com.efl.server.print.Print;
import com.efl.server.serialPort.SerialPortA;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
@Component
@ChannelHandler.Sharable
public class EflHandler extends SimpleChannelInboundHandler<EflMessage> {

    @Autowired
    SerialPortA serialPortA;

    @Autowired
    Print print;
    @Autowired
    DlpServer dlpServer;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress ipSocket = (InetSocketAddress)ctx.channel().remoteAddress();
        String clientIp = ipSocket.getAddress().getHostAddress();
        if(dlpServer.getSize()==0){
            dlpServer.put(clientIp,ctx.channel());
            log.info("连接成功："+clientIp);
            String s="服务端连接成功";
            ctx.writeAndFlush(new EflMessage(s.getBytes().length,ConstantValue.STRING,s.getBytes()));
        }else {
            String s = "服务端已存在连接";
            ctx.writeAndFlush(new EflMessage(s.getBytes().length, ConstantValue.STRING, s.getBytes()));
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress ipSocket = (InetSocketAddress)ctx.channel().remoteAddress();
        String clientIp = ipSocket.getAddress().getHostAddress();
        log.info("断开连接："+clientIp);
        dlpServer.removeChannel(clientIp);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,EflMessage eflMessage) throws Exception {
        if(eflMessage.getType()== ConstantValue.STRING){
            String s=new String(eflMessage.getContent());
            log.info("收到客户端消息："+s);
            serialPortA.sendToPortWithException(s);
        }else if(eflMessage.getType()==ConstantValue.IMAGE) {
            Image img = convertToImage(eflMessage.getContent());
            print.setImage(img);
        }
    }


    private Image convertToImage(byte[] bytes){
        BufferedImage bufferedImage= null;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

}
