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

    private static final String PREF_PLAY_ORDER = "play_order";

    public static final String PREF_PLAY_ORDER_STATUS = "play_order_status";

    private static final String PREF_LOOP_MODE = "loop_mode";

    public static final String PREF_LOOP_MODE_STATUS = "loop_mode_status";

    private static final String PREF_AUDIO_FX = "audio_fx";

    public static final String MUSIC_SONG_POSITION = "com.byd.player.SongPosition";

    public static final String PLAYER_MSG = "com.byd.player.MSG";

    public static final String MUSIC_URL = "com.byd.player.URL";

    public static final String MUSIC_DURATION = "com.byd.player.Duration";

    public static final String MUSIC_CURRENT = "com.byd.player.Current";

    public static final String MUSIC_SEEK_TO = "com.byd.player.SeekTo";

    public static final String USB_REGIX = "/mnt/udisk/";

    public static final String AUDIO_FX_ID = "com.byd.player.AudioFxId";

    public static final String IS_AUX_CONNECTED = "com.byd.player.IsAuxConnected";

    public class PlayerCommand {
        public static final int PLAY = 0;

        public static final int STOP = 1;

        public static final int SEEK = 2;

        public static final int PAUSE = 3;

        public static final int CONTINUE_PLAY = 4;

        public static final int NEXT = 5;

        public static final int PREVIOUS = 6;

        public static final int AUDIO_FX = 7;

        public static final int PLAY_POSITION = 8;
    }

    public class AudioFx {
        public static final int NONE = 0;

        public static final int CLASSICAL = 1;

        public static final int DANCE = 2;

        public static final int FLAT = 3;

        public static final int FOLK = 4;

        public static final int HEAVYMETAL = 5;

        public static final int HIPHOP = 6;

        public static final int JAZZ = 7;

        public static final int POP = 8;

        public static final int ROCK = 9;
    }

    public static final short[][] SOUND_EFFECT_LEVEL = {
        {300, 0, 0, 0, 300}, // NONE
        {600, 0, 0, 0, 600}, // CLASSICAL
        {1000, 600, -400, 800, 800}, // DANCE
        {1200, 0, 400, 800, 200}, //FLAT
        {0, 0, 0, 0, 0}, // FOLK
        {600, 0, 0, 400, -200}, //HEAVYMETAL
        {800, 200, 1800, 600, 0}, // HIPHOP
        {1000, 600, 0, 200, 600}, // JAZZ
        {800, 400, -400, 400, 1000}, // POP
        {-200, 400, 1000, 200, -400} // ROCK
    };

    public class PlayOrder {
        public static final int ORDER_PLAY = 0;

        public static final int RANDOM_PLAY = 1;
    }

    public class LoopMode {
        public static final int LIST_LOOP = 2;

        public static final int SINGLE_LOOP = 3;
    }

    public static final int getPlayOrder(Context context) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        return pref.getInt(PREF_PLAY_ORDER, PlayOrder.ORDER_PLAY);
    }

    public static final void setPlayOrder(Context context, int order) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        pref.edit().putInt(PREF_PLAY_ORDER, order).commit();
    }

    public static final int getLoopMode(Context context) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        return pref.getInt(PREF_LOOP_MODE, LoopMode.LIST_LOOP);
    }

    public static final void setLoopMode(Context context, int mode) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        pref.edit().putInt(PREF_LOOP_MODE, mode).commit();
    }

    public static final boolean isPlayOrderChecked(Context context) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_PLAY_ORDER_STATUS, true);
    }

    public static final boolean isLoopModeChecked(Context context) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_LOOP_MODE_STATUS, false);
    }

    public static final void setCheckedStatus(Context context, String key, boolean isChecked) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        pref.edit().putBoolean(key, isChecked).commit();
    }

    public static final int getAudioFx(Context context) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        return pref.getInt(PREF_AUDIO_FX, 0);
    }

    public static final void setAudioFx(Context context, int audioFx) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        pref.edit().putInt(PREF_AUDIO_FX, audioFx).commit();
    }
}
