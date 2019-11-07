package com.example.sleepreportview.util;

import android.util.Log;
import android.util.StateSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static SimpleDateFormat cylinder_date_sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private static final String TAG = "DateUtil";

    /**
     * 时间差
     *
     * @return 返回相差多少秒
     */
    public static int time_diff(String start, String end) {
        try {
            long time_start = cylinder_date_sdf.parse(start).getTime() / 1000;
            Log.e(TAG, "time_diff: time_start :" + time_start);

            long time_end = cylinder_date_sdf.parse(end).getTime() / 1000;
            Log.e(TAG, "time_diff: time_end :" + time_end);
            int time_diff_s = (int) (time_end - time_start);
            if (time_diff_s < 0)
                time_diff_s = time_diff(start, "24:00:00") + time_diff("00:00:00", end);

            return time_diff_s;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
