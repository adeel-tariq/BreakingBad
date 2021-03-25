package com.task.breakingbad.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.task.breakingbad.R;
import com.task.breakingbad.databinding.ActivitySplashBinding;
import com.task.breakingbad.ui.characters.BreakingBadCharacterActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        Glide.with(this).load(R.drawable.splash_2).centerCrop().into(mBinding.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, BreakingBadCharacterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                finish();
            }
        }, 1500);
    }
}