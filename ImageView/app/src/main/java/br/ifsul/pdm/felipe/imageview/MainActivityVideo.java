package br.ifsul.pdm.felipe.imageview;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.VideoView;


public class MainActivityVideo extends ActionBarActivity {

    private VideoView vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_video);

        vv = (VideoView) findViewById(R.id.videoView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickStart(View v)
    {
        //o system getproperty file.separator é só para pegar a barra ou contrabarra, conforme o sistema
        String path = "android.resource://" + getPackageName() + System.getProperty("file.separator") + R.raw.small;

        vv.setVideoURI(Uri.parse(path));
        vv.start();
    }
}
