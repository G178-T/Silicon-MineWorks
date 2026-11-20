package com.xtaing.ui.start;


import javax.swing.*;
import java.awt.*;

public class Start extends JFrame{
    public Start() {
        super();
        JFrame JfStart = new JFrame("Silicon Mineworks");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JfStart.setSize(800,600);

        Container container = JfStart.getContentPane();
        container.setLayout(new BorderLayout());

        JLabel label = new JLabel("Silicon Mineworks");
        label.setFont(new Font("微软雅黑",Font.BOLD,50));
        label.setHorizontalAlignment(SwingConstants.CENTER); //水平居中
        container.add(label,BorderLayout.NORTH);

        Panel panel1 = new Panel(new GridLayout(10,3));
        JButton start = new JButton("开始游戏");
        JButton setting = new JButton("设置");
        panel1.add(start);
        panel1.add(setting);
        container.add(panel1,BorderLayout.WEST,1);

        Panel panel2 = new Panel();
        container.add(panel2,BorderLayout.CENTER,1);

        JfStart.setVisible(true);
    }
}
