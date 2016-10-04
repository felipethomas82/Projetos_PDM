package com.example.felipe.mp3player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Musica.class);
                intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                startService(intent);
            }
        });

        Button btnParar = (Button) findViewById(R.id.btnParar);
        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Musica.class);
                stopService(intent);
            }
        });

        Button btnProxima = (Button) findViewById(R.id.btnProxima);
        btnProxima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Musica.class);
                intent.putExtra("Proxima", true);
                startService(intent);
            }
        });

        Button btnPausar = (Button) findViewById(R.id.btnPausar);
        btnPausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Musica.class);
                intent.putExtra("Pausar", true);
                startService(intent);
            }
        });

    }
}
