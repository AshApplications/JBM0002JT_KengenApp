package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_exit);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PowerPreference.getDefaultFile().putBoolean(Constant.isRunning,false);
                try {
                    System.exit(0);
                } catch (Exception e) {
                    finishAffinity();
                }
            }
        }, 2000);

    }
}