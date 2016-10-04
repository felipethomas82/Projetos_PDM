package com.example.felipe.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    /*

Async Task
AsyncTask was written to provide a convenient, easy-to-use way to achieve background processing in Android apps,
without worrying too much about the low-level details(threads, message loops etc).
It provides callback methods that help to schedule tasks and also to easily update the UI whenever required.
AsyncTask runs a set of actions in a separate thread, can show progress during its work and show notification
when the task is completed.
However, there is a limit to the number of jobs that can be scheduled using AsyncTasks.

Runnable
Runnable is a command that can be performed. You should run in a separate thread.
Also you should develop a logic in this case how to update progress and how to notify when the task is finished.

Handler
Handler is more transparent of all and probably gives you more freedom; so if you want more control on things
you would choose Handler.
Handler is a special class that can handle messages that are sent to the handler's thread.

*/

    Handler handler = new Handler();
    ImageView iv ;
    ImageView iv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.imageView1);
        iv2 = (ImageView) findViewById(R.id.imageView2);

    }


    public void Clica(View v) {

        new Thread(new Runnable() {
            public void run() {

                final Bitmap imgAux =
                        loadImageFromNetwork("http://targettrust.com.br/img/header-logo_v2.png");
                //iv.setImageBitmap(imgAux);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(imgAux);
                    }
                });

                handler.post(new Runnable() {
                    public void run() {
                        iv.setImageBitmap(imgAux);
                    }
                });

            }

        }).start();
    }

    public Bitmap loadImageFromNetwork(String urlImg) {
        Bitmap img = null;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            URL url = new URL(urlImg);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            InputStream input = conexao.getInputStream();
            img = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            //tratar exceção
        }
        return img;
    }

    public void Clica2(View v) {

        new TrabalhoParalelo().execute("http://targettrust.com.br/img/header-logo_v2.png");

    }

	/*
	1 - Params, the type of the parameters sent to the task upon execution.
	2 - Progress, the type of the progress units published during the background computation.
	3 - Result, the type of the result of the background computation.
	*/

    class TrabalhoParalelo extends AsyncTask<String, String, String> {

        Bitmap imgAux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            imgAux = loadImageFromNetwork(params[0]);

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

}
