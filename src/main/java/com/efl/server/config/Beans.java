package com.efl.server.config;

import com.efl.server.serialPort.SerialPortA;
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
