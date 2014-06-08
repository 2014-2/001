package com.byd.audioplayer.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.universalchardet.UniversalDetector;

import android.util.Log;

public class LrcUtils {

    public static List<LrcContent> readLRC(String lrcPath) {
        if (!isLrcFileExist(lrcPath)) {
            return null;
        }
        File f = new File(lrcPath);

        List<LrcContent> lrcList = new ArrayList<LrcContent>();

        try {
            FileInputStream fis = new FileInputStream(f);
            String encode = encodeOfFile(f);
            InputStreamReader isr = new InputStreamReader(fis, encode);
            BufferedReader br = new BufferedReader(isr);
            String s = "";
            LrcContent lrcContent;
            while((s = br.readLine()) != null) {
                lrcContent = new LrcContent();
                // replace the '[' and ']' in lyric time
                s = s.replace("[", "");
                s = s.replace("]", "@");

                // separate the '@' character
                String splitLrcData[] = s.split("@");
                if(splitLrcData.length > 1) {
                    lrcContent.setLrcStr(splitLrcData[1]);

                    // get the time of lyric
                    int lrcTime = time2Str(splitLrcData[0]);

                    lrcContent.setLrcTime(lrcTime);

                    lrcList.add(lrcContent);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcList;
    }

    public static boolean isLrcFileExist(String path) {
        File f = new File(path);
        return f.exists();
    }

    /**
     * 灏嗘瓕璇嶄腑鐨勬椂闂磋浆鎹㈡垚姣
     * 
     * @param timeStr 鏃堕棿瀛楃涓�
     * @return 杞崲鎴愭绉掔殑鏃堕棿
     */
    public static int time2Str(String timeStr) {
        timeStr = timeStr.replace(":", ".");
        timeStr = timeStr.replace(".", "@");

        String timeData[] = timeStr.split("@");

        // convert time to integer
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);

        // convert second to millisecond
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
        return currentTime;
    }

    /**
     * 鑾峰彇姝岃瘝鏂囦欢缂栫爜绫诲瀷
     * 
     * @param file 姝岃瘝鏂囦欢
     * @return 缂栫爜绫诲瀷
     */
    public static String encodeOfFile(File file) {
        byte[] buf = new byte[4096];
        String fileName = file.getAbsolutePath();
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        UniversalDetector detector = new UniversalDetector(null);

        int nread;
        try {
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            Log.v("LrcUtils", "Detected encoding = " + encoding);
        } else {
            Log.v("LrcUtils", "No encoding detected.");
            encoding = "UTF-8";
        }
        detector.reset();
        return encoding;
    }

    /**
     * 鏇挎崲姝屾洸鏂囦欢鎵╁睍鍚嶄负.lrc锛岀敤浜庡鎵句笌姝屾洸鍚嶇О鐩稿悓鐨勬瓕璇嶆枃浠�
     * 
     * @param path 姝屾洸璺緞
     * @return 姝岃瘝鏂囦欢璺緞
     */
    public static String replaceExtensionToLrc(String path) {
        return path.replaceAll("\\.\\w+$", ".lrc");
    }
}
