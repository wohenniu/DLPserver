package com.efl.server.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * 用于预览打印中投屏的图片
 */
public class PreviewPanel extends JPanel {

    /**
     * panel里面需要显示的图片
     */
    private Image image;

    /**
     * 设置需要显示的图片
     * @param image 需要显示的图片
     */
    public void paintImage(Image image) {
        this.image = image;
        repaint();
    }

    /**
     * 将图片居中显示，并缩放到最大
     * @param g Graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int x = 0, y = 0;
            int width = this.getWidth(), height = this.getHeight();
            float fAspect = (float)image.getWidth(null)/image.getHeight(null);
            if ((float) width/height > fAspect){
                width = (int) (height * fAspect);
                x = (this.getWidth() - width)/2;
            }else{
                height = (int) (width / fAspect);
                y = (this.getHeight() - height) / 2;
            }
            g.drawImage(image, x, y, width, height, null);
        }
    }


}
