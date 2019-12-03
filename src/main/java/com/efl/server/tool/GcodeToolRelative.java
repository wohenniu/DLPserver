
package com.efl.server.tool;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Component
public class GcodeToolRelative {

    private List<List<String>> Gcodes;

    @Autowired
    private PropertiesTool PropertiesTool;

    private void start(double fallingdistance)
    {
        List<String> startCodes=new ArrayList<>();
        startCodes.add("G21");
        startCodes.add("G91");
        startCodes.add("M17");
        startCodes.add("G1 Z83 F600");
        startCodes.add("G1 Z-60 F400");
        startCodes.add("G1 Y6 F200");
        startCodes.add("G1 Z-"+String.valueOf(fallingdistance-60.0)+" F200");
        Gcodes.add(startCodes);
    }

    private void end()
    {
        List<String> endGcodes=new ArrayList<>();
      //  String s=mainFrame.getEndGcode();
      //  Collections.addAll(endGcodes, s.split("\\n"));
        endGcodes.add("G1 Z30 F200");
        endGcodes.add("G1 Y6.0000 F200");
        endGcodes.add("M18");
        Gcodes.add(endGcodes);
    }

    private void before()
    {
        List<String> beforecodes=new ArrayList<>();
        String s="sas";
        if(s!=null) {
            for (String str : s.split("\\n"))
                beforecodes.add(str);
        }
        Gcodes.add(beforecodes);
    }

    private void after()
    {
        List<String> aftercodes=new ArrayList<>();
        String s="dad";
        if(s!=null) {
            Collections.addAll(aftercodes, s.split("\\n"));
        }
        Gcodes.add(aftercodes);
    }

    public List<List<String>> process(String Filepath) throws IOException {
        PropertiesTool.setFilepath(Filepath);
        PropertiesTool.reading();
        int bottomlayers= PropertiesTool.getBottomlayers();
        int bottomlayerstime=PropertiesTool.getBottomlayerstime();
        double fallingdistance=PropertiesTool.getFallingdistance();
        double layerheight =PropertiesTool.getLayerheight();
        int ordinarylayerstime=PropertiesTool.getOrdinarylayerstime();
        String lightintensity=PropertiesTool.getLightintensity();
        Gcodes=new ArrayList<>();
        if(lightintensity.equals("")||lightintensity==null) {
            lightintensity="15";
        }
        start(fallingdistance);
        before();

       List<String> Gcode=new ArrayList<>();
            Gcode.add("M1 S"+lightintensity);
            Gcode.add("M1 S0");
            Gcode.add("G1 Z-2.5 Y-6 F200");
            Gcode.add("G1 Z5.5 F200");
            Gcode.add("G1 Z"+String.format("%.2f",-3+layerheight)+" F200");
            Gcode.add("G1 Y6 F200");
            Gcodes.add(Gcode);

        after();
        end();
        return Gcodes;
    }
}

