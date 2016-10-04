package com.example.pc.testearraystring;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SharedPreferences preferences = this.getSharedPreferences("preferencia", MODE_PRIVATE);
        String email = preferences.getString("email", null);
        ((TextView) findViewById(R.id.tvEmail)).setText(email);
    }
}
