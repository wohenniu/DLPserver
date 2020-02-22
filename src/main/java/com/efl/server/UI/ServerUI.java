package com.efl.server.UI;

import com.efl.server.dlpServer.DlpServer;
import com.efl.server.raspWifi.raspWIFI;
import com.efl.server.readimg.Readimg;
import com.efl.server.serialException.*;
import com.efl.server.serialPort.SerialPortA;
import com.efl.server.serialPort.SerialTool;
import com.efl.server.test.LightTest;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import gnu.io.SerialPort;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class ServerUI {

    @Autowired
    private SerialPortA SerialPortA;
    @Autowired
    private Readimg readimg;
    @Autowired
    DlpServer dlpServer;
    private LightTest lightTest;
    private raspWIFI raspWIFI;
    private String Filepath;
    private Thread printThread;
    private JPanel panel;
    private JPanel panelone;
    private JPanel paneltwo;
    private JButton addButton;
    private JTabbedPane tabbedPane;
    private JCheckBox linkCheckBox;
    private JComboBox comboBox2;
    private JButton emergencyButton;
    private JPanel Wifipanel;
    private JButton manulLink;
    private JTabbedPane tabbedPane1;
    private JProgressBar printProgressBar;
    private JButton stopButton;
    private JButton 允许客户端连接Button;
    private JButton 断开客户端连接Button;
    private JPanel zMovePanel;
    private JPanel yMovePanel;
    private JButton 光机测试Button;
    private JTextField textField1;
    private String path;
    private String name;
    private List<String> serialPortNames;
    private MoveButton1D moveButtonZ;
    private MoveButton1D moveButtonY;
    @PostConstruct
    public void init() {
        stopButton.setEnabled(false);
        MouseListener popItems = comboBox2.getMouseListeners()[0];
        //点击文本区域弹出
        comboBox2.removeMouseListener(popItems);
        comboBox2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                portListRefresh();
                popItems.mousePressed(e);
            }
        });
        //点击倒三角符号弹出
        comboBox2.getComponent(0).removeMouseListener(popItems);
        comboBox2.getComponent(0).addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                portListRefresh();
                popItems.mousePressed(e);
            }
        });

        emergencyButton.addActionListener(e -> {
            System.out.println(comboBox2.getSelectedItem().toString());
            try {
                SerialPort serialPort = SerialTool.openPort(comboBox2.getSelectedItem().toString(), 115200);
                SerialPortA.setSerialPort(serialPort);
//                SerialPortA.setZ(new BigDecimal("0"));
//                SerialPortA.setY(new BigDecimal("0"));
                if (serialPort != null) {
                    JOptionPane.showMessageDialog(null, comboBox2.getSelectedItem().toString() + "连接成功");
                    SerialPortA.addListener(serialPort, SerialPortA.getListener());
                } else {
                    JOptionPane.showMessageDialog(null, "连接失败");
                }
                linkCheckBox.setSelected(true);
                emergencyButton.setEnabled(false);
                //   // setManulCtrlEnabled(true);
                /*temperature=new Temperature(serialPortA);
                temperature.start();*/
            } catch (SerialPortParameterFailure serialPortParameterFailure) {
                serialPortParameterFailure.printStackTrace();
            } catch (NotASerialPort notASerialPort) {
                notASerialPort.printStackTrace();
            } catch (NoSuchPort noSuchPort) {
                noSuchPort.printStackTrace();
            } catch (PortInUse portInUse) {
                portInUse.printStackTrace();
            } catch (TooManyListeners tooManyListeners) {
                tooManyListeners.printStackTrace();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //   jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
                jfc.showDialog(new JLabel(), "选择");
                File file = jfc.getSelectedFile();
                if (null != file) {
                    System.out.println(file.getName());
                    if (file.isDirectory() && file.getName().endsWith("_slice")) {
                        Filepath = file.getAbsolutePath();
                        try {
                            int v = JOptionPane.showConfirmDialog(null, "是否开始打印", "请确认", JOptionPane.YES_NO_OPTION);
                            if (v == JOptionPane.YES_OPTION) {
                                readimg.readimg(Filepath);
                                readimg.getPrint().setPrintProgressBar(printProgressBar);
                                Thread.sleep(300);
                                printThread = readimg.getPrintThread();
                                stopButton.setEnabled(true);
                            }
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        System.out.println(file.getAbsolutePath());
                    } else {
                        JOptionPane.showMessageDialog(null, "请选择slice文件夹");
                    }
                }
            }
        });

        stopButton.addActionListener(e -> {
            int v = JOptionPane.showConfirmDialog(null, "是否终止打印", "请确认", JOptionPane.YES_NO_OPTION);
            if (v == JOptionPane.YES_OPTION) {
                if (printThread != null && printThread.isAlive()) {
                    printThread.interrupt();
                    stopButton.setEnabled(false);
                }
            }
        });

        moveButtonZ.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (moveButtonZ.getOrder() != null) {
                    //todo 执行Z轴运动
//                    if (!linkCheckBox.isSelected()) {
//                        JOptionPane.showMessageDialog(null, "请先连接打印机");
//                    } else {
                    SerialPortA.sendToPort("G1 Z" + moveButtonZ.getOrder() + " F400");
//                    }
                    System.out.println(moveButtonZ.getOrder());
                }
            }
        });

        moveButtonY.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (moveButtonY.getOrder() != null) {
                    //todo 执行料槽倾斜
//                    if (!linkCheckBox.isSelected()) {
//                        JOptionPane.showMessageDialog(null, "请先连接打印机");
//                    } else {
                    SerialPortA.sendToPort("G1 Y" + moveButtonY.getOrder() + " F200");
//                    }
                    System.out.println(moveButtonY.getOrder());
                }
            }
        });

        光机测试Button.addActionListener(e -> {
            if (光机测试Button.getText().equals("光机测试")) {
//                if (!linkCheckBox.isSelected()) {
//                    JOptionPane.showMessageDialog(null, "请先连接打印机");
//                } else {
                if (lightTest == null) {
                    lightTest = new LightTest();
                } else {
                    lightTest.setVisible(true);
                }
                SerialPortA.sendToPort("M1 S50");
                光机测试Button.setText("关闭");
//                }
            } else {
                lightTest.setVisible(false);
                SerialPortA.sendToPort("M1 S0");
                光机测试Button.setText("光机测试");
            }
        });

    }

    public ServerUI(raspWIFI raspWIFI) {
        this.raspWIFI = raspWIFI;
        Wifipanel = raspWIFI.getWifipanel();
        moveButtonZ = new MoveButton1D(new String[]{"0.01", "0.1", "1", "10"}, new String[]{"-0.01", "-0.1", "-1", "-10"}, "Z");
        moveButtonZ.setBounds(0, 0, 104, 184);
        JPanel containerZ = new JPanel(null);
        containerZ.add(moveButtonZ);
        containerZ.setSize(104, 184);
        zMovePanel = containerZ;
        moveButtonY = new MoveButton1D(new String[]{"0.5", "1", "5"}, new String[]{"-0.5", "-1", "-5"}, "料槽");
        moveButtonY.setBounds(0, 0, 104, 184);
        JPanel containerY = new JPanel(null);
        containerY.add(moveButtonY);
        containerY.setSize(104, 184);
        yMovePanel = containerY;
        $$$setupUI$$$();

        允许客户端连接Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Host = dlpServer.getHost();
                textField1.setText(Host);
            }
        });
    }

    private void portListRefresh() {
        String chosePort = (String) comboBox2.getSelectedItem();
        serialPortNames = SerialTool.findPort();
        comboBox2.removeAllItems();
        for (String s : serialPortNames) {
            comboBox2.addItem(s);
            if (s.equals(chosePort)) {
                comboBox2.setSelectedItem(s);
            }
        }
    }

    public void portClose() {
        if (SerialPortA != null) {
            SerialPortA.close();
        }
        linkCheckBox.setSelected(false);
        manulLink.setEnabled(true);
        if (printThread != null && printThread.isAlive()) {
            printThread.interrupt();
        }
    }

    public String getFilepath() {
        return Filepath;
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here

    }

    public Readimg getReadimg() {
        return readimg;
    }

    public void setReadimg(Readimg readimg) {
        this.readimg = readimg;
    }

    public void setFilepath(String filepath) {
        Filepath = filepath;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public JPanel getPanelone() {
        return panelone;
    }

    public void setPanelone(JPanel panelone) {
        this.panelone = panelone;
    }

    public JPanel getPaneltwo() {
        return paneltwo;
    }

    public void setPaneltwo(JPanel paneltwo) {
        this.paneltwo = paneltwo;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public void setAddButton(JButton addButton) {
        this.addButton = addButton;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel.setMinimumSize(new Dimension(720, 460));
        panel.setOpaque(true);
        panel.setPreferredSize(new Dimension(720, 460));
        panelone = new JPanel();
        panelone.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        panelone.setMaximumSize(new Dimension(9000, 1000));
        panelone.setMinimumSize(new Dimension(720, 50));
        panelone.setPreferredSize(new Dimension(720, 50));
        panelone.setRequestFocusEnabled(false);
        panel.add(panelone, BorderLayout.NORTH);
        comboBox2 = new JComboBox();
        panelone.add(comboBox2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, 30), new Dimension(150, 30), new Dimension(150, 30), 0, false));
        linkCheckBox = new JCheckBox();
        linkCheckBox.setIcon(new ImageIcon(getClass().getResource("/ico/disconnect36.png")));
        linkCheckBox.setSelectedIcon(new ImageIcon(getClass().getResource("/ico/connect36.png")));
        linkCheckBox.setText("");
        panelone.add(linkCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manulLink = new JButton();
        manulLink.setText("手动连接");
        panelone.add(manulLink, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(90, 30), new Dimension(90, 30), new Dimension(90, 30), 0, false));
        emergencyButton = new JButton();
        emergencyButton.setIcon(new ImageIcon(getClass().getResource("/ico/stop36.png")));
        emergencyButton.setLabel("");
        emergencyButton.setText("");
        panelone.add(emergencyButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(40, 40), new Dimension(40, 40), new Dimension(40, 40), 0, false));
        final Spacer spacer1 = new Spacer();
        panelone.add(spacer1, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        stopButton = new JButton();
        stopButton.setText("停止打印");
        panelone.add(stopButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(90, 30), new Dimension(90, 30), new Dimension(90, 30), 0, false));
        paneltwo = new JPanel();
        paneltwo.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        paneltwo.setMinimumSize(new Dimension(720, 400));
        paneltwo.setPreferredSize(new Dimension(720, 400));
        panel.add(paneltwo, BorderLayout.CENTER);
        tabbedPane = new JTabbedPane();
        paneltwo.add(tabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(640, 336), new Dimension(640, 336), new Dimension(9000, 5000), 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setInheritsPopupMenu(false);
        panel1.setMaximumSize(new Dimension(9000, 5000));
        panel1.setMinimumSize(new Dimension(720, 400));
        panel1.setPreferredSize(new Dimension(720, 400));
        tabbedPane.addTab("U盘打印", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(720, 32), new Dimension(720, 32), new Dimension(9000, 5000), 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("打印进度：");
        panel2.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        printProgressBar = new JProgressBar();
        printProgressBar.setStringPainted(true);
        panel2.add(printProgressBar, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 18), new Dimension(146, 18), new Dimension(1460, 21), 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel2.add(spacer4, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel2.add(spacer5, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel2.add(spacer6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(30, 5), new Dimension(30, 5), new Dimension(30, 5), 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(720, 336), new Dimension(720, 336), new Dimension(9000, 1000), 0, false));
        addButton = new JButton();
        addButton.setActionCommand("选择文件");
        addButton.setIcon(new ImageIcon(getClass().getResource("/ico/uu.png")));
        addButton.setLabel("");
        addButton.setText("");
        panel3.add(addButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, 80), new Dimension(130, 130), new Dimension(130, 130), 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setMinimumSize(new Dimension(40, 40));
        panel4.setPreferredSize(new Dimension(40, 40));
        tabbedPane.addTab("联机打印", panel4);
        tabbedPane1 = new JTabbedPane();
        panel4.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(640, 320), new Dimension(640, 320), new Dimension(9000, 4500), 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(7, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setMinimumSize(new Dimension(640, 300));
        panel5.setOpaque(true);
        panel5.setPreferredSize(new Dimension(640, 300));
        tabbedPane1.addTab("联机设置", panel5);
        允许客户端连接Button = new JButton();
        允许客户端连接Button.setActionCommand("允许客户端连接");
        允许客户端连接Button.setBorderPainted(false);
        允许客户端连接Button.setText("允许客户端连接");
        panel5.add(允许客户端连接Button, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 35), new Dimension(130, 35), new Dimension(130, 35), 0, false));
        断开客户端连接Button = new JButton();
        断开客户端连接Button.setText("断开客户端连接");
        panel5.add(断开客户端连接Button, new GridConstraints(5, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 35), new Dimension(130, 35), new Dimension(130, 30), 0, false));
        textField1 = new JTextField();
        panel5.add(textField1, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(180, 35), new Dimension(180, 35), new Dimension(180, 35), 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("服务端ip：");
        panel5.add(label2, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 40), new Dimension(-1, 40), new Dimension(-1, 40), 0, false));
        final Spacer spacer7 = new Spacer();
        panel5.add(spacer7, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel5.add(spacer8, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(70, 70), new Dimension(70, 70), new Dimension(70, 70), 0, false));
        final Spacer spacer9 = new Spacer();
        panel5.add(spacer9, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 86), new Dimension(-1, 86), new Dimension(-1, 86), 0, false));
        final Spacer spacer10 = new Spacer();
        panel5.add(spacer10, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(30, 20), new Dimension(30, 20), new Dimension(30, 20), 0, false));
        final Spacer spacer11 = new Spacer();
        panel5.add(spacer11, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(70, 70), new Dimension(-1, 70), new Dimension(-1, 70), 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.setMinimumSize(new Dimension(640, 280));
        panel6.setPreferredSize(new Dimension(640, 280));
        panel6.setRequestFocusEnabled(false);
        tabbedPane1.addTab("网络连接", panel6);
        panel6.add(Wifipanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(400, 100), new Dimension(400, 100), new Dimension(400, 100), 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.setMinimumSize(new Dimension(408, 265));
        panel7.setPreferredSize(new Dimension(408, 265));
        tabbedPane.addTab("设备控制", panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(640, 300), new Dimension(640, 300), new Dimension(4000, 2605), 0, false));
        final Spacer spacer12 = new Spacer();
        panel8.add(spacer12, new GridConstraints(0, 2, 6, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(40, 11), new Dimension(40, 11), new Dimension(40, 11), 0, false));
        final Spacer spacer13 = new Spacer();
        panel8.add(spacer13, new GridConstraints(0, 0, 6, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(220, 1), new Dimension(220, 1), new Dimension(220, 1), 0, false));
        final Spacer spacer14 = new Spacer();
        panel8.add(spacer14, new GridConstraints(2, 4, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(260, 1), new Dimension(260, 1), new Dimension(260, 1), 0, false));
        panel8.add(yMovePanel, new GridConstraints(0, 3, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(104, 184), new Dimension(104, 184), new Dimension(104, 184), 0, false));
        panel8.add(zMovePanel, new GridConstraints(0, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(104, 184), new Dimension(104, 184), new Dimension(104, 184), 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        光机测试Button = new JButton();
        光机测试Button.setActionCommand("光机测试");
        光机测试Button.setText("光机测试");
        panel9.add(光机测试Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 30), new Dimension(100, 30), new Dimension(100, 30), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
