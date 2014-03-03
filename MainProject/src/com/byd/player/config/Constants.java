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

    private static final String PREF_AUDIO_FX = "audio_fx";

    public static final String MUSIC_SONG_POSITION = "com.byd.player.SongPosition";

    public static final String PLAYER_MSG = "com.byd.player.MSG";

    public static final String MUSIC_URL = "com.byd.player.URL";

    public static final String MUSIC_DURATION = "com.byd.player.Duration";

    public static final String MUSIC_CURRENT = "com.byd.player.Current";

    public static final String MUSIC_SEEK_TO = "com.byd.player.SeekTo";

    public static final String AUDIO_FX_ID = "com.byd.player.AudioFxId";

    public class PlayerCommand {
        public static final int PLAY = 0;

        public static final int STOP = 1;

        public static final int SEEK = 2;

        public static final int PAUSE = 3;

        public static final int CONTINUE_PLAY = 4;

        public static final int NEXT = 5;

        public static final int PREVIOUS = 6;

        public static final int AUDIO_FX = 7;
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

    public static final int getAudioFx(Context context) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        return pref.getInt(PREF_AUDIO_FX, 0);
    }

    public static final void setAudioFx(Context context, int audioFx) {
        SharedPreferences pref = context.getSharedPreferences(AUDIO_PREF, Context.MODE_PRIVATE);
        pref.edit().putInt(PREF_AUDIO_FX, audioFx).commit();
    }
}
