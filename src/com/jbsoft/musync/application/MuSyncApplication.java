
package com.jbsoft.musync.application;

import com.jbsoft.musync.services.PlayerService;

import android.app.Application;
import android.content.Intent;
import android.graphics.Typeface;

public class MuSyncApplication extends Application {

    public Typeface font;

    @Override
    public void onCreate() {

        super.onCreate();
        initializeTypefaces();
        startPlayerService();
    }

    // Starts the player service on Application launch or on restart of a killed
    // process
    private void startPlayerService() {
        Intent i = new Intent(this, PlayerService.class);
        startService(i);

    }

    private void stopPlayerService() {

        Intent i = new Intent(this, PlayerService.class);
        stopService(i);
    }

    private void initializeTypefaces() {

        Fonts.ROBOTO_LIGHT = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        Fonts.ROBOTO_ITALIC = Typeface.createFromAsset(getAssets(), "fonts/roboto_lightitalic.ttf");
        Fonts.ROBOTO_REGULAR = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");

    }

    public static class Fonts {

        public static Typeface ROBOTO_REGULAR;

        public static Typeface ROBOTO_ITALIC;

        public static Typeface ROBOTO_LIGHT;

    }

}
