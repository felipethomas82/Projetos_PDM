package com.example.pc.mostrandoimagens;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTrocar = (Button) findViewById(R.id.btnTroca);
        btnTrocar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv = (ImageView) findViewById(R.id.imagem);
                iv.setImageResource(R.drawable.madruga);
            }
        });

        vv = (VideoView) findViewById(R.id.videoView);
    }

    public void onClickTrocar(View v) {
        ImageView iv = (ImageView) findViewById(R.id.imagem);
        iv.setImageResource(R.drawable.madruga);
    }

    public void onClickReproduz(View v) {
        String caminho = "android.resource://" + getPackageName() +
                System.getProperty("file.separator") + R.raw.small;

        vv.setVideoURI(Uri.parse(caminho));
        vv.start();

        Log.i("CAMINHO", caminho);
    }
}
