package com.efl.server.tool;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class PropertiesTool {

    private double layerheight;
    private  double fallingdistance;
    private int bottomlayers;
    private int bottomlayerstime;
    private int ordinarylayerstime;
    private String lightintensity;
    private String Filepath;


    public void setFilepath(String filepath) {
        Filepath = filepath;
    }

    public double getLayerheight() {
        return layerheight;
    }

    public double getFallingdistance() {
        return fallingdistance;
    }

    public int getBottomlayers() {
        return bottomlayers;
    }

    public int getBottomlayerstime() {
        return bottomlayerstime;
    }

    public int getOrdinarylayerstime() {
        return ordinarylayerstime;
    }

    public String getLightintensity() {
        return lightintensity;
    }


    public void reading() throws IOException {
        InputStream ips = new FileInputStream(Filepath);
        Properties props = new Properties();
        props.load(ips);
        ips.close();
        System.out.println(props.toString());
         layerheight =Double.parseDouble(props.getProperty("layerheight"))/1000;
         fallingdistance=Double.parseDouble(props.getProperty("fallingdistance"));
         bottomlayers= Integer.parseInt(props.getProperty("bottomlayers"));
         bottomlayerstime= Integer.parseInt(props.getProperty("bottomlayerstime"));
         ordinarylayerstime= Integer.parseInt(props.getProperty("ordinarylayerstime"));
         lightintensity= props.getProperty("lightintensity");
    }

}
