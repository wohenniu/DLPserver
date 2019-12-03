//package com.efl.server.config;
//
//import com.efl.server.UI.ServerUI;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//
//@Configuration
//public class Bean2 {
//
//    @Autowired
//    ServerUI serverUI;
//    @Bean
//    public JFrame jFrame(){
//        JFrame frame=new JFrame();
//        frame.setTitle("DLP");
//        frame.setContentPane(serverUI.getPanel());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();//调整此窗口的大小，以适合其子组件的首选大小和布局。
//        frame.setLocationRelativeTo(null);//居中
//        frame.setVisible(true);
//        frame.addWindowListener(new WindowAdapter() {        //检测到主窗口关闭
//            @Override
//            public void windowClosing(WindowEvent e) {
//                try {
//               //   serverUI.getReadimg().getPrintThread().interrupt();
//                } catch (Exception e1) {
//                    System.out.println(e1.getMessage());
//                }
//                serverUI.portClose();                       //串口关闭
//                super.windowClosing(e);
//
//            }
//        });
//        return  frame;
//    }
//}
