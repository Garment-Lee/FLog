package com.ligf.logtest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ligf.flog.FLog;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mStartBtn;

    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        init();
    }

    private void initViews(){
        mStartBtn = findViewById(R.id.btn_start);
        mStartBtn.setOnClickListener(this);
    }

    private void init(){
        FLog.setSaveToFileFlag(true);
        FLog.setShowLogFlag(true);
//        FLog.setSaveFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FLogTest");
        FLog.init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            try {
                                Thread.sleep(3000);
                                num ++;
                                FLog.i("test log:" + num);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
            default:

                break;

        }
    }
}
