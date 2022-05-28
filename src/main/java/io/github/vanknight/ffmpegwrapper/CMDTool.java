package io.github.vanknight.ffmpegwrapper;

import cn.hutool.core.date.DateUtil;
import io.github.vanknight.ffmpegwrapper.listener.FfmpegLogListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 转码命令工具
 */
public class CMDTool {
    /**
     * 阻塞执行命令
     *
     * @param commands 命令参数
     */
    public static int waitProcessCommand(List<String> commands, FfmpegLogListener ffmpegLogListener) {
        ProcessBuilder builder = new ProcessBuilder(commands);
        Process process = null;
        // 创建线程执行
        AtomicInteger resultCode = new AtomicInteger(-1);
        try {
            // 正常信息和错误信息不合并输出
            builder.redirectErrorStream(true);
            // 开始执行命令
            process = builder.start();
            Thread readThread = FFmpegThread.readThread(resultCode, process, ffmpegLogListener);
            readThread.start();
            System.out.println("waitFor");
            return process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("关闭Process");
            if (process != null) {
                process.destroy();
            }
        }
        return -1;
    }

    /**
     * HH:mm:ss => int
     *
     * @param timeStr HH:mm:ss
     * @return 秒数
     */
    public static int timeToSecond(String timeStr) {
        if (timeStr == null || timeStr.length() < 8) {
            return 0;
        }
        if (timeStr.contains(".")) {
            timeStr = timeStr.split("\\.")[0];
        }
        return DateUtil.timeToSecond(timeStr.split("\\.")[0]);
    }
}
