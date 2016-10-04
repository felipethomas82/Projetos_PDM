package com.example.felipe.mp3player;

/**
 * Created by Felipe on 18/05/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

public class Musica extends Service {

    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Boolean pausar = false;
        Boolean avancar = false;

        try {
            avancar = intent.getBooleanExtra("Proxima", false);
            pausar = intent.getBooleanExtra("Pausar", false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }



        if (!avancar) {
            Log.d("Musica", "INICIOU");
            if (!player.isPlaying())
                player.start();
        }
        else {
            player.stop();
            player = MediaPlayer.create(this, R.raw.ptx02);
            Log.d("Musica", "START2");
            player.setVolume(100,100);
            player.start();
        }

        return null;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Musica", "ON CREATE");
        player = MediaPlayer.create(this, R.raw.ptx01);
        player.setVolume(100, 100);
        player.setLooping(true);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Musica", "ID: " + startId);

        Boolean pausar = false;
        Boolean avancar = false;

       try {
           avancar = intent.getBooleanExtra("Proxima", false);
           pausar = intent.getBooleanExtra("Pausar", false);
       } catch (NullPointerException e) {
           e.printStackTrace();
       }


        if (pausar) {
            if (player.isPlaying())
                player.pause();
            else
                player.start();

            return START_NOT_STICKY;
        }

        if (!avancar) {
            Log.d("Musica", "INICIOU");
            if (!player.isPlaying())
                player.start();
        }
        else {
            player.stop();
            player = MediaPlayer.create(this, R.raw.ptx02);
            Log.d("Musica", "START2");
            player.setVolume(100,100);
            player.start();
        }
        return START_NOT_STICKY;

    }

    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
