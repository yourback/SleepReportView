package com.example.sleepreportview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.sleepreportview.frame.ItemInfoBean;
import com.example.sleepreportview.frame.SleepTimingChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SleepTimingChart stc;

    // default item num
    private List<ItemInfoBean> dl = new ArrayList<>();

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
        initData();

        stc.load_data(20, 50, 30, dl);
    }

    private void initData() {
        dl.clear();
        if (false) {
            Map<String, String> m = new HashMap<>();
            m.put("22:10:20", "22:10:50");
            m.put("00:10:20", "00:12:00");
            m.put("02:10:20", "02:10:59");
            m.put("06:10:20", "06:30:20");
            m.put("07:10:20", "09:10:20");

            Map<String, String> m1 = new HashMap<>();
            m1.put("22:40:20", "22:40:40");
            m1.put("00:40:20", "00:45:20");
            m1.put("02:40:20", "02:59:20");
            m1.put("06:40:20", "06:50:20");
            m1.put("07:40:20", "08:40:20");

            Map<String, String> m2 = new HashMap<>();
            m2.put("22:00:20", "22:00:30");
            m2.put("00:00:20", "00:03:20");
            m2.put("02:00:20", "02:01:20");
            m2.put("06:00:20", "07:00:00");
            m2.put("07:00:20", "08:00:20");
            m2.put("11:00:20", "17:00:01");

            dl.add(new ItemInfoBean("深睡眠", ItemInfoBean.CIRCLE, R.color.colorPrimaryDark, m, ItemInfoBean.DATA_TYPE_LOW));
            dl.add(new ItemInfoBean("浅睡眠", ItemInfoBean.CIRCLE, R.color.colorPrimary, m1, ItemInfoBean.DATA_TYPE_MIDDLE));
            dl.add(new ItemInfoBean("快速眼动", ItemInfoBean.CIRCLE, R.color.colorAccent, m2, ItemInfoBean.DATA_TYPE_HIGH));
        } else {
            Map<String, String> m = new HashMap<>();
            m.put("22:10:20", "22:10:50");
            m.put("00:10:20", "00:12:00");
            m.put("02:10:20", "02:10:59");
            m.put("06:10:20", "06:30:20");
            m.put("07:10:20", "09:10:20");

            Map<String, String> m1 = new HashMap<>();
            m1.put("22:40:20", "22:40:40");
            m1.put("00:40:20", "00:45:20");
            m1.put("02:40:20", "02:59:20");
            m1.put("06:40:20", "06:50:20");
            m1.put("07:40:20", "08:40:20");

            Map<String, String> m4 = new HashMap<>();
            m4.put("20:00:10", "22:40:40");
            m4.put("04:40:40", "07:40:40");


            Map<String, String> m5 = new HashMap<>();
            m5.put("22:40:40", "00:20:40");

            Map<String, String> m2 = new HashMap<>();
            m2.put("00:00:20", "22:00:30");
            m2.put("10:00:20", "00:03:20");

            Map<String, String> m3 = new HashMap<>();
            m3.put("05:00:20", "22:00:30");
            m3.put("14:00:20", "00:03:20");

            Map<String, String> m7 = new HashMap<>();
            m7.put("05:00:20", "07:00:30");
            m7.put("14:00:20", "00:03:20");

//            dl.add(new ItemInfoBean("强鼾声", ItemInfoBean.TOP_RECT, R.color.strength_snore, m7, ItemInfoBean.DATA_TYPE_HIGH));
//            dl.add(new ItemInfoBean("中鼾声", ItemInfoBean.TOP_RECT, R.color.med_snore, m1, ItemInfoBean.DATA_TYPE_MIDDLE));
//            dl.add(new ItemInfoBean("弱鼾声", ItemInfoBean.TOP_RECT, R.color.weak_snore, m, ItemInfoBean.DATA_TYPE_LOW));
//            dl.add(new ItemInfoBean("仰卧", ItemInfoBean.BOTTOM_RECT, R.color.supine, m4, ItemInfoBean.DATA_TYPE_BOTTOM));
//            dl.add(new ItemInfoBean("侧卧", ItemInfoBean.BOTTOM_RECT, R.color.side_lying, m5, ItemInfoBean.DATA_TYPE_BOTTOM));
//            dl.add(new ItemInfoBean("干预成功", ItemInfoBean.TRIANGLE, R.color.snore_success, m2, ItemInfoBean.DATA_TYPE_TRIANGLE));
//            dl.add(new ItemInfoBean("干预失败", ItemInfoBean.TRIANGLE, R.color.snore_fail, m3, ItemInfoBean.DATA_TYPE_TRIANGLE));
            dl.add(new ItemInfoBean("1", ItemInfoBean.TOP_RECT, R.color.strength_snore, m7, ItemInfoBean.DATA_TYPE_HIGH));
            dl.add(new ItemInfoBean("2", ItemInfoBean.TOP_RECT, R.color.med_snore, m1, ItemInfoBean.DATA_TYPE_MIDDLE));
            dl.add(new ItemInfoBean("3", ItemInfoBean.TOP_RECT, R.color.weak_snore, m, ItemInfoBean.DATA_TYPE_LOW));
            dl.add(new ItemInfoBean("4", ItemInfoBean.BOTTOM_RECT, R.color.supine, m4, ItemInfoBean.DATA_TYPE_BOTTOM));
            dl.add(new ItemInfoBean("5", ItemInfoBean.BOTTOM_RECT, R.color.side_lying, m5, ItemInfoBean.DATA_TYPE_BOTTOM));
            dl.add(new ItemInfoBean("6", ItemInfoBean.TRIANGLE, R.color.snore_success, m2, ItemInfoBean.DATA_TYPE_TRIANGLE));
            dl.add(new ItemInfoBean("7", ItemInfoBean.TRIANGLE, R.color.snore_fail, m3, ItemInfoBean.DATA_TYPE_TRIANGLE));
        }
    }
}
