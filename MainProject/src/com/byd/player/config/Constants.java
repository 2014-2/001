package com.byd.player.config;


/**
 * 
 * All constants should be defined here
 * 
 */
public class Constants {

    public static final int ERROR_DISMISS_THREE_SECONDS = 3000;
    public static final String VIDEO_PLAY_PARAMS = "VIDEO_PLAY_PARAMS";

    public static final String MUSIC_SONG_POSITION = "com.byd.player.SongPosition";

    public static final String PLAYER_MSG = "com.byd.player.MSG";

    public static final String MUSIC_URL = "com.byd.player.URL";

    public static final String MUSIC_DURATION = "com.byd.player.Duration";

    public static final String MUSIC_CURRENT = "com.byd.player.Current";

    public static final String MUSIC_SEEK_TO = "com.byd.player.SeekTo";

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
}
