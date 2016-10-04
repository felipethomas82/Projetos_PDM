package com.example.felipe.crachadodrawer;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        teste();
        //testeHamburguer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private NavigationView mNavigationView;

    private int FILTER_ID = 10;

    public void teste(){

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        final Menu menu = mNavigationView.getMenu();
        TextView tv = new TextView(this);
        tv.setText("10");

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        p.setMargins(0, 0, 0, 0);

        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setPadding(0, 0, 10, 0);

        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(11);
        tv.setBackgroundResource(R.drawable.circulo_cracha);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(p);
        linearLayout.setPadding(0,40,200,0);
        //linearLayout.setBackgroundResource(R.drawable.borda);
        linearLayout.addView(tv);

        MenuItem item = menu.getItem(1);
        item.setActionView(linearLayout);

    }

    public void testeHamburguer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View vHamburguer = toolbar.getChildAt(0);
        //final Menu menu = toolbar.getMenu();
        TextView tv = new TextView(this);
        tv.setText("10");

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        p.setMargins(0, 0, 0, 0);

        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setPadding(0, 0, 10, 0);

        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(11);
        tv.setBackgroundResource(R.drawable.circulo_cracha);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(p);
        linearLayout.setPadding(0,40,200,0);
        linearLayout.setBackgroundResource(R.drawable.borda);
        linearLayout.addView(tv);
        toolbar.addView(linearLayout);
        //MenuItem item = menu.getItem(0);
        //item.setActionView(linearLayout);
    }
}
