package com.ui.start;


import com.i18n.zh_CN;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class start extends JFrame{
    public start() {
        JFrame JfStart = new JFrame("Silicon Mineworks");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JfStart.setSize(600,800);
        Container container = JfStart.getContentPane();

        container.setLayout(new BorderLayout());
        String title = "";
        JLabel label = new JLabel(new zh_CN());
    }
}
