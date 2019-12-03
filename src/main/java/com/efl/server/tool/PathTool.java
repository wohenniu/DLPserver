package com.efl.server.tool;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PathTool {

    private String path;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path+"/"+name;
    }

    public void setPath(String path,String name) {
        this.path = path;
        this.name=name;
    }

    public int getNums() {                //获取图片数目
        if(path==null)
            return 0;
        File file=new File(path);
        if(file.exists())
        {
            File[] files=file.listFiles();
            return files.length-1;
        }else
            return 0;
    }

}
