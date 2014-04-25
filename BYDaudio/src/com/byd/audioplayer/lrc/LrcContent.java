package com.byd.audioplayer.lrc;

public class LrcContent {
    private String lrcStr;  // lyric string
    private int lrcTime;    // current lyric time
    public String getLrcStr() {
        return lrcStr;
    }
    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }
    public int getLrcTime() {
        return lrcTime;
    }
    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }
}
