package com.xtaing.ui.background;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * FileVideoSource是文件视频源，本类以 FFmpeg 解码本地视频文件并持续输出帧，供背景面板铺满绘制。
 * 解码在独立守护线程中执行，帧经深拷贝后发布，避免绘制线程读到正在改写的图像。
 */
public class FileVideoSource implements VideoSource {
    /** 默认帧率，视频未报告有效帧率时按此值控制解码节奏。 */
    private static final double DEFAULT_FPS = 30.0;

    private final File file;
    private FFmpegFrameGrabber grabber;
    private Java2DFrameConverter converter;
    private Thread decoder;
    private volatile BufferedImage frame;
    private volatile boolean running;
    private int imageWidth;
    private int imageHeight;

    /**
     * 构造文件视频源。
     *
     * @param path 视频文件路径
     */
    public FileVideoSource(String path) {
        this.file = new File(path);
    }

    /**
     * 判断视频文件是否已就位。
     *
     * @return 路径指向普通文件时返回 true
     */
    public boolean exists() {
        return file.isFile();
    }

    /**
     * 打开视频并启动解码线程。
     * 初始化失败时保留干净状态并输出简要原因，由上层据此决定是否降级到其他解码源。
     */
    @Override
    public void start() {
        if (running || !file.isFile()) {
            return;
        }
        try {
            grabber = new FFmpegFrameGrabber(file.getAbsolutePath());
            grabber.start();
            imageWidth = grabber.getImageWidth();
            imageHeight = grabber.getImageHeight();
            converter = new Java2DFrameConverter();
            running = true;
            decoder = new Thread(this::decodeLoop, "video-decoder");
            decoder.setDaemon(true);
            decoder.start();
        } catch (Throwable e) {
            if (e instanceof VirtualMachineError) {
                throw (VirtualMachineError) e;
            }
            // native 缺失时抛出的是 LinkageError，需一并捕获才能触发上层降级
            System.err.println("JavaCV 解码初始化失败：" + e);
            release();
        }
    }

    /**
     * 报告解码器是否已就绪。
     *
     * @return 成功打开视频并启动解码线程时返回 true
     */
    public boolean isReady() {
        return running;
    }

    /**
     * 停止解码线程并释放解码器资源。
     */
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
        release();
    }

    /**
     * 取得当前已解码的帧。
     *
     * @return 当前帧图像，尚无可用帧时返回 null
     */
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
     * 解码循环，按视频帧率推进，到达文件末尾后回到首帧以循环播放。
     * 单帧解码失败仅记录并继续，不终止整个循环。
     */
    private void decodeLoop() {
        double fps = grabber.getFrameRate();
        if (fps <= 0 || Double.isNaN(fps)) {
            fps = DEFAULT_FPS;
        }
        long interval = Math.max(1L, (long) (1000.0 / fps));

        while (running) {
            long begin = System.currentTimeMillis();
            try {
                Frame raw = grabber.grab();
                if (raw == null) {
                    // 到达文件末尾时回到首帧，使背景持续循环
                    grabber.setFrameNumber(0);
                } else {
                    BufferedImage decoded = converter.convert(raw);
                    if (decoded != null) {
                        frame = copy(decoded);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            long wait = interval - (System.currentTimeMillis() - begin);
            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * 将解码图像复制到独立缓冲，避免转换器复用缓冲造成画面撕裂。
     *
     * @param src 解码得到的图像
     * @return 独立的图像副本
     */
    private BufferedImage copy(BufferedImage src) {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = dst.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dst;
    }

    /**
     * 关闭解码器并清空当前帧。
     */
    private void release() {
        if (grabber != null) {
            try {
                grabber.stop();
                grabber.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            grabber = null;
        }
        converter = null;
        frame = null;
    }
}
