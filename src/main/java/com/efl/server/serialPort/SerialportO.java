package com.efl.server.serialPort;

import com.efl.server.serialException.*;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.util.ArrayList;

public abstract class SerialportO {




    abstract void  sendToPort(String order) throws SerialPortOutputStreamCloseFailure, SendDataToSerialPortFailure, IOException;  //发送串口信息
    abstract byte[] readFromPort() throws ReadDataFromSerialPortFailure, SerialPortInputStreamCloseFailure; //读串口信息
    abstract SerialPort ConnectAuto() throws PortInUse;  //自动连接串口
    abstract void readData() throws SendDataToSerialPortFailure, SerialPortOutputStreamCloseFailure;
    abstract SerialPort getSerialport();

    protected SerialPort serialport;

    public String getName()
    {
        if(serialport!=null)
        return serialport.getName();
        return null;
    }
    /**
     * 查找所有可用端口
     * @return 可用端口名称列表
     */
    public ArrayList<String> findPort(){
        return SerialTool.findPort();
    }

    /**
     * 打开指定串口
     * @param sportName 端口名称
     * @param Baudrate  波特率
     * @return 串口对象
     * @throws SerialPortParameterFailure 设置串口参数失败
     * @throws NoSuchPort 端口指向设备不是串口类型
     * @throws PortInUse 没有该端口对应的串口设备
     * @throws NotASerialPort 端口已被占用
     */
    public SerialPort openPort(String sportName,int Baudrate) throws SerialPortParameterFailure, NoSuchPort, PortInUse, NotASerialPort {

            return SerialTool.openPort(sportName,Baudrate);
    }

    /**
     *关闭串口
     * @param serialPort 待关闭的串口对象
     */
    public void closePort(SerialPort serialPort)
    {
        SerialTool.closePort(serialPort);
    }

    /**
     *  添加监听器
     * @param port  串口对象
     * @param listener 串口监听器
     * @throws TooManyListeners 监听类对象过多
     */
    public void addListener(SerialPort port, SerialPortEventListener listener) throws TooManyListeners {
        SerialTool.addListener(port,listener);
    }



}
