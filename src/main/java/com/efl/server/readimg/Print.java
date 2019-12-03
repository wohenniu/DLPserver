package com.efl.server.readimg;


import  com.efl.server.GUI.PreviewPanel;
import com.efl.server.print.Printing;
import com.efl.server.serialException.SendDataToSerialPortFailure;
import com.efl.server.serialException.SerialPortOutputStreamCloseFailure;
import com.efl.server.serialPort.SerialPortA ;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;



public class Print  implements Runnable, ActionListener {
    private List<List<String>> Gcode;
    private SerialPortA serialPort;
    private PreviewPanel previewPanel;
    private String path;
    private int layerNum;
    private int index;
    private int time1;//基层曝光时间
    private int time2;//片层曝光时间
    private int bottomlayers;  //基层层数
    private boolean flag;
    private List<String> startCodes;
    private List<String> beforeCodes;
    private List<String> processCodes;
    private List<String> afterCodes;
    private List<String> endGodes;
    private JLabel progressLabel;
    private final int delay = 500;
    private Timer timer = new Timer(delay,this);
    private long startTime;
    private JProgressBar printProgressBar;
    private DecimalFormat pngNameNumFormat = new DecimalFormat("0000");
    private Printing printing;
    public void setPrintProgressBar(JProgressBar printProgressBar) {
        this.printProgressBar = printProgressBar;
        printProgressBar.setMinimum(0);
        printProgressBar.setMaximum(layerNum);
    }


    public Print(List<List<String>> Gcode, SerialPortA serialPort, int bottomlayers , int bottomlayerstime, int ordinarylayerstime, String path, int layerNum, PreviewPanel previewPanel, Printing printing)
    {
        this.layerNum = layerNum;
        System.out.println("层数:"+layerNum);
        this.path=path;
        this.bottomlayers=bottomlayers;
        this.time1=bottomlayerstime;
        this.time2=ordinarylayerstime;
        this.serialPort=serialPort;
        this.Gcode=Gcode;
        startCodes=Gcode.get(0);
        beforeCodes=Gcode.get(1);
        processCodes=Gcode.get(2);
        System.out.println(processCodes);
        afterCodes=Gcode.get(3);
        endGodes=Gcode.get(4);
        this.previewPanel = previewPanel;
       this.printing=printing;
        flag=false;
    }


    public void setProgressLabel(JLabel progressLabel) {
        this.progressLabel = progressLabel;
    }


    @Override
    public void run(){
        System.out.println("打印开始");
        printProgressBar.setValue(0);
       // timer.start();
        try {
            //打印前
           for(int j=0;j<7;j++)
           {
               System.out.println(startCodes.get(j));
               serialPort.sendToPortWithException(startCodes.get(j));
           }
            Thread.sleep(30000);
            //打印中
            while (index <layerNum) {
                int i=0;
                Image image = new ImageIcon(path+pngNameNumFormat.format(index)+".png").getImage();
                printing.setImage(image);
                //Thread.sleep(1000);
                System.out.println(processCodes.get(i));
                serialPort.sendToPortWithException(processCodes.get(i++));
                if (index < bottomlayers)
                    Thread.sleep(time1);//基层
                else
                    Thread.sleep(time2);//片层
                for (int j = 0; j < 4; j++) {
                System.out.println(processCodes.get(i));
                serialPort.sendToPortWithException(processCodes.get(i++)); }
                System.out.println(processCodes.get(i));
                serialPort.sendToPortWithException(processCodes.get(i++));
                Thread.sleep(8000);
                index++;
                if (printProgressBar != null)
                    printProgressBar.setValue(index);
            }
            //打印后
            for(int j=0;j<endGodes.size();j++)
            {
                serialPort.sendToPortWithException(endGodes.get(j));
                System.out.println(endGodes.get(j));
            }
        } catch (InterruptedException e) {
            System.out.println("打印中断");
        } catch (SerialPortOutputStreamCloseFailure | SendDataToSerialPortFailure serialPortException) {
            System.out.println(serialPortException.getMessage());
        }

        serialPort.sendToPort("M1 S0");//关闭光机
       // timer.stop();

      //  String time = progressLabel.getText();
      //  progressLabel.setText("用时"+time.substring(2,time.indexOf("层"))+"打印结束");
      //  previewPanel.paintImage(null);
         System.out.println("end");
       // mainFrame.setSlider(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (progressLabel != null) {
            //long currentTime = System.currentTimeMillis();
            long totalS = (System.currentTimeMillis() - startTime) / 1000;
            int ms = (int) (System.currentTimeMillis() - startTime) % 1000;
            int hh = (int) (totalS / 3600);
            int mm = (int) (totalS % 3600 / 60);
            int ss = (int) (totalS % 3600 % 60);
            int gap = 2;//每30min调用垃圾回收,最少为2min
            progressLabel.setText("已用 "+hh+"时"+mm+"分"+ss+"秒 层："+index+"/"+layerNum);
            //System.out.println("time:"+hh+":"+mm+":"+ss+"."+ms);
            if (mm % gap == 0 && ss == 0 && ms < delay) {
                System.gc();
                System.out.println("调用回收");
            }
        }
    }

}
