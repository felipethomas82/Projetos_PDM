package com.example.pc.asynctask2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    ImageView iv2;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.imageView1);
        iv2 = (ImageView) findViewById(R.id.imageView2);
    }


    public void Clica(View v) {

        new Thread(new Runnable() {
            @Override
            public void run() {
               final Bitmap imgAux = loadImageFromUrl("http://targettrust.com.br/img/header-logo_v2.png");

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        iv.setImageBitmap(imgAux);
//                    }
//                });

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(imgAux);
                    }
                });
            }
        }).start();
    }

    public void clica2(View v) {
        new TrabalhoParalelo().execute("http://targettrust.com.br/img/header-logo_v2.png");
    }


    class TrabalhoParalelo extends AsyncTask<String, String, String> {

        Bitmap imgAux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            imgAux = loadImageFromUrl(params[0]);

            return "FIM";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            iv2.setImageBitmap(imgAux);
        }
    }


    public Bitmap loadImageFromUrl(String url){
        Bitmap img = null;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            URL urlImg = new URL(url);
            HttpURLConnection conexao = (HttpURLConnection) urlImg.openConnection();
            InputStream input = conexao.getInputStream();
            img = BitmapFactory.decodeStream(input);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }


}
