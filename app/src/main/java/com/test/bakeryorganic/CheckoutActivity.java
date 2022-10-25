package com.test.bakeryorganic;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import android.widget.LinearLayout;

public class CheckoutActivity extends AppCompatActivity {

    RevealAnimation mRevealAnimation;
    boolean shouldAllowBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        Intent intent = this.getIntent();   //get the intent to recieve the x and y coords, that you passed before

        LinearLayout rootLayout = findViewById(R.id.root_layout); //there you have to get the root layout of your second activity
        mRevealAnimation = new RevealAnimation(rootLayout, intent, this);
        new Handler().postDelayed(() -> {
            mRevealAnimation.unRevealActivity();
        },5000);
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack) super.onBackPressed();

    }

}