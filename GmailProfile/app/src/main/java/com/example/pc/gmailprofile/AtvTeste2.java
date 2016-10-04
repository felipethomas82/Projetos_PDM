package com.example.pc.gmailprofile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AtvTeste2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_teste2);

        ImageView cardPerfil = (ImageView) findViewById(R.id.imageView1);
        cardPerfil.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                //Toast.makeText(AtvTeste2.this, "Teste", Toast.LENGTH_SHORT).show();

                CardView card = (CardView) findViewById(R.id.card);

                int cx = (card.getLeft() + card.getRight()) / 2;
                int cy = (card.getTop() + card.getBottom()) /2;

                int finalRadius = Math.max(card.getWidth(), card.getHeight());

                Animator anim = ViewAnimationUtils.createCircularReveal(card, cx, cy, 0, finalRadius);

                card.setVisibility(View.VISIBLE);
                anim.start();
            }
        });

        Button btnFechar = (Button) findViewById(R.id.btnFecharCard);
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                // previously visible view
                final View myView = findViewById(R.id.card);

                // get the center for the clipping circle
                int cx = (myView.getLeft() + myView.getRight()) / 2;
                int cy = (myView.getTop() + myView.getBottom()) / 2;

                // get the initial radius for the clipping circle
                int initialRadius = myView.getWidth();

                // create the animation (the final radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        myView.setVisibility(View.INVISIBLE);
                    }
                });

                // start the animation
                anim.start();
            }
        });

        Button btn2 = (Button) findViewById(R.id.btn2);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AtvTeste2.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }//onCreate
}
