package com.ui.start;


import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class start extends JFrame{
    public start() {
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
        JfStart.setVisible(true);
    }
}
