package io.github.vanknight.ffmpegwrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FFmpegLogRegex {

    //转码匹配 - 速率
    private static final String REGEX_FRAME_SPEED = "(\\s*)frame=(\\s*\\d+) fps=(\\s*\\d+\\.?\\d*) q=(\\d+\\.?\\d*) size=(\\s*\\d+)kB time=(\\d+:\\d+:\\d+\\.?\\d*) bitrate=(\\s*\\d+\\.?\\d*)kbits/s speed=(\\d+\\.?\\d*)x";
    //视频信息匹配 - 时长
    private static final String REGEX_VIDEO_INFO_DURATION = "(\\s*)Duration: (\\d+:\\d+:\\d+\\.?\\d*), start: (\\d+\\.?\\d*), bitrate: (\\d+) kb/s";


    public static Map<String, Object> regexFrameSpeed(String logLine) {
        List<String> result = matcher(REGEX_FRAME_SPEED, logLine);
        Map<String, Object> resultMap = new HashMap<>();
        if (result.size() == 8) {
            String startBlank = result.get(0);
            String frameStr = result.get(1);
            String fpsStr = result.get(2);
            String qStr = result.get(3);
            String sizeStr = result.get(4);
            String timeStr = result.get(5);
            String bitrateStr = result.get(6);
            String speedStr = result.get(7);
            resultMap.put("frame", Integer.valueOf(frameStr));
            resultMap.put("fps", Float.valueOf(fpsStr));
            resultMap.put("q", Float.valueOf(qStr));
            resultMap.put("size", Integer.valueOf(sizeStr));
            resultMap.put("time", timeStr);
            resultMap.put("bitrate", Float.valueOf(bitrateStr));
            resultMap.put("speed", Float.valueOf(speedStr));
        }
        return resultMap;
    }

    public static Map<String, Object> regexVideoDuration(String logLine) {
        List<String> result = matcher(REGEX_VIDEO_INFO_DURATION, logLine);
        Map<String, Object> resultMap = new HashMap<>();
        if (result.size() == 4) {
            String startBlank = result.get(0);
            String durationStr = result.get(1);
            String startStr = result.get(2);
            String bitrateStr = result.get(3);
            resultMap.put("duration", durationStr);
            resultMap.put("start", Float.valueOf(startStr));
            resultMap.put("bitrate", Integer.valueOf(bitrateStr));
        }
        return resultMap;
    }


    /**
     * 字符串提取
     *
     * @param regex 正则匹配
     * @param test  目标字符串
     * @return 匹配值
     */
    public static List<String> matcher(String regex, String test) {
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(test);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                result.add(matcher.group(i).trim());
            }
        }
        return result;
    }
}
