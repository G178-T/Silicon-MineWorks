package com.xtaing.ui.background;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * JcodecSource是纯 Java 视频源，本类以 JCodec 解码视频文件，作为 native 解码不可用时的兜底方案。
 * 不依赖任何平台二进制，可在任意 JVM 上运行，但解码速度低于 FFmpeg。
 */
public class JcodecSource implements VideoSource {
    /** 兜底播放的帧间隔，单位为毫秒，约合 24 帧每秒。 */
    private static final int FRAME_DELAY = 42;

    private final File file;
    private Thread decoder;
    private volatile BufferedImage frame;
    private volatile boolean running;
    private int imageWidth;
    private int imageHeight;

    /**
     * 构造纯 Java 视频源。
     *
     * @param path 视频文件路径
     */
    public JcodecSource(String path) {
        this.file = new File(path);
    }

    @Override
    public void start() {
        if (running || !file.isFile()) {
            return;
        }
        running = true;
        decoder = new Thread(this::decodeLoop, "jcodec-decoder");
        decoder.setDaemon(true);
        decoder.start();
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        if (decoder != null) {
            decoder.interrupt();
            decoder = null;
        }
        frame = null;
    }

    @Override
    public BufferedImage currentFrame() {
        return frame;
    }

    @Override
    public int width() {
        return imageWidth;
    }

    @Override
    public int height() {
        return imageHeight;
    }

    /**
     * 解码循环，逐帧读取并按固定间隔发布，到达文件末尾后重新打开以循环播放。
     *  JCodec 仅支持其覆盖的编码格式，解码失败时输出原因并终止循环，保留最后一帧。
     */
    private void decodeLoop() {
        try {
            while (running) {
                SeekableByteChannel channel = NIOUtils.readableChannel(file);
                try {
                    FrameGrab grab = FrameGrab.createFrameGrab(channel);
                    Picture picture;
                    while (running && (picture = grab.getNativeFrame()) != null) {
                        BufferedImage decoded = AWTUtil.toBufferedImage(picture);
                        if (imageWidth == 0) {
                            imageWidth = decoded.getWidth();
                            imageHeight = decoded.getHeight();
                        }
                        frame = decoded;
                        Thread.sleep(FRAME_DELAY);
                    }
                } finally {
                    channel.close();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException | JCodecException e) {
            System.err.println("JCodec 解码失败：" + e);
        }
    }
}
