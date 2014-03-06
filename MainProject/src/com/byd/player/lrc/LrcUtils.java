package com.byd.player.lrc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    public static String encodeOfFile(File file){
        String code = "UTF-8";
        if(file==null || !file.exists()){
            return code;
        }
        try {
            BufferedInputStream bin = new BufferedInputStream( new FileInputStream(file));
            int p = (bin.read() << 8) + bin.read();
            //其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
            switch (p) {
                case 0xefbb:
                    code = "UTF-8";
                    break;
                case 0xfffe:
                    code = "Unicode";
                    break;
                case 0xfeff:
                    code = "UTF-16BE";
                    break;
                case 0x5c75:
                    code = "ANSI|ASCII" ;
                    break ;
                default:
                    code = "GBK";
            }
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return code;
    }
}
