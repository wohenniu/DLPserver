package com.efl.server.config;

import com.efl.server.serialPort.SerialPortA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Configuration
public class Beans {



    @Bean
    public SerialPortA serialPortA(){
        SerialPortA serialPortA=new SerialPortA();
        serialPortA.connect();
        return serialPortA;
    }


}
