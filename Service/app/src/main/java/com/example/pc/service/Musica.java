package com.example.pc.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by PC on 18/05/2016.
 */
public class Musica extends Service {
    MediaPlayer mp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("Musica", "CRIOU O SERVICE");
        mp = MediaPlayer.create(this, R.raw.ptx01);
        mp.setVolume(100,100);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Musica", "STARTOU O SERVICE => " + startId);

        Boolean pausa = false;

        try {
            pausa = intent.getBooleanExtra("pause", false);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        if (pausa) {
            if (mp.isPlaying())
                mp.pause();
            else
                mp.start();
        } else {
            mp.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mp.stop();
        stopSelf();
        super.onDestroy();
    }
}
