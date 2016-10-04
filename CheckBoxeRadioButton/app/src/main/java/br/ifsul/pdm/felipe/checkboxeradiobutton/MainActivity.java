package br.ifsul.pdm.felipe.checkboxeradiobutton;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TextView txtHelloWorld;
    CheckBox cb;
    RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtHelloWorld = (TextView) findViewById(R.id.textView);
        cb = (CheckBox) findViewById(R.id.checkBox);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
    }

    public void onClickCheckBox(View v)
    {
        if (cb.isChecked())
            txtHelloWorld.setText("checkBox checado");
        else
            txtHelloWorld.setText("checkBox não checado");
    }

    public void onClickRadioGroup(View v)
    {
        switch (rg.getCheckedRadioButtonId())
        {
            case R.id.radioButton : txtHelloWorld.setText("Radio 1 checado");
                break;
            case R.id.radioButton2 : txtHelloWorld.setText("Radio 2 checado");
                break;
            case R.id.radioButton3 : txtHelloWorld.setText("Radio 3 checado");
                break;
            default: txtHelloWorld.setText("Nenhum radio button checado");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
