package com.efl.server.config;

import com.efl.server.UI.MoveButton1D;
import com.efl.server.UI.ServerUI;
import com.efl.server.readimg.Print;
import com.efl.server.serialPort.SerialPortA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

    @Bean
    public SerialPortA serialPortA(){
        SerialPortA serialPortA=new SerialPortA();
        serialPortA.connect();
        return serialPortA;
    }
}
