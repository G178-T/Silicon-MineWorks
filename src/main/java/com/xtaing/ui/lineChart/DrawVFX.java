package com.xtaing.ui.lineChart;

import java.awt.*;

/**
 * VFX是视觉特效（Visual Effects）的缩写。
 * DrawVFX是视觉特效线条，本类保存单条流光线的位置、方向、配色与生命周期。
 * 线条以中心点沿水平方向平移，渲染时飞行前端不透明、后端渐隐。
 */
public class DrawVFX {
    /** 流光线的飞行方向。 */
    public enum Direction {
        left,
        right
    }

    /** 线条总长度，单位为像素。 */
    private static final int LINE_LENGTH = 60;
    /** 每帧沿水平方向平移的像素数。 */
    private static final int SPEED = 3;
    /** 线条可存活的最大帧数，达到该值后应由外部移除。 */
    private static final int LIFE = 40;
    /** 线条与按钮高亮共用的饱和度。 */
    private static final float SATURATION = 0.85f;
    /** 线条与按钮高亮共用的亮度。 */
    private static final float BRIGHTNESS = 1f;
    /** 色相波动的频率，决定色彩流动的快慢。 */
    private static final float HUE_FREQUENCY = 0.09f;
    /** 色相相对基准值的最大偏移，用于保持所属发射源的主色调。 */
    private static final float HUE_RANGE = 0.05f;

    /** 线条的飞行方向，同时决定平移与渐变的方向。 */
    private final Direction direction;
    /** 线条所在的纵坐标，由发射器在按钮高度范围内随机确定。 */
    private final int y;
    /** 线条的基准色相，决定其配色归属。 */
    private final float baseHue;
    /** 线条中心点的横坐标，初始化时位于按钮内部。 */
    private int centerX;
    /** 已存活的帧数。 */
    private int life;
    /** 当前色相，在基准值附近小幅波动。 */
    private float hue;

    /**
     * 依据锚点按钮的边界构造一条流光线。
     * 中心点初始位于按钮内部距边缘半个线长处，使整条线先被按钮区域遮挡，再随平移逐渐探出。
     *
     * @param buttonBounds 锚点按钮在特效层坐标系中的边界
     * @param y 线条的纵坐标
     * @param direction 线条的飞行方向
     * @param baseHue 线条的基准色相
     */
    public DrawVFX(Rectangle buttonBounds, int y, Direction direction, float baseHue) {
        int half = LINE_LENGTH / 2;
        this.centerX = (direction == Direction.left)
                ? buttonBounds.x + half
                : buttonBounds.x + buttonBounds.width - half;
        this.y = y;
        this.direction = direction;
        this.baseHue = baseHue;
        this.hue = baseHue;
    }

    /**
     * 推进一帧，依次更新中心点坐标、存活帧数与色相。
     */
    public void update() {
        centerX += (direction == Direction.left) ? -SPEED : SPEED;
        life++;
        // 色相在基准值附近波动，既保留流动感又不改变所属发射源的主色调
        hue = baseHue + (float) Math.sin(life * HUE_FREQUENCY) * HUE_RANGE;
        if (hue < 0f) {
            hue += 1f;
        } else if (hue >= 1f) {
            hue -= 1f;
        }
    }

    /**
     * 判断线条是否已走完生命周期。
     *
     * @return 存活帧数达到上限时返回 true，否则返回 false
     */
    public boolean isDone() {
        return life >= LIFE;
    }

    /**
     * 取得指定色相在特效起始状态下的颜色，供按钮高亮处复用。
     *
     * @param hue 色相
     * @return 对应的颜色
     */
    public static Color colorOf(float hue) {
        return Color.getHSBColor(hue, SATURATION, BRIGHTNESS);
    }

    /**
     * 将线条绘制到指定的二维画笔。
     * 整体透明度随存活帧数线性衰减，沿线方向由前端的实色渐变为后端的全透明。
     *
     * @param g2 用于绘制的二维画笔
     */
    public void draw(Graphics2D g2) {
        int alpha = (int) (255 * (1f - life / (float) LIFE));
        Color base = colorOf(hue);
        Color bright = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
        Color faded = new Color(base.getRed(), base.getGreen(), base.getBlue(), 0);
        int half = LINE_LENGTH / 2;
        int step = (direction == Direction.left) ? -half : half;
        // frontX 位于飞行方向前端，backX 位于后端，渐变据此由实色过渡到全透明
        int frontX = centerX + step;
        int backX = centerX - step;

        g2.setStroke(new BasicStroke(3f));
        g2.setPaint(new GradientPaint(frontX, y, bright, backX, y, faded));
        g2.drawLine(frontX, y, backX, y);
    }
}
