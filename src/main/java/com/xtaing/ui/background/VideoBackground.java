package com.xtaing.ui.background;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * VideoBackground是视频背景面板，本类将视频源的当前帧按铺满方式绘制，作为界面的最底层。
 */
public class VideoBackground extends JPanel {
    /** 拉取帧并重绘的间隔，单位为毫秒。 */
    private static final int FRAME_DELAY = 33;
    /** 无视频源时使用的底色。 */
    private static final Color FALLBACK = new Color(18, 20, 28);
    /** 背景视频的固定路径：将视频置于项目根目录的 assets 下并命名为 background.mp4。 */
    public static final String VIDEO_PATH = "assets/background.mp4";

    private VideoSource source;
    private Timer timer;

    /**
     * 构造视频背景面板。
     */
    public VideoBackground() {
        setOpaque(true);
        setBackground(FALLBACK);
    }

    /**
     * 按固定路径装载背景视频。
     * 仅当路径指向的视频文件存在时设置视频源，否则维持底色，不对文件缺失作异常处理。
     */
    public void loadVideo() {
        FallbackVideoSource video = new FallbackVideoSource(VIDEO_PATH);
        if (video.exists()) {
            setSource(video);
        }
    }

    /**
     * 设置视频源并启动播放。
     * 替换视频源时会先停止原有源，无需外部另行清理。
     *
     * @param source 新的视频源，传入 null 表示仅显示底色
     */
    public void setSource(VideoSource source) {
        stop();
        this.source = source;
        if (source != null) {
            source.start();
            if (timer == null) {
                timer = new Timer(FRAME_DELAY, e -> repaint());
                timer.start();
            }
        }
        repaint();
    }

    /**
     * 停止播放并释放视频源，动画定时器一并停止。
     */
    public void stop() {
        if (source != null) {
            source.stop();
        }
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * 绘制当前帧。
     * 帧按保持宽高比的 cover 方式缩放并居中裁切，确保铺满整个面板且不变形。
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (source == null) {
            return;
        }
        BufferedImage frame = source.currentFrame();
        if (frame == null || frame.getWidth() == 0 || frame.getHeight() == 0) {
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        // cover 缩放取宽高两个方向中较大的比例，使帧覆盖整个面板
        double scale = Math.max(
                panelWidth / (double) frame.getWidth(),
                panelHeight / (double) frame.getHeight());
        int drawWidth = (int) Math.ceil(frame.getWidth() * scale);
        int drawHeight = (int) Math.ceil(frame.getHeight() * scale);
        int x = (panelWidth - drawWidth) / 2;
        int y = (panelHeight - drawHeight) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(frame, x, y, drawWidth, drawHeight, this);
        g2.dispose();
    }
}
