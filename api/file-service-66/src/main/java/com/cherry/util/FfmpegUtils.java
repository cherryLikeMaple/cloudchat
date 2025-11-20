package com.cherry.util;

import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FfmpegUtils {

    // 如果你的 ffmpeg / ffprobe 不在 PATH，可改成绝对路径
    private static final String FFMPEG = "ffmpeg";
    private static final String FFPROBE = "ffprobe";

    @Data
    public static class VideoInfo {
        private Integer width;
        private Integer height;
        private Integer duration; // 秒
    }

    /**
     * 通过 ffprobe 获取视频宽高和时长
     */
    public static VideoInfo getVideoInfo(File videoFile) throws IOException, InterruptedException {
        VideoInfo info = new VideoInfo();

        // 1. 获取宽高
        ProcessBuilder pbSize = new ProcessBuilder(
                FFPROBE,
                "-v", "error",
                "-select_streams", "v:0",
                "-show_entries", "stream=width,height",
                "-of", "csv=s=x:p=0",
                videoFile.getAbsolutePath()
        );
        pbSize.redirectErrorStream(true);
        Process pSize = pbSize.start();
        String sizeLine;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(pSize.getInputStream()))) {
            sizeLine = br.readLine();
        }
        pSize.waitFor();
        if (sizeLine != null && sizeLine.contains("x")) {
            String[] arr = sizeLine.split("x");
            info.setWidth(Integer.parseInt(arr[0]));
            info.setHeight(Integer.parseInt(arr[1]));
        }

        // 2. 获取时长（单位秒，float）
        ProcessBuilder pbDuration = new ProcessBuilder(
                FFPROBE,
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                videoFile.getAbsolutePath()
        );
        pbDuration.redirectErrorStream(true);
        Process pDuration = pbDuration.start();
        String durationLine;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(pDuration.getInputStream()))) {
            durationLine = br.readLine();
        }
        pDuration.waitFor();
        if (durationLine != null) {
            double seconds = Double.parseDouble(durationLine);
            info.setDuration((int) Math.floor(seconds));
        }

        return info;
    }

    /**
     * 截取视频第一帧作为封面图
     *
     * @param videoFile 视频文件
     * @param coverFile 输出封面图文件（jpg/png）
     */
    public static void generateCover(File videoFile, File coverFile) throws IOException, InterruptedException {
        // 截取第 1 秒的一帧，可根据需要改成 0.5s 等
        ProcessBuilder pb = new ProcessBuilder(
                FFMPEG,
                "-y",                             // 覆盖输出
                "-ss", "00:00:01",
                "-i", videoFile.getAbsolutePath(),
                "-vframes", "1",                  // 只要一帧
                "-q:v", "2",                      // 画质 2（1-31，数值越小越清晰）
                coverFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            while (br.readLine() != null) {
                // 读完输出即可，不用处理
            }
        }
        p.waitFor();
    }
}
