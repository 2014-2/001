package com.byd.player.config;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * 
 * All constants should be defined here
 * 
 */
public class Constants {

    public static final int ERROR_DISMISS_THREE_SECONDS = 3000;

    public static final String VIDEO_PLAY_PARAMS = "VIDEO_PLAY_PARAMS";

    private static final String AUDIO_PREF = "audio.pref";

    private static final String PREF_SINGLE_LOOP = "single_loop";

    private static final String PREF_RANDOM_PLAY = "random_play";

    public static final String MUSIC_SONG_POSITION = "com.byd.player.SongPosition";

    public static final String PLAYER_MSG = "com.byd.player.MSG";

    public static final String MUSIC_URL = "com.byd.player.URL";

    public static final String MUSIC_DURATION = "com.byd.player.Duration";

    public static final String MUSIC_CURRENT = "com.byd.player.Current";

    public static final String MUSIC_SEEK_TO = "com.byd.player.SeekTo";
    
    public static final String USB_REGIX = "/mnt/udisk/";

    public class PlayerCommand {
        public static final int PLAY = 0;

        public static final int STOP = 1;

        public static final int SEEK = 2;

        public static final int PAUSE = 3;

        public static final int CONTINUE_PLAY = 4;

        public static final int NEXT = 5;

        public static final int PREVIOUS = 6;
    }

    public class PlayerAction {
        //        public static final String ACTION_DURATION = "com.byd.player.ACTION_DURATION";
        //
        //        public static final String ACTION_UPDATE_CURRENT = "com.byd.player.ACTION_UPDATE_CURRENT";
    }

    public static final boolean getSingleLoopStatus(Context context) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_SINGLE_LOOP, false);
    }

    public static final void setSingleLoopStatus(Context context, boolean isSingleLoop) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        pref.edit().putBoolean(PREF_SINGLE_LOOP, isSingleLoop).commit();
    }

    public static final boolean getRandomPlayStatus(Context context) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_RANDOM_PLAY, false);
    }

    public static final void setRandomPlayStatus(Context context, boolean isRandomPlay) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        pref.edit().putBoolean(PREF_RANDOM_PLAY, isRandomPlay).commit();
    }
}
