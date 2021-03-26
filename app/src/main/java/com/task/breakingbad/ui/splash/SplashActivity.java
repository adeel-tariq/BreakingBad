package com.task.breakingbad.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.task.breakingbad.R;
import com.task.breakingbad.databinding.ActivitySplashBinding;
import com.task.breakingbad.ui.characters.BreakingBadCharacterActivity;

// forces splash screen to make app look  bit nice it shows an image for 1.5 seconds
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        Glide.with(this).load(R.drawable.splash_2).centerCrop().into(mBinding.splash);

        // handler which after 1.5 seconds move to next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // intent for breaking bad characters list activity
                Intent intent = new Intent(SplashActivity.this, BreakingBadCharacterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // adding flag so activity is opened to be new
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out); // activity page changing animation
                finish(); // closing splash screen so if user press back than splash is not shown again from breaking bad activity
            }
        }, 1500);
    }
}