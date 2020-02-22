package com.efl.server.serialPort;

import com.efl.server.serialException.*;
import com.efl.server.serialException.SendDataToSerialPortFailure;
import com.efl.server.serialException.SerialPortOutputStreamCloseFailure;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import lombok.extern.slf4j.Slf4j;


import javax.swing.*;
import java.math.BigDecimal;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class SerialPortA extends SerialportO {

    @Override
    void readData() {

    }

    private boolean flag=true;
    private LinkedBlockingQueue<String> queue;
    private boolean isConnect=false;



    /**
     * 初始化，自动连接指定串口并返回串口对象
     * 添加监听器，当下位机有信息返回时执行
     * @throws TooManyListeners 监听器过多
     * @throws NoPort  没有找到对应端口
     *
     */
    public void setSerialPort(SerialPort serialPort)
    {
        reset();
        this.serialport=serialPort;
    }
    public SerialPortA(){
        queue=new LinkedBlockingQueue<>();

    }


    /**
     * 手动连接
     */

    public void connect()
    {
        reset();
        if (serialport == null) {
            serialport=ConnectAuto();
        }
        if (serialport==null) {
            log.error("没有匹配的串口");
        }
        else {

            log.info(serialport.getName());
            try {
                SerialTool.addListener(serialport,new SerialListener());
                log.info("串口监听添加成功");
            } catch (TooManyListeners tooManyListeners) {
                tooManyListeners.printStackTrace();
            }

        }
    }

    public void close() {
        reset();
        isConnect=false;
        try {
            queue.put("byebye");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SerialTool.closePort(serialport);
        serialport = null;
    }

    /**
     * 获取串口对象
     * @return 串口对象
     */
    @Override
    public SerialPort getSerialport() {
        return serialport;
    }


    /**
     *  向串口发送信息
     * @param order 待发送数据
     * @throws SerialPortOutputStreamCloseFailure 数据发送失败
     * @throws SendDataToSerialPortFailure  关闭串口对象的输出流出错
     */
    @Override
    public void sendToPort(String order)  {
        try {
            sendToPortWithException(order);
        } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
            sendDataToSerialPortFailure.printStackTrace();
        }
    }

    public void sendToPortWithException(String order) throws SerialPortOutputStreamCloseFailure, SendDataToSerialPortFailure {


        if(flag) {
            synchronized(this){
            if(flag&&serialport!=null)
            {
                SerialTool.sendToPort(serialport,order);
                flag=false;
            }

        }
        } else {
            try {
                queue.put(order);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 从串口读取数据
     * @return 读取到的数据
     * @throws ReadDataFromSerialPortFailure 从串口读取数据时出错
     * @throws SerialPortInputStreamCloseFailure    关闭串口对象输入流出错
     */
    @Override
    public byte[] readFromPort() {
        byte[] data=null;
        try {
           data= SerialTool.readFromPort(serialport);
        } catch (ReadDataFromSerialPortFailure readDataFromSerialPortFailure) {
            readDataFromSerialPortFailure.printStackTrace();

        } catch (SerialPortInputStreamCloseFailure serialPortInputStreamCloseFailure) {
            serialPortInputStreamCloseFailure.printStackTrace();
        }
            return data;
    }



    /**
     * 自动连接串口并返回串口对象
     * @return 串口对象
     */

    @Override
    SerialPort ConnectAuto() {
        return SerialTool.ConnectAuto();
    }

    /**
     * 清空队列
     */
    public void reset(){
        isConnect=true;
        queue.clear();
    }


    public SerialPortEventListener getListener()
    {
        return new SerialListener();
    }
    /**
     * 监听器，当下位机返回数据时执行
     */
      private class SerialListener implements SerialPortEventListener{

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {

            switch (serialPortEvent.getEventType()) {

                case SerialPortEvent.BI: // 10 通讯中断
                    JOptionPane.showMessageDialog(null, "与串口设备通讯中断", "错误", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case SerialPortEvent.OE: // 7 溢位（溢出）错误

                case SerialPortEvent.FE: // 9 帧错误

                case SerialPortEvent.PE: // 8 奇偶校验错误

                case SerialPortEvent.CD: // 6 载波检测

                case SerialPortEvent.CTS: // 3 清除待发送数据

                case SerialPortEvent.DSR: // 4 待发送数据准备好了

                case SerialPortEvent.RI: // 5 振铃指示

                case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
                    break;

                case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
                {
                    String str=new String();
                    byte[] data = null;

                    while(!str.endsWith("\n")){
                        data=readFromPort();
                        str+=new String(data);
                    }
                    log.info("打印机发来信息： "+str);
                    if(isConnect&&( str.indexOf("ok")!=-1||str.indexOf("OK")!=-1) ){
                        try {
                            SerialTool.sendToPort(serialport,queue.take());
                        } catch (SendDataToSerialPortFailure sendDataToSerialPortFailure) {
                            sendDataToSerialPortFailure.printStackTrace();
                        } catch (SerialPortOutputStreamCloseFailure serialPortOutputStreamCloseFailure) {
                            serialPortOutputStreamCloseFailure.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }



            }
        }
    }

}
