package com.efl.server;

import com.efl.server.UI.ServerUI;
import com.efl.server.dlpServer.DlpServer;
import com.efl.server.tool.PathTool;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import javax.swing.*;
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


    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        //System.out.println(serverUI);
        String Filepath=serverUI.getFilepath();
        JFrame frame = new JFrame();
        frame.setTitle("DLP");
        frame.setContentPane(serverUI.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();//调整此窗口的大小，以适合其子组件的首选大小和布局。
        frame.setLocationRelativeTo(null);//居中
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {        //检测到主窗口关闭
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    //   serverUI.getReadimg().getPrintThread().interrupt();
                } catch (Exception e1) {
                    System.out.println(e1.getMessage());

                }
                serverUI.portClose();                       //串口关闭
                log.info("窗口应该关闭");
                super.windowClosing(e);
            }
        });

    }
}
