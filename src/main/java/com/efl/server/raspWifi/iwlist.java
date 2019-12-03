package com.efl.server.raspWifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class iwlist{
    private String example="(?<=ESSID\\:\")(.*?)(?=\")";
    private Pattern pa= Pattern.compile(example);
    private static Process p = null;
    private ArrayList<String> WIFInames=new ArrayList<String>();
  //  public String[] WIFInames=null;

    public ArrayList<String> getWIFInames() {
        return WIFInames;
    }

    public iwlist() throws InterruptedException, IOException {
        p = Runtime.getRuntime().exec("sudo iwlist wlan0 scan");
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        //StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            Matcher matcher1 = pa.matcher(line);
            if(matcher1.find())
           {line=matcher1.group();
           WIFInames.add(line);}
        }
//        String result = sb.toString();
//        FileWriter writer;
//        writer = new FileWriter("/home/pi/Desktop/inf.conf");
//        writer.write(result);
//        writer.flush();
//        writer.close();
    }
}
