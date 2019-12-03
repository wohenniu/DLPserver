package com.efl.server.test;

import javax.swing.*;
import java.awt.*;

public class LightTest extends JFrame {
   public LightTest() {
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
        Rectangle gcbound= device[1].getDefaultConfiguration().getBounds();
        int X=(int)gcbound.getX();
        int Y=(int)gcbound.getY();
        int Height=(int)gcbound.getHeight();
        int Width=(int)gcbound.getWidth();
        setSize(Width,Height);
        setLocation(X,Y);
    }
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Image image = new ImageIcon(LightTest.class.getClassLoader().getResource("test.png")).getImage();
        int x=this.getWidth();
        int y=this.getHeight();
        System.out.println("x:"+x+"  y:"+y);
        int imagex=image.getWidth(null);
        int imagey=image.getHeight(null);
        x=(x-imagex)/2;
        y=(y-imagey)/2;
        g.drawImage(image,x,y,imagex,imagey,null);

    }

    public void stop(){
        dispose();
    }
}
