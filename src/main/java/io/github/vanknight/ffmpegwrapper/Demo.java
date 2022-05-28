package io.github.vanknight.ffmpegwrapper;

import com.alibaba.fastjson2.JSON;
import io.github.vanknight.ffmpegwrapper.listener.FfmpegLogListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Demo {
    public static final String ffmpegPath = "D:\\Config\\ffmpeg\\bin\\ffmpeg.exe";


    public static void main(String[] args) {
        final String sourceVideo = "D:\\Config\\ffmpeg\\bin\\source.flv";
        final String outputVideo = "D:\\Config\\ffmpeg\\bin\\source.mp4";

        List<String> commands = new ArrayList<String>();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(sourceVideo);
        commands.add("-preset");
        commands.add("slow");
        commands.add("-c:v");
        commands.add("libx264");
        commands.add("-c:a");
        commands.add("aac");
        commands.add("-y");
        commands.add("-hide_banner");
        commands.add(outputVideo);

        int i = CMDTool.waitProcessCommand(commands, new FfmpegLogListener() {
            @Override
            public void onVideoInfo(Map<String, Object> videoInfo, boolean isLast) {
                System.out.println("VideoInfo: " + JSON.toJSONString(videoInfo));
            }

            @Override
            public void onPercent(BigDecimal value, Map<String, Object> frameSpeedMap) {
                String percentByTime = value.setScale(2, RoundingMode.HALF_UP).toString();
                System.out.println("转换进度: " + percentByTime + "%");
            }

            @Override
            public void onThrowable(Exception ex) {
                System.out.println("onThrowable: " + ex.toString());
            }

            @Override
            public void onCountDown(AtomicInteger result) {
                //System.out.println("onCountDown: " + result.get());
            }
        });
        System.out.println("输出: " + i);
    }
}
