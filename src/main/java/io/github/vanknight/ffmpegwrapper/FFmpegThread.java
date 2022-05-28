package io.github.vanknight.ffmpegwrapper;

import cn.hutool.core.date.DateUtil;
import io.github.vanknight.ffmpegwrapper.listener.FfmpegLogListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FFmpegThread {

    public static Thread readThread(AtomicInteger result, Process process, FfmpegLogListener listener) {
        BufferedReader readStdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // 视频总时长
        AtomicInteger duration = new AtomicInteger(0);
        // 视频信息
        Map<String, Object> videoInfoMap = new HashMap<>();
        Thread readStdoutThread = new Thread() {
            String line;

            @Override
            public void run() {
                System.out.println("开始读取流");
                try {
                    while ((line = readStdout.readLine()) != null) {
                        //System.out.println("line: " + line);
                        if (duration.get() <= 0) {
                            Map<String, Object> durationResultMap = FFmpegLogRegex.regexVideoDuration(line);
                            if (durationResultMap.size() > 0) {
                                String durationTimeStr = (String) durationResultMap.get("duration");
                                int durationTime = timeToSecond(durationTimeStr);
                                duration.set(durationTime);
                                durationResultMap.put("durationVal", duration);
                                videoInfoMap.put("duration", durationResultMap);
                                listener.onVideoInfo(videoInfoMap, false);
                            }
                        } else {
                            Map<String, Object> frameSpeedMap = FFmpegLogRegex.regexFrameSpeed(line);
                            try {
                                if (frameSpeedMap.size() > 0) {
                                    String translatedTimeStr = (String) frameSpeedMap.get("time");
                                    int translatedTime = timeToSecond(translatedTimeStr);
                                    float percent = ((float) translatedTime / duration.get()) * 100.0f;
                                    BigDecimal temp = new BigDecimal(percent);
                                    listener.onPercent(temp, frameSpeedMap);
                                }
                            } catch (Exception e1) {
                                result.decrementAndGet();
                                listener.onThrowable(e1);
                                listener.onCountDown(result);
                            }
                        }
                    }
                    listener.onPercent(new BigDecimal(100), null);
                } catch (Exception e) {
                    e.printStackTrace();
                    result.decrementAndGet();
                    listener.onThrowable(e);
                    listener.onCountDown(result);
                }

            }
        };
        return readStdoutThread;
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
