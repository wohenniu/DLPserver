package com.efl.server.dlpServer;

import com.efl.server.decoder.EflDecoder;
import com.efl.server.encoder.EflEncoder;
//import com.efl.server.handler.DlpHandler;
import com.efl.server.handler.EflHandler;
//import com.efl.server.print.Print;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class DlpServer {
    /**
     * NioEventLoop并不是一个纯粹的I/O线程，它除了负责I/O的读写之外
     * 创建了两个NioEventLoopGroup，
     * 它们实际是两个独立的Reactor线程池。
     * 一个用于接收客户端的TCP连接，
     * 另一个用于处理I/O相关的读写操作，或者执行系统Task、定时任务Task等。
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(5);
    private ConcurrentHashMap<String,Channel> concurrentHashMap=new ConcurrentHashMap<>();
    private Channel channel;



//    @Autowired
//    Print print;
    @Autowired
    private EflHandler eflHandler;

    public ChannelFuture start(String host,int port){

        ChannelFuture f = null;
        try {
            //ServerBootstrap负责初始化netty服务器，并且开始监听端口的socket请求
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(host,port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            为监听客户端read/write事件的Channel添加用户自定义的ChannelHandler
                            socketChannel.pipeline()
                                    .addLast("decoder", new EflDecoder())
                                    .addLast("encoder", new EflEncoder())
                                    .addLast(eflHandler);
                        }
                    });

            f = b.bind().sync();
            channel = f.channel();
            log.info("======EchoServer启动成功!!!=========");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (f != null && f.isSuccess()) {
                log.info("Netty server listening " + host + " on port " + port + " and ready for connections...");
            } else {
                log.error("Netty server start up Error!");
            }
        }
        return f;
    }

    /**
     * 停止服务
     */
    public void destroy() {
        log.info("Shutdown Netty Server...");
        if(channel != null) { channel.close();}
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
     //   print.dispose();
        log.info("Shutdown Netty Server Success!");
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void put(String s,Channel channel){
        concurrentHashMap.put(s,channel);
    }
    public int getSize(){
        return concurrentHashMap.size();
    }
    public Channel getChannel(String s){
        return concurrentHashMap.get(s);
    }
    public void removeChannel(String s){
        concurrentHashMap.remove(s);
    }
}
