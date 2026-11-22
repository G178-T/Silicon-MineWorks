package com.xtaing.ui.lineChart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * VFX是视觉特效（Visual Effects）的缩写。
 * VFXPanel是特效覆盖层，本类覆盖整个窗口，为每个锚点按钮按各自间隔持续发射流光线。
 * 各锚点可独立启停并配置不同色相，覆盖层不接收鼠标事件。
 */
public class VFXPanel extends JPanel {
    /** 单侧发射间隔，单位为毫秒。 */
    private static final int EMIT_DELAY = 150;
    /** 动画帧间隔，单位为毫秒。 */
    private static final int FRAME_DELAY = 16;

    /**
     * Emitter是发射源，本类描述单个锚点按钮的启停状态、配色与定时任务。
     */
    private static class Emitter {
        final JComponent anchor;
        final float hue;
        boolean active;
        Timer leftTimer;
        Timer rightTimer;

        Emitter(JComponent anchor, float hue) {
            this.anchor = anchor;
            this.hue = hue;
        }
    }

    /** 当前尚存活的流光线集合。 */
    private final List<DrawVFX> lines = new ArrayList<>();
    /** 已注册的发射源集合。 */
    private final List<Emitter> emitters = new ArrayList<>();
    /** 用于在线条高度范围内生成随机纵坐标。 */
    private final Random random = new Random();

    private Timer animTimer;

    /**
     * 构造特效覆盖层。
     */
    public VFXPanel() {
        setOpaque(false);
    }

    /**
     * 注册一个发射源，指定其锚点按钮与线条色相。
     *
     * @param anchor 作为发射源的按钮
     * @param hue 该发射源所发线条的基准色相
     */
    public void addEmitter(JComponent anchor, float hue) {
        emitters.add(new Emitter(anchor, hue));
    }

    /**
     * 设置指定锚点的发射状态。
     * 仅在状态发生变化时启停定时任务，避免重复创建。
     *
     * @param anchor 目标锚点按钮
     * @param active true 表示开始发射，false 表示停止发射
     */
    public void setActive(JComponent anchor, boolean active) {
        for (Emitter emitter : emitters) {
            if (emitter.anchor != anchor) {
                continue;
            }
            if (active && !emitter.active) {
                startEmitter(emitter);
            } else if (!active && emitter.active) {
                stopEmitter(emitter);
            }
            return;
        }
    }

    /**
     * 本覆盖层不参与鼠标事件的命中判定，使事件穿透至下层组件。
     */
    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    /**
     * 启动单个发射源的左右定时发射，必要时创建动画定时器。
     */
    private void startEmitter(Emitter emitter) {
        emitter.active = true;
        emitter.leftTimer = new Timer(EMIT_DELAY, e -> emit(emitter, DrawVFX.Direction.left));
        emitter.rightTimer = new Timer(EMIT_DELAY, e -> emit(emitter, DrawVFX.Direction.right));
        emitter.leftTimer.start();
        emitter.rightTimer.start();
        // 停止发射后残留线条仍在播放，此处避免重复创建动画定时器
        if (animTimer == null) {
            animTimer = new Timer(FRAME_DELAY, e -> advance());
            animTimer.start();
        }
    }

    /**
     * 停止单个发射源的定时发射，不清除其已发出的线条。
     */
    private void stopEmitter(Emitter emitter) {
        emitter.active = false;
        emitter.leftTimer.stop();
        emitter.rightTimer.stop();
        emitter.leftTimer = null;
        emitter.rightTimer = null;
    }

    /**
     * 在指定发射源的锚点一侧生成一条流光线。
     * 先将按钮边界转换到覆盖层坐标系，纵坐标在按钮的上下边界之间随机取值。
     *
     * @param emitter 目标发射源
     * @param direction 发射方向
     */
    private void emit(Emitter emitter, DrawVFX.Direction direction) {
        Rectangle bounds = SwingUtilities.convertRectangle(
                emitter.anchor.getParent(), emitter.anchor.getBounds(), this);
        if (bounds.width == 0 || bounds.height == 0) {
            return;
        }
        int y = bounds.y + random.nextInt(bounds.height);
        lines.add(new DrawVFX(bounds, y, direction, emitter.hue));
    }

    /**
     * 推进所有线条一帧，并移除已走完生命周期者。
     * 当全部发射源均已停止且画面清空时，停止动画定时器以避免空转。
     */
    private void advance() {
        Iterator<DrawVFX> it = lines.iterator();
        while (it.hasNext()) {
            DrawVFX line = it.next();
            line.update();
            if (line.isDone()) {
                it.remove();
            }
        }
        if (!hasActiveEmitter() && lines.isEmpty() && animTimer != null) {
            animTimer.stop();
            animTimer = null;
        }
        repaint();
    }

    /**
     * 判断当前是否存在处于发射状态的锚点。
     *
     * @return 存在则返回 true，否则返回 false
     */
    private boolean hasActiveEmitter() {
        for (Emitter emitter : emitters) {
            if (emitter.active) {
                return true;
            }
        }
        return false;
    }

    /**
     * 绘制全部流光线。
     * 通过裁剪区排除所有锚点按钮所占矩形，使线条仅在按钮外部可见。
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Area clip = new Area(new Rectangle(0, 0, getWidth(), getHeight()));
        // 逐个排除锚点按钮矩形，避免任何线段落入按钮内部
        for (Emitter emitter : emitters) {
            Rectangle bounds = SwingUtilities.convertRectangle(
                    emitter.anchor.getParent(), emitter.anchor.getBounds(), this);
            clip.subtract(new Area(bounds));
        }
        g2.setClip(clip);
        for (DrawVFX line : lines) {
            line.draw(g2);
        }
        g2.dispose();
    }
}
