package com.byd.player.config;


/**
 * 
 * All constants should be defined here
 * 
 */
public class Constants {

    public static final int ERROR_DISMISS_THREE_SECONDS = 3000;
    public static final String VIDEO_PLAY_PARAMS = "VIDEO_PLAY_PARAMS";

    public static final String EXTENDED_DATA_SONG = "com.byd.player.PlayingSong";

    public static final String PLAYER_MSG = "com.byd.player.MSG";

    public static final String MUSIC_URL = "com.byd.player.URL";

    public static final String MUSIC_DURATION = "com.byd.player.Duration";

    public static final String MUSIC_CURRENT = "com.byd.player.Current";

    public class PlayerCommand {
        public static final int PLAY = 0;

        public static final int STOP = 1;
    }

    public class PlayerAction {
        //        public static final String ACTION_DURATION = "com.byd.player.ACTION_DURATION";
        //
        //        public static final String ACTION_UPDATE_CURRENT = "com.byd.player.ACTION_UPDATE_CURRENT";
    }
}
