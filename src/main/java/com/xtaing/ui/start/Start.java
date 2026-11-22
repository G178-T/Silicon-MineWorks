package com.xtaing.ui.start;

import com.xtaing.core.player.Player;
import com.xtaing.ui.background.VideoBackground;
import com.xtaing.ui.lineChart.DrawVFX;
import com.xtaing.ui.lineChart.VFXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Start是开始界面，本类提供左侧主菜单与右侧分层选项的布局。
 */
public class Start extends JFrame {
    private static final Color BG = new Color(30, 34, 46, 200);
    private static final Color MENU_BG = new Color(40, 45, 60, 220);
    private static final Color BUTTON_BG = new Color(50, 56, 74);
    private static final Color ACCENT = new Color(0, 190, 200);
    private static final Color TEXT = new Color(232, 236, 245);
    private static final Color MUTED = new Color(160, 168, 190);

    /** 开始游戏按钮的特效色相，对应青色。 */
    private static final float HUE_PLAY = 0.5f;
    /** 设置按钮的特效色相，对应橙色。 */
    private static final float HUE_SETTING = 0.08f;
    /** 关于按钮的特效色相，对应紫色。 */
    private static final float HUE_ABOUT = 0.78f;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private JButton activeButton;
    private JButton playButton;
    private JButton settingButton;
    private JButton aboutButton;
    private VFXPanel vfx;
    private String currentCard = "play";
    private String hoveredCard;

    /**
     * 构造开始界面，按屏幕尺寸初始化窗口，并装配菜单、内容区与特效覆盖层。
     */
    public Start() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocationRelativeTo(null);

        // 视频背景作为内容窗格铺满整个窗口，菜单与内容面板叠放在其上
        VideoBackground background = new VideoBackground();
        background.setLayout(new BorderLayout());
        // 按固定路径装载背景视频，文件未就位时仅显示底色
        background.loadVideo();
        setContentPane(background);
        content.setBackground(BG);

        // 包装层以内容区底色填充，左侧留白使菜单面板整体右移
        JPanel menuArea = new JPanel(new BorderLayout());
        menuArea.setBackground(BG);
        menuArea.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        menuArea.add(createMenu(), BorderLayout.CENTER);

        add(menuArea, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        content.add(createPlayOptions(), "play");
        content.add(createSettingOptions(), "setting");
        content.add(createAboutOptions(), "about");

        // 特效层作为玻璃层覆盖全窗口，三个菜单按钮各注册一种色相的发射源
        vfx = new VFXPanel();
        vfx.addEmitter(playButton, HUE_PLAY);
        vfx.addEmitter(settingButton, HUE_SETTING);
        vfx.addEmitter(aboutButton, HUE_ABOUT);
        setGlassPane(vfx);
        vfx.setVisible(true);
        refreshVfx();

        setVisible(true);
    }

    /**
     * 返回当前窗口实例，供外部进行界面跳转。
     *
     * @return 当前窗口
     */
    public JFrame getJFrame() {
        return this;
    }

    /**
     * 根据当前所在卡片与各按钮的悬停状态，分别决定三个发射源的启停。
     * 对应卡片显示中或鼠标停留在该按钮上时启动，否则停止发射。
     */
    private void refreshVfx() {
        if (vfx == null) {
            return;
        }
        // 两个触发条件为或关系，任一成立即保持对应发射源工作
        vfx.setActive(playButton, "play".equals(currentCard) || "play".equals(hoveredCard));
        vfx.setActive(settingButton, "setting".equals(currentCard) || "setting".equals(hoveredCard));
        vfx.setActive(aboutButton, "about".equals(currentCard) || "about".equals(hoveredCard));
    }

    /**
     * 构建左侧主菜单，包含标题、副标题与三个导航按钮。
     *
     * @return 主菜单面板
     */
    private JPanel createMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(MENU_BG);
        menu.setBorder(BorderFactory.createEmptyBorder(48, 28, 48, 28));
        menu.setPreferredSize(new Dimension(300, 0));

        JLabel title = new JLabel("Silicon Mineworks");
        title.setFont(new Font("微软雅黑", Font.BOLD, 22));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("从晶圆到芯片");
        sub.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        sub.setForeground(MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        playButton = menuButton("开始游戏", "play", HUE_PLAY);
        settingButton = menuButton("设置", "setting", HUE_SETTING);
        aboutButton = menuButton("关于", "about", HUE_ABOUT);
        // 悬停任一菜单按钮均触发其各自配色的特效，以便未进入该界面时也能预览
        attachHover(playButton, "play");
        attachHover(settingButton, "setting");
        attachHover(aboutButton, "about");
        select(playButton, HUE_PLAY);

        menu.add(title);
        menu.add(Box.createVerticalStrut(4));
        menu.add(sub);
        menu.add(Box.createVerticalStrut(48));

        menu.add(playButton);
        menu.add(Box.createVerticalStrut(15));
        menu.add(settingButton);
        menu.add(Box.createVerticalStrut(15));
        menu.add(aboutButton);
        menu.add(Box.createVerticalGlue());

        return menu;
    }

    /**
     * 为菜单按钮注册悬停监听，记录当前悬停的卡片标识以触发特效。
     *
     * @param button 需要监听的菜单按钮
     * @param card 该按钮对应的卡片标识
     */
    private void attachHover(JButton button, String card) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoveredCard = card;
                refreshVfx();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredCard = null;
                refreshVfx();
            }
        });
    }

    /**
     * 创建左侧导航按钮，点击后将右侧内容区切换到指定卡片。
     *
     * @param text 按钮显示的文字
     * @param card 目标卡片的标识
     * @param hue 按钮高亮所用的色相
     * @return 导航按钮
     */
    private JButton menuButton(String text, String card, float hue) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        button.setForeground(TEXT);
        button.setBackground(BUTTON_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 48));
        button.addActionListener(e -> {
            select(button, hue);
            cards.show(content, card);
            currentCard = card;
            refreshVfx();
        });
        return button;
    }

    /**
     * 将指定按钮设为高亮，并还原先前高亮按钮的底色。
     * 高亮色取该按钮对应特效的起始颜色，使选中态与流光配色一致。
     *
     * @param button 需要高亮的按钮
     * @param hue 该按钮对应的特效色相
     */
    private void select(JButton button, float hue) {
        if (activeButton != null) {
            activeButton.setBackground(BUTTON_BG);
        }
        activeButton = button;
        button.setBackground(DrawVFX.colorOf(hue));
    }

    /**
     * 构建开始游戏选项面板，提供新游戏与读取存档两个入口。
     *
     * @return 选项面板
     */
    private JPanel createPlayOptions() {
        JPanel panel = optionPanel("开始游戏");
        JButton startButton = optionButton("新游戏");
        startButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("开始新的游戏");
                Player player = new Player(200);
            }
        });
        JButton loadButton = optionButton("读取存档");
        loadButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("开始读取存档");
            }
        });

        panel.add(startButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loadButton);
        return panel;
    }

    /**
     * 构建设置选项面板。
     *
     * @return 选项面板
     */
    private JPanel createSettingOptions() {
        JPanel panel = optionPanel("设置");
        panel.add(optionButton("画面"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(optionButton("声音"));
        return panel;
    }

    /**
     * 构建关于选项面板，显示当前版本信息。
     *
     * @return 选项面板
     */
    private JPanel createAboutOptions() {
        JPanel panel = optionPanel("关于");
        JLabel info = new JLabel("Silicon Mineworks  v1.0.0");
        info.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        info.setForeground(MUTED);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(info);
        return panel;
    }

    /**
     * 构建选项面板的公共骨架，包含标题及其下方的固定间距。
     *
     * @param heading 面板标题
     * @return 选项面板
     */
    private JPanel optionPanel(String heading) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(80, 64, 80, 64));

        JLabel label = new JLabel(heading);
        label.setFont(new Font("微软雅黑", Font.BOLD, 32));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(32));
        return panel;
    }

    /**
     * 创建右栏使用的选项按钮。
     *
     * @param text 按钮显示的文字
     * @return 选项按钮
     */
    private JButton optionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        button.setForeground(TEXT);
        button.setBackground(BUTTON_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(280, 44));
        return button;
    }
}
