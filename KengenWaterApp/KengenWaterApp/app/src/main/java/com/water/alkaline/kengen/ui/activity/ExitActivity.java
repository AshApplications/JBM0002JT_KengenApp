package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        getWindow().setLayout(-1, -1);
        getWindow().setBackgroundDrawable(null);
        setFinishOnTouchOutside(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    System.exit(0);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    finishAffinity();
                }
            }
        }, 2000);


    }
}