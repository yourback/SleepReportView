package com.example.sleepreportview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.sleepreportview.frame.SleepTimingChart;

public class MainActivity extends AppCompatActivity {

    private SleepTimingChart stc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stc = findViewById(R.id.stc);
        stc.setOnDayTimeClick(new SleepTimingChart.OnDayTimeClick() {
            @Override
            public void onDaytimeClick() {
                Toast.makeText(MainActivity.this, "切换到白天了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNightClick() {
                Toast.makeText(MainActivity.this, "切换到夜晚了", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
