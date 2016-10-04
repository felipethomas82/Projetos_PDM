package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;


public class atvMostrarFotoFull extends ActionBarActivity {

    ImageView ivPhoto;

    String photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_mostrar_foto_full);

        ivPhoto = (ImageView) findViewById(R.id.ivPhotoFullShow);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        if (params != null)
        {
            photoName = params.getString("photoName");

            //String picsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ File.separator;
            File file = new File(photoName);
            Bitmap bp = Photo.decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
            // BitmapFactory.decodeFile(file.getAbsolutePath());
            ivPhoto.setImageBitmap(bp);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_mostrar_foto_full, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        //return super.onOptionsItemSelected(item);
    }

    public void btnCoverDefineClick(View v){
        Intent intent = new Intent();
        intent.putExtra("action", "setCover");
        intent.putExtra("photoName", photoName);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void btnRemovePhotoClick(View v){
        Intent intent = new Intent();
        intent.putExtra("action", "removePhoto");
        intent.putExtra("photoName", photoName);
        setResult(RESULT_OK,intent);
        finish();
    }
}
