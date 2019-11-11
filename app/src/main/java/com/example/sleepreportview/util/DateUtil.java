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
            if (time_diff_s < 0) {
                // 如果是白天
                if (start.equals("20:00:00"))
                    // 如果是夜晚
                    time_diff_s = time_diff(start, "24:00:00") + time_diff("00:00:00", end);
                else
                    time_diff_s = 0;

            }

            return time_diff_s;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断  是否在对应的时间段里
     *
     * @param time “00：00：00”
     * @return 在时间段 true 反之false
     */
    public static boolean in20to24(String time) {
        try {
            Date date = cylinder_date_sdf.parse(time);
            Date start_date = cylinder_date_sdf.parse("20:00:00");
            Date end_date = cylinder_date_sdf.parse("24:00:00");
            assert date != null;
            assert start_date != null;
            assert end_date != null;
            if ((date.getTime() > start_date.getTime()) && (date.getTime() < end_date.getTime())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean in0to8(String time) {
        try {
            Date date = cylinder_date_sdf.parse(time);
            Date start_date = cylinder_date_sdf.parse("00:00:00");
            Date end_date = cylinder_date_sdf.parse("08:00:00");
            assert date != null;
            assert start_date != null;
            assert end_date != null;
            if ((date.getTime() > start_date.getTime()) && (date.getTime() < end_date.getTime())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean in8to20(String time) {
        try {
            Date date = cylinder_date_sdf.parse(time);
            Date start_date = cylinder_date_sdf.parse("08:00:00");
            Date end_date = cylinder_date_sdf.parse("20:00:00");
            assert date != null;
            assert start_date != null;
            assert end_date != null;
            if ((date.getTime() > start_date.getTime()) && (date.getTime() < end_date.getTime())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
