package io.github.vanknight.ffmpegwrapper.listener;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface FfmpegLogListener {
    void onVideoInfo(Map<String, Object> videoInfo, boolean isLast);

    void onPercent(BigDecimal value, Map<String, Object> frameSpeedMap);

    void onThrowable(Exception ex);

    void onCountDown(AtomicInteger result);
}
