package com.efl.server.UI;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

/**
 * 一维的多组命令控制按钮，通过实现MouseMotionListener的方法用于随着鼠标位置的移动更改命令的选择
 * @author EFL_tjl
 * @version 1.0
 */

public class MoveButton1D extends JComponent implements MouseMotionListener {
    private Area up, down;
    private Color colorNull = new Color(238,238,238);
    private Color colorOut = new Color(84,195,223);
    private Color colorIn = new Color(204,237,245);
    private Color linesAndText = new Color(80,85,97);
    private Color[] defaultColor;
    private Color[] changeColor;
    private Font font;
    private FontMetrics metrics;
    private BasicStroke stroke1;
    private BasicStroke stroke2;
    private LinearGradientPaint linearPaint_up, linearPaint_down;

    private int textAreaHeight = 45;//字体高度
    private int buttonHeight = 180;//总高度
    private int buttonWidth = 100;//总宽
    private int roundSize = 40;//圆角大小
    private int offsetX = 2;//
    private int offsetY = 2;//
    private int triangleSize = 8;//三角形大小
    private float[] fractions;//渐变区域 的比率 默认0%到100%
    private float[] fractionsColor;

    //两个三角形
    private int[] triangle_x;
    private int[] triangleUp_y;
    private int[] triangleDn_y;

    private Object[] upOrders;
    private Object[] dnOrders;
    private int orderIndex;

    private String order;
    private String text;
    private String textDefault;

    {
        defaultColor = new Color[]{colorNull, colorNull};
        changeColor = new Color[]{colorIn, colorOut, colorNull, colorNull};
        font= new Font("等线",Font.BOLD,18);
        stroke1 = new BasicStroke(2.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        stroke2 = new BasicStroke(1.6f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        fractions = new float[]{0, 1};
        fractionsColor = new float[]{0, 0.5f, 0.51f, 1};
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                DefaultSet();
                repaint();
            }
        });
        addMouseMotionListener(this);
    }

    public MoveButton1D(Object[] upOrders, Object[] dnOrders){
        this(upOrders, dnOrders, " ");
    }

    public MoveButton1D(Object[] upOrders, Object[] dnOrders, String textDefault) {
        this.textDefault = textDefault;
        this.upOrders = upOrders;
        this.dnOrders = dnOrders;
        DefaultSet();
        createShapes();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    private Point mousePoint3D;
    @Override
    public void mouseMoved(MouseEvent e) {
        if (isEnabled()) {
            mousePoint3D = e.getPoint();
            DefaultSet();
            if (up.contains(mousePoint3D)) {
                orderIndex = (int) Math.floor(2.0 * (offsetY - mousePoint3D.y) * upOrders.length / (buttonHeight - textAreaHeight) + upOrders.length);
                setFractionsColor(upOrders);
                linearPaint_up = new LinearGradientPaint(offsetX + buttonWidth / 2.0f, offsetY + (buttonHeight - textAreaHeight) / 2.0f,
                        offsetX + buttonWidth / 2.0f, offsetY, fractionsColor, changeColor);

            } else if (down.contains(mousePoint3D)) {
                orderIndex = (int) Math.floor((2.0 * mousePoint3D.y - buttonHeight - textAreaHeight) * dnOrders.length / (buttonHeight - textAreaHeight));
                setFractionsColor(dnOrders);
                linearPaint_down = new LinearGradientPaint(offsetX + buttonWidth / 2.0f, offsetY + (buttonHeight + textAreaHeight) / 2.0f,
                        offsetX + buttonWidth / 2.0f, offsetY + buttonHeight, fractionsColor, changeColor);

            } else {
                repaint();
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        DefaultSet();
        super.setEnabled(enabled);
    }

    private void setFractionsColor(Object[] orders) {
        if (orderIndex >= orders.length) orderIndex = orders.length - 1;
        if (orderIndex < 0) orderIndex = 0;
        float ratio = (orderIndex + 1.0f) / orders.length - 0.02f;
        fractionsColor[1] = ratio;
        fractionsColor[2] = ratio + 0.01f;
        changeColor[1] = getMiddleColor(colorIn, colorOut, ratio);
        order = orders[orderIndex].toString();
        text = order;
        repaint();
    }

    private void createShapes() {
        up = new Area(new RoundRectangle2D.Float(offsetX , offsetY , buttonWidth, roundSize, roundSize, roundSize));
        up.add(new Area(new Rectangle(offsetX, offsetY + roundSize / 2, buttonWidth, (buttonHeight - textAreaHeight - roundSize) / 2)));
        down = new Area(new RoundRectangle2D.Float(offsetX , offsetY + buttonHeight - roundSize, buttonWidth, roundSize, roundSize, roundSize));
        down.add(new Area(new Rectangle(offsetX, offsetY + buttonHeight / 2 + textAreaHeight / 2, buttonWidth, (buttonHeight - textAreaHeight - roundSize) / 2)));

        int xCenter = offsetX + buttonWidth / 2;

        int yDn = offsetY + buttonHeight * 3 / 4 + textAreaHeight / 4;//向下为正
        int yUp = offsetY + buttonHeight / 4 - textAreaHeight / 4;
        triangle_x = new int[]{xCenter - triangleSize, xCenter , xCenter + triangleSize};
        triangleUp_y = new int[]{yUp + triangleSize, yUp, yUp + triangleSize};
        triangleDn_y = new int[]{yDn - triangleSize, yDn, yDn - triangleSize};
        setBounds(0,0,buttonWidth+4,buttonHeight+4);
    }

    private void DefaultSet() {
        text = textDefault;
        order = null;
        linearPaint_up   = new LinearGradientPaint(offsetX + buttonWidth / 2.0f, offsetY + buttonHeight / 2.0f - textAreaHeight / 2.0f, offsetX + buttonWidth / 2.0f, offsetY, fractions, defaultColor);
        linearPaint_down = new LinearGradientPaint(offsetX + buttonWidth / 2.0f, offsetY + buttonHeight / 2.0f + textAreaHeight / 2.0f, offsetX + buttonWidth / 2.0f, offsetY + buttonHeight, fractions, defaultColor);//渐变由内到外
        //setBounds(0, 0, 2 * offsetX + buttonWidth, 2 * offsetY + buttonHeight);
    }

    private Color getMiddleColor(Color startColor, Color endColor, float ratio){
        return new Color((int) ((endColor.getRed()-startColor.getRed())*ratio+startColor.getRed()),
                (int) ((endColor.getGreen()-startColor.getGreen())*ratio+startColor.getGreen()),
                (int) ((endColor.getBlue()-startColor.getBlue())*ratio+startColor.getBlue()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(linearPaint_up);
        g2d.fill(up);

        g2d.setPaint(linearPaint_down);
        g2d.fill(down);

        metrics = g2d.getFontMetrics(font);
        //int x = offsetX + buttonWidth / 2 - metrics.stringWidth(XYCenterText) / 2;
        g2d.setFont(font);
        g2d.setColor(linesAndText);

        int x  = offsetX + buttonWidth / 2 - metrics.stringWidth(text) / 2;
        int y = offsetY + buttonHeight / 2 -  metrics.getHeight() / 2 + metrics.getAscent();
        g2d.drawString(text, x, y);

        //画外圈
        g2d.setStroke(stroke1);
        g2d.drawRoundRect(offsetX + buttonWidth / 2 - buttonWidth / 2, offsetY + buttonHeight / 2 - buttonHeight / 2, buttonWidth, buttonHeight, roundSize, roundSize);

        //内
        g2d.setStroke(stroke2);
        g2d.drawLine(offsetX + buttonWidth / 2 - buttonWidth /2, offsetY + buttonHeight / 2 - textAreaHeight/2,offsetX + buttonWidth / 2 + buttonWidth /2, offsetY + buttonHeight / 2 - textAreaHeight/2);
        g2d.drawLine(offsetX + buttonWidth / 2 - buttonWidth /2, offsetY + buttonHeight / 2 + textAreaHeight/2,offsetX + buttonWidth / 2 + buttonWidth /2, offsetY + buttonHeight / 2 + textAreaHeight/2);
        g2d.drawPolygon(triangle_x, triangleUp_y, 3);
        g2d.drawPolygon(triangle_x, triangleDn_y, 3);
    }

//    public static void main(String[] args){
//        JFrame frametest = new JFrame();
//        frametest.setLayout(null);
//        frametest.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frametest.setSize(104,204);
//
//        MoveButton1D graph1 = new MoveButton1D(new Float[]{0.01f,0.1f,1f}, new Float[]{0.01f,0.1f,5f});
//        //MoveButton1D graph1 = new MoveButton1D(new String[]{"0.01","0.1","1"}, new String[]{"-0.01","-0.1","-1","-5"});
//        graph1.setBounds(0,0,104,184);
//
//        JPanel container = new JPanel(null);
//        container.add(graph1);
//        container.setSize(104,184);
//        frametest.add(container);
//        frametest.setVisible(true);
//    }

    public void setColorNull(Color colorNull) {
        this.colorNull = colorNull;
    }

    public void setColorOut(Color colorOut) {
        this.colorOut = colorOut;
    }

    public void setColorIn(Color colorIn) {
        this.colorIn = colorIn;
    }

    public void setLinesAndText(Color linesAndText) {
        this.linesAndText = linesAndText;
    }

    public void setTextDefault(String textDefault) {
        this.textDefault = textDefault;
        repaint();
    }


    public Color getColorNull() {
        return colorNull;
    }

    public Color getColorOut() {
        return colorOut;
    }

    public Color getColorIn() {
        return colorIn;
    }

    public Color getLinesAndText() {
        return linesAndText;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    public int getTextAreaHeight() {
        return textAreaHeight;
    }

    public void setTextAreaHeight(int textAreaHeight) {
        this.textAreaHeight = textAreaHeight;
    }

    public int getButtonHeight() {
        return buttonHeight;
    }

    public void setSize(int width, int height) {
        buttonWidth = width;
        buttonHeight= height;
        createShapes();
    }

    public int getButtonWidth() {
        return buttonWidth;
    }

    public int getRoundSize() {
        return roundSize;
    }

    public void setRoundSize(int roundSize) {
        this.roundSize = roundSize;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getTriangleSize() {
        return triangleSize;
    }

    public void setTriangleSize(int triangleSize) {
        this.triangleSize = triangleSize;
    }


    public String getOrder() {
        return order;
    }

}
