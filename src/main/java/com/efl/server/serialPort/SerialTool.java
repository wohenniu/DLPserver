package com.efl.server.serialPort;

import gnu.io.*;
import com.efl.server.serialException.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * 串口服务类，提供打开、关闭串口，读取、发送串口数据等服务（采用单例设计模式）
 * @author zhong
 *
 */
public class SerialTool {




    /**
     * 查找所有可用端口
     * @return 可用端口名称列表
     */

    public static final ArrayList<String> findPort() {

        //获得当前所有可用串口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

        ArrayList<String> portNameList = new ArrayList<>();

        //将可用串口名添加到List并返回该List
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            if (!portName.equals("COM1"))
                portNameList.add(portName);
        }

        return portNameList;

    }

    /**
     * 打开串口
     * @param portName 端口名称
     * @param baudrate 波特率
     * @return 串口对象
     * @throws SerialPortParameterFailure 设置串口参数失败
     * @throws NotASerialPort 端口指向设备不是串口类型
     * @throws NoSuchPort 没有该端口对应的串口设备
     * @throws PortInUse 端口已被占用
     */

    public static final SerialPort openPort(String portName, int baudrate) throws SerialPortParameterFailure, NotASerialPort, NoSuchPort, PortInUse {

        try {

            //通过端口名识别端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            //打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            CommPort commPort = portIdentifier.open(portName, 2000);

            //判断是不是串口
            if (commPort instanceof SerialPort) {

                SerialPort serialPort = (SerialPort) commPort;

                try {
                    //设置一下串口的波特率等参数
                    serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                } catch (UnsupportedCommOperationException e) {
                    throw new SerialPortParameterFailure();
                }

                return serialPort;

            }
            else {
                //不是串口
                throw new NotASerialPort();
            }
        } catch (NoSuchPortException e1) {
            throw new NoSuchPort();
        } catch (PortInUseException e2) {

            throw new PortInUse();
        }

    }

    /**
     * 关闭串口
     * @param serialPort 待关闭的串口对象
     */
    public static void closePort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
        }
    }

    /**
     * 往串口发送数据
     * @param serialPort 串口对象
     * @param order	待发送数据
     * @throws SendDataToSerialPortFailure 向串口发送数据失败
     * @throws SerialPortOutputStreamCloseFailure 关闭串口对象的输出流出错
     */
    public static void sendToPort(SerialPort serialPort,String order) throws SendDataToSerialPortFailure, SerialPortOutputStreamCloseFailure {



            OutputStreamWriter out = null;

            try {

                out = new OutputStreamWriter(serialPort.getOutputStream());

                //DataOutputStream out1 = new DataOutputStream(out);
                out.write(order + "\r");
                out.flush();
                //System.out.println("send:"+order);

            } catch (IOException e) {
                throw new SendDataToSerialPortFailure();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        out = null;
                    }
                } catch (IOException e) {
                    throw new SerialPortOutputStreamCloseFailure();
                }
            }

    }

    /**
     * 从串口读取数据
     * @param serialPort 当前已建立连接的SerialPort对象
     * @return 读取到的数据
     * @throws ReadDataFromSerialPortFailure 从串口读取数据时出错
     * @throws SerialPortInputStreamCloseFailure 关闭串口对象输入流出错
     */

    public static byte[] readFromPort(SerialPort serialPort) throws ReadDataFromSerialPortFailure, SerialPortInputStreamCloseFailure {

        InputStream in = null;
        byte[] bytes = null;
        byte[] res=new byte[0];

        try {

            in = serialPort.getInputStream();

            int bufflenth = in.available();		//获取buffer里的数据长度
            while (bufflenth != 0) {
                byte[] temp=res;

                bytes = new byte[bufflenth];	//初始化byte数组为buffer中数据的长度
                in.read(bytes);
                res=new byte[temp.length+bytes.length];
                System.arraycopy(temp,0,res,0,temp.length);
                System.arraycopy(bytes,0,res,temp.length,bytes.length);
                bufflenth = in.available();
            }
        } catch (IOException e) {
            throw new ReadDataFromSerialPortFailure();
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        }*/ finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch(IOException e) {
                throw new SerialPortInputStreamCloseFailure();
            }

        }

        return res;

    }

    /**
     * 添加监听器
     * @param port     串口对象
     * @param listener 串口监听器
     * @throws TooManyListeners 监听类对象过多
     */
    public static void addListener(SerialPort port, SerialPortEventListener listener) throws TooManyListeners {

        try {

            //给串口添加监听器
            port.addEventListener(listener);
            //设置当有数据到达时唤醒监听接收线程
            port.notifyOnDataAvailable(true);
            //设置当通信中断时唤醒中断线程
            port.notifyOnBreakInterrupt(true);

        } catch (TooManyListenersException e) {
            throw new TooManyListeners();
        }
    }

    /**
     *
     * @return
     */
    public static SerialPort ConnectAuto() {

        String s="start";
        int Baudrate=115200;
      //  System.out.println(s+":"+Baudrate);

        ArrayList<String> comList=findPort();
        SerialPort serialPort=null;
         boolean flag=true;
        for(String comName:comList)
        {
            if(!flag)
                break;
            try {
                serialPort =openPort(comName,Baudrate);
                if(serialPort!=null)
                {
                    byte[] bytes=new byte[1024];
                    InputStream in=serialPort.getInputStream();
                    Thread.sleep(2000);
                    int len=in.read(bytes);
                    String str=new String(bytes,0,len);
                   // System.out.println(str);
                    if(str.startsWith(s))
                        flag=false;
                    in.close();
                }

            } catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | IOException | InterruptedException | PortInUse e) {
                e.printStackTrace();
            } finally {
                if(flag)
                {
                    closePort(serialPort);
                    serialPort=null;
                }
            }
        }
      /*  if(serialPort==null)
            System.out.println("gg");
        else
        System.out.println(serialPort.getName()+"连接成功");*/

        return serialPort;
    }

    /**
     * 检查串口是否连接，返回状态码
     * @param s1 串口1，一般为主板串口
     * @param s2 串口2，一般为传感器串口
     * @return 状态代号，0：串口均无连接；1：串口1无连接；2：串口2无连接；3：串口连接成功
     */
    public static int checkPort(SerialPort s1,SerialPort s2)
    {
        if(s1==null&&s2==null)
            return 0;
        else if(s1==null)
            return 1;
        else if(s2==null)
            return 2;
        else
            return 3;
    }

}
