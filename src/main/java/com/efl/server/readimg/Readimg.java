package com.efl.server.readimg;

//import com.efl.server.print.Print;
import com.efl.server.GUI.PreviewPanel;
import com.efl.server.print.Printing;
import com.efl.server.tool.GcodeToolRelative;
import com.efl.server.tool.PathTool;
import com.efl.server.tool.PropertiesTool;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import com.efl.server.serialPort.*;

@Component
public class Readimg {
    @Autowired
    private GcodeToolRelative gcode;
    @Autowired
    private SerialPortA SerialPortA;
    @Autowired
    private PropertiesTool PropertiesTool;
    @Autowired
    private PathTool p;
    @Autowired
    private Printing   printing;
    private String Filepath;
    private Print print;
    private List<List<String>> Gcodes;
    private Thread printThread;
    private Boolean Linux_flag = true;

    public Thread getPrintThread() {

        return printThread;
    }
    public Print getPrint() {
        return print;
    }
    public void readimg(String Filepath) throws InterruptedException, IOException {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            Linux_flag = false;
        } else {
            Linux_flag = true;
        }
        p.setPath(Filepath, "5");
        if (Linux_flag) {
            String[] Names = Filepath.split("/");             //windows   linux
            String Name = Names[Names.length - 1].replace("_slice", "");
            PropertiesTool.setFilepath(Filepath + "/" + Name + ".properties");
            PropertiesTool.reading();
            int bottomlayers = PropertiesTool.getBottomlayers();
            int bottomlayerstime = PropertiesTool.getBottomlayerstime();
            int ordinarylayerstime = PropertiesTool.getOrdinarylayerstime();
            Gcodes = gcode.process(Filepath + "/" + Name + ".properties");
            print = new Print(Gcodes, SerialPortA, bottomlayers, bottomlayerstime, ordinarylayerstime, Filepath + "/" + Name, p.getNums(), new PreviewPanel(),printing);
            printThread = new Thread(print);
            printThread.start();
        }

       else if (!Linux_flag) {
            String[] Names = Filepath.split("\\\\");             //windows   linux
            String Name = Names[Names.length - 1].replace("_slice", "");
            PropertiesTool.setFilepath(Filepath + "\\" + Name + ".properties");
            PropertiesTool.reading();
            int bottomlayers = PropertiesTool.getBottomlayers();
            int bottomlayerstime = PropertiesTool.getBottomlayerstime();
            int ordinarylayerstime = PropertiesTool.getOrdinarylayerstime();
            Gcodes = gcode.process(Filepath + "\\" + Name + ".properties");
            print = new Print(Gcodes, SerialPortA, bottomlayers, bottomlayerstime, ordinarylayerstime, Filepath + "\\" + Name, p.getNums(), new PreviewPanel(),printing);
            printThread = new Thread(print);
            printThread.start();
        }
    }
}