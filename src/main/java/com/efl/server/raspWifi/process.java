package com.efl.server.raspWifi;

import java.io.FileWriter;
import java.io.IOException;


public class process {
    private static Process p = null;

    public static Process getP() {
        return p;
    }

    public process(String WIFI, String password ) throws InterruptedException, IOException {
        String str="ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev\n" +
                "update_config=1\n" +
                "country=CN\n" +
                "\n" +
                "network={\n" +
                "\tssid=\""+WIFI+"\"\n" +
                "\tpsk=\""+password+"\"\n" +
                "\tkey_mgmt=WPA-PSK\n" +
                "}\n";
        FileWriter writer;
        try {
            writer = new FileWriter("/home/pi/Desktop/wpa_supplicant.conf");
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        p = Runtime.getRuntime().exec("sudo cp wpa_supplicant.conf /etc/wpa_supplicant");
        System.out.println("WIFI配置文件就绪....");
        p = Runtime.getRuntime().exec("sudo wpa_cli -i wlan0 reconfigure");
        p.waitFor();
        System.out.println("----配置文件生效----");
    }


    //        p = Runtime.getRuntime().exec("sudo cp wpa_supplicant.conf /etc/wpa_supplicant");
//        p.waitFor();
//        System.out.println("WIFI配置文件就绪....");
//        p = Runtime.getRuntime().exec("sudo wpa_cli -i wlan0 reconfigure");
//        p.waitFor();
//        System.out.println("----配置文件生效----");


//                String command1 = "sudo cp wpa_supplicant.conf /etc/wpa_supplicant";
//                String command2 = "sudo wpa_cli -i wlan0 reconfigure";
//                String message1 = ShellUtil.runShell(command1);
//                String message2 = ShellUtil.runShell(command2);
//                System.out.println(message1);
//                System.out.println(message2);

        }


