package br.ifsul.pdm.felipe.trocacomigo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SplahScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //create the intent for login
                Intent intent = new Intent(SplahScreen.this, atvLogin.class);
                startActivity(intent);

                //clos this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
