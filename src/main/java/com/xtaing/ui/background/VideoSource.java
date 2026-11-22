package com.xtaing.ui.background;

import java.awt.image.BufferedImage;

/**
 * VideoSource是视频源接口，本接口定义背景视频的播放控制与帧获取契约。
 * 背景面板只依赖本接口，后续接入具体解码实现时无需改动绘制逻辑。
 */
public interface VideoSource {
    /**
     * 启动视频源，使其进入可输出帧的状态。
     */
    void start();

    /**
     * 停止视频源，停止推进播放进度。
     */
    void stop();

    /**
     * 取得当前应当显示的帧。
     *
     * @return 当前帧图像，无可用帧时返回 null
     */
    BufferedImage currentFrame();

    /**
     * 取得帧的宽度。
     *
     * @return 帧宽度，单位为像素
     */
    int width();

    /**
     * 取得帧的高度。
     *
     * @return 帧高度，单位为像素
     */
    int height();
}
