package com.xtaing.ui.background;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * FallbackVideoSource是降级视频源，本类优先采用 JavaCV 解码，失败时自动切换到纯 Java 的 JCodec 解码。
 * 上层只依赖 VideoSource 接口，解码后端的切换对背景面板透明。
 */
public class FallbackVideoSource implements VideoSource {
    private final String path;
    private VideoSource delegate;

    /**
     * 构造降级视频源。
     *
     * @param path 视频文件路径
     */
    public FallbackVideoSource(String path) {
        this.path = path;
    }

    /**
     * 判断视频文件是否已就位。
     *
     * @return 路径指向普通文件时返回 true
     */
    public boolean exists() {
        return new File(path).isFile();
    }

    /**
     * 依次尝试解码后端，优先 JavaCV，失败后降级至 JCodec。
     * 两者均失败时保持空委托，背景面板显示底色。
     */
    @Override
    public void start() {
        FileVideoSource primary = new FileVideoSource(path);
        primary.start();
        if (primary.isReady()) {
            delegate = primary;
            return;
        }

        System.err.println("JavaCV 不可用，降级至 JCodec 纯 Java 解码");
        JcodecSource fallback = new JcodecSource(path);
        fallback.start();
        delegate = fallback;
    }

    @Override
    public void stop() {
        if (delegate != null) {
            delegate.stop();
            delegate = null;
        }
    }

    @Override
    public BufferedImage currentFrame() {
        return delegate == null ? null : delegate.currentFrame();
    }

    @Override
    public int width() {
        return delegate == null ? 0 : delegate.width();
    }

    @Override
    public int height() {
        return delegate == null ? 0 : delegate.height();
    }
}
