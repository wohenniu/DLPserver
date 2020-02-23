package com.efl.server;

import com.efl.server.UI.ServerUI;
import com.efl.server.dlpServer.DlpServer;
import com.efl.server.test.LightTest;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 */
@SpringBootApplication
@Slf4j
public class ServerApplication implements CommandLineRunner {
    @Autowired
    ServerUI serverUI;

    @Autowired
    DlpServer dlpServer;

    @Value("${netty.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        JFrame frame = new JFrame();
        frame.setTitle("DLP");
        frame.setContentPane(serverUI.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();//调整此窗口的大小，以适合其子组件的首选大小和布局。
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);//居中
        frame.setVisible(true);
       // Image image=new ImageIcon(LightTest.class.getClassLoader().getResource("图标.png")).getImage(); /*image.gif是你的图标*/
       // frame.setIconImage(image);
        frame.addWindowListener(new WindowAdapter() {        //检测到主窗口关闭
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    //   serverUI.getReadimg().getPrintThread().interrupt();
                } catch (Exception e1) {
                    System.out.println(e1.getMessage());

                }
                serverUI.portClose();                       //串口关闭
                super.windowClosing(e);
                log.info("窗口关闭");
            }
        });

        ChannelFuture future = dlpServer.start(port);
        //通过addshut..向jvm虚拟机注册钩子事件，在jvm关闭之前，运行线程hook，做一些未完成的事情
        Runtime.getRuntime().addShutdownHook(new Thread() {  //在jvm销毁之前关闭线程池
            @Override
            public void run() {
                dlpServer.destroy();
            }
        });
        //服务端管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程
        future.channel().closeFuture().syncUninterruptibly();

    }
}
