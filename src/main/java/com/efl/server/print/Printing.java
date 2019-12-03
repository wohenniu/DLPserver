package com.efl.server.print;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class Printing extends JFrame {

    private Image image;

    {
        getContentPane().setBackground(Color.BLACK);
        setAlwaysOnTop(true);
        toFront();
        setUndecorated(true);
        setVisible(true);
        Toolkit tk=Toolkit.getDefaultToolkit();
        Image img=tk.getImage("");
        Cursor cu=tk.createCustomCursor(img,new Point(0,0),"stick");
        this.setCursor(cu);
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] device = environment.getScreenDevices();
        Rectangle gcbound= device[0].getDefaultConfiguration().getBounds();
        int X=(int)gcbound.getX();
        int Y=(int)gcbound.getY();
        int Height=(int)gcbound.getHeight();
        int Width=(int)gcbound.getWidth();
        this.setSize(Width,Height);
        this.setLocation(X,Y);
    }
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if(image!=null) {
            int x = this.getWidth();
            int y = this.getHeight();
            int imagex = image.getWidth(null);
            int imagey = image.getHeight(null);
            x = (x - imagex) / 2;
            y = (y - imagey) / 2;
            g.drawImage(image, x, y, imagex, imagey, null);
        }
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

}
