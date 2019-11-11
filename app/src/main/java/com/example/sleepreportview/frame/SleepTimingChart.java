package com.example.sleepreportview.frame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.sleepreportview.R;
import com.example.sleepreportview.util.DateUtil;
import com.example.sleepreportview.util.DrawUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SleepTimingChart extends View {


    private static final String TAG = "SleepTimingChart";

    private Context mContext;

    // is daytime has data ?
    private boolean has_day_time = true;
    // current show  daytime / nighttime
    private boolean isDayTime = false;
    private static final String DAYTIME = "08:00";
    private static final String NIGHTTIME = "20:00";


    // current report type
    private int current_report_type = SNORE_REPORT;
    private static final int SLEEP_REPORT = 0;
    private static final int SNORE_REPORT = 1;

    // width / height
    private float ratio = 1;


    // text paint  20:00 \ 08:00   深睡眠  浅睡眠 so on
    private Paint text_paint;
    private float text_size = 10;

    // big circle
    private Paint side_circle_paint;
    private float radius_side_circle;

    // little circle
    private Paint middle_circle_paint;
    private float radius_middle_circle;

    // item info circle
    private float radius_item_info_circle;
    // item info text paint
    private Paint item_text_paint;

    // to get text width
    Rect bounds = new Rect();

    // default item num
    private List<ItemInfoBean> dl = new ArrayList<>();

    // baseline paint
    private Paint baseline_paint;


    // cylinder paint
    private Paint cylinder_paint;


    // 已经选择的白天图标
    private Bitmap daytime_selected_bitmap;

    // 未选择的白天图标
    private Bitmap daytime_normal_bitmap;

    // 已经选择的夜晚图标
    private Bitmap night_selected_bitmap;

    // 未选择的夜晚图标
    private Bitmap night_normal_bitmap;
    // 夜晚图标的大小
    private Rect night_bitmap_rect;
    // 夜晚图标显示区域
    private Rect night_bitmap_show_rect;
    // 白天图标的大小
    private Rect daytime_bitmap_rect;
    // 白天图标显示区域
    private Rect daytime_bitmap_show_rect;

    // 顶部字体画笔
    private Paint top_paint;

    // 强中弱占百分比
    private int strength_percent = 20;
    private int med_percent = 45;
    private int weak_percent = 35;


    public SleepTimingChart(Context context) {
        super(context);
        mContext = context;
    }

    public SleepTimingChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SleepTimingChart);

        current_report_type = ta.getInteger(R.styleable.SleepTimingChart_report_type, SLEEP_REPORT);
        ratio = ta.getFloat(R.styleable.SleepTimingChart_ratio, current_report_type == SLEEP_REPORT ? 1.5f : 1.0f);
        text_size = ta.getDimension(R.styleable.SleepTimingChart_text_size, 1);
        radius_side_circle = ta.getDimension(R.styleable.SleepTimingChart_radius_side_circle, 1);
        radius_middle_circle = ta.getDimension(R.styleable.SleepTimingChart_radius_middle_circle, 1);
        radius_item_info_circle = ta.getDimension(R.styleable.SleepTimingChart_radius_item_info_circle, 1);
        ta.recycle();
        initPaint();
        initBitmap();
    }

    private void initBitmap() {

        daytime_selected_bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.daytime_selected);
        daytime_normal_bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.daytime_normal);
        night_selected_bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.night_selected);
        night_normal_bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.night_normal);
        night_bitmap_rect = new Rect();
        night_bitmap_show_rect = new Rect(0, 0, night_normal_bitmap.getWidth(), night_normal_bitmap.getHeight());

        daytime_bitmap_rect = new Rect();
        daytime_bitmap_show_rect = new Rect(0, 0, daytime_selected_bitmap.getWidth(), daytime_selected_bitmap.getHeight());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (x < night_bitmap_rect.right && night_bitmap_rect.left < x && y > night_bitmap_rect.top && y < night_bitmap_rect.bottom) {
                // 当前不是黑夜
                if (isDayTime)
                    if (onDayTimeClick != null) {
                        isDayTime = false;
                        onDayTimeClick.onNightClick();
                        invalidate();
                    }
            } else if (x < daytime_bitmap_rect.right && daytime_bitmap_rect.left < x && y > daytime_bitmap_rect.top && y < daytime_bitmap_rect.bottom) {
                // 当前显示的不是白天，且白天有数据
                if (isHas_day_time() && !isDayTime)
                    if (onDayTimeClick != null) {
                        isDayTime = true;
                        onDayTimeClick.onDaytimeClick();
                        invalidate();
                    }
            }
        }

        return super.onTouchEvent(event);
    }



    /**
     * init paint color width weight and so on
     */
    private void initPaint() {
        // time text paint init
        text_paint = new Paint();
        text_paint.setColor(ContextCompat.getColor(mContext, R.color.color_text));
        text_paint.setAntiAlias(true);
        text_paint.setTextSize(text_size);
        text_paint.setStyle(Paint.Style.FILL);
        text_paint.setTextAlign(Paint.Align.CENTER);

        // big circle paint init
        side_circle_paint = new Paint();
        side_circle_paint.setAntiAlias(true);
        side_circle_paint.setColor(ContextCompat.getColor(mContext, R.color.color_big_circle));
        side_circle_paint.setStrokeWidth(1);

        // little circle paint init
        middle_circle_paint = new Paint();
        middle_circle_paint.setAntiAlias(true);
        middle_circle_paint.setColor(ContextCompat.getColor(mContext, R.color.color_little_circle));
        middle_circle_paint.setStrokeWidth(1);


        // item text paint init
        item_text_paint = new Paint();
        item_text_paint.setColor(Color.BLACK);
        item_text_paint.setAntiAlias(true);
        item_text_paint.setTextSize(text_size);
        item_text_paint.setStyle(Paint.Style.FILL);
        item_text_paint.setTextAlign(Paint.Align.LEFT);


        // baseline_paint init
        baseline_paint = new Paint();
        baseline_paint.setColor(Color.BLACK);
        baseline_paint.setAntiAlias(true);
        baseline_paint.setStrokeWidth(3);
        baseline_paint.setStyle(Paint.Style.FILL);


        // cylinder_paint init
        cylinder_paint = new Paint();
        cylinder_paint.setAntiAlias(true);
        cylinder_paint.setStrokeWidth(1);
        cylinder_paint.setStyle(Paint.Style.FILL);

        // 顶部字体画笔
        top_paint = new Paint();
        top_paint.setAntiAlias(true);
        top_paint.setStrokeWidth(1);
        top_paint.setTextSize(text_size);
        top_paint.setTextAlign(Paint.Align.CENTER);
    }

    public void load_data(int i, int i1, int i2,List<ItemInfoBean> dl) {
        weak_percent = i;
        med_percent = i1;
        strength_percent = i2;

        this.dl = dl;
        for (ItemInfoBean iib : this.dl) {
            for (Map.Entry<String, String> e : iib.getData_map().entrySet()) {
                // 如果 有结束点 在 8到20点 也就是白天的话 就设置为有白天按钮
                if (DateUtil.in8to20(e.getValue())) {
                    setHas_day_time(true);
                    return;
                } else {
                    setHas_day_time(false);
                }
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        float side_circle_y = getMeasuredHeight() - getPaddingBottom()
                - radius_side_circle // show all circle
                - text_size * 1 - radius_side_circle // blank for time text
                - radius_side_circle * 4 // blank for item info text
                ;
        // 两边的大圆
        float start_side_circle_x = getPaddingStart() + radius_side_circle + text_size * 2;
        float end_side_circle_x = getMeasuredWidth() - getPaddingStart() - radius_side_circle - text_size * 2;
        canvas.drawCircle(start_side_circle_x, side_circle_y, radius_side_circle, side_circle_paint);
        canvas.drawCircle(end_side_circle_x, side_circle_y, radius_side_circle, side_circle_paint);


        // 中间的小圆
        float middle_circle_width = end_side_circle_x - start_side_circle_x;
        float middle_circle_width_unit = middle_circle_width / 12;
        for (int i = 1; i < 12; i++) {
            float middle_circle_x = start_side_circle_x + i * middle_circle_width_unit;
            canvas.drawCircle(middle_circle_x, side_circle_y, radius_middle_circle, middle_circle_paint);
        }

        // 两边的时间
        // time text y
        float time_text_y = side_circle_y + radius_side_circle + text_size;
        if (isDayTime) {
            canvas.drawText(DAYTIME, start_side_circle_x, time_text_y, text_paint);
            canvas.drawText(NIGHTTIME, end_side_circle_x, time_text_y, text_paint);
        } else {
            canvas.drawText(NIGHTTIME, start_side_circle_x, time_text_y, text_paint);
            canvas.drawText(DAYTIME, end_side_circle_x, time_text_y, text_paint);
        }


        // item info text
        float item_text_y = time_text_y + radius_item_info_circle * 2;
        // 总宽度
        float total_width = 0;
        // 查看有几个是在下面显示的
        int dl_bottom = 0;
        for (ItemInfoBean iib : dl) {
            if (iib.getItem_shape() == ItemInfoBean.TOP_RECT) continue;
            // item width
            float item_weight = getItem_width(iib);
            total_width += item_weight;
            dl_bottom++;
        }


        total_width += radius_item_info_circle * 2  // blank  between item and item
                * (dl_bottom - 1) // len - 1
        ;
        // first item x
        float both_side_width = (end_side_circle_x - start_side_circle_x - total_width) / 2;
        float first_item_x = start_side_circle_x + both_side_width;
        // draw
        float current_item_x = first_item_x;
        for (ItemInfoBean iib : dl) {
            if (iib.getItem_shape() == ItemInfoBean.TOP_RECT) continue;
            item_text_paint.setColor(ContextCompat.getColor(mContext, iib.getItem_color()));
            switch (iib.getItem_shape()) {
                case ItemInfoBean.CIRCLE:
                    canvas.drawCircle(current_item_x + radius_item_info_circle, item_text_y, radius_item_info_circle, item_text_paint);
                    item_text_paint.setColor(Color.BLACK);
                    canvas.drawText(iib.getItem_text(), current_item_x + radius_item_info_circle * 2 + 2, DrawUtil.draw_text_middle_y(item_text_paint, item_text_y), item_text_paint);
                    break;
                case ItemInfoBean.BOTTOM_RECT:
                    canvas.drawRoundRect(current_item_x, item_text_y - radius_item_info_circle / 2, current_item_x + radius_item_info_circle * 3, item_text_y + radius_item_info_circle / 2, 5, 5, item_text_paint);
                    item_text_paint.setColor(Color.BLACK);
                    canvas.drawText(iib.getItem_text(), current_item_x + radius_item_info_circle * 3 + 2, DrawUtil.draw_text_middle_y(item_text_paint, item_text_y), item_text_paint);
                    break;
                case ItemInfoBean.TRIANGLE:
                    float top_x = current_item_x + radius_item_info_circle;
                    float top_y = (float) (item_text_y - 0.75 * radius_item_info_circle);
                    drawTriangleBaseTop(canvas, top_x, top_y, item_text_paint);
                    item_text_paint.setColor(Color.BLACK);
                    canvas.drawText(iib.getItem_text(), current_item_x + radius_item_info_circle * 2 + 2, DrawUtil.draw_text_middle_y(item_text_paint, item_text_y), item_text_paint);
                    break;
            }
            current_item_x += getItem_width(iib) + radius_item_info_circle * 2;
        }


        // 横坐标绘制
        float baseline_y = 0;
        if (current_report_type == SLEEP_REPORT) {
            baseline_y = side_circle_y - radius_item_info_circle * 2;
        } else {
            baseline_y = side_circle_y - radius_item_info_circle * 4;
        }
        canvas.drawLine(0, baseline_y, getWidth(), baseline_y, baseline_paint);

        // 图表绘制
        float chart_min_unit = (end_side_circle_x - start_side_circle_x) / (12 * 60 * 60);
        for (ItemInfoBean iib : dl) {
            Log.e(TAG, "dl:" + iib.getItem_text());
            // 设置画笔颜色
            cylinder_paint.setColor(ContextCompat.getColor(mContext, iib.getItem_color()));
            Map<String, String> data_map = iib.getData_map();
            for (Map.Entry<String, String> m : data_map.entrySet()) {
                switch (iib.getItem_data_type()) {
                    case ItemInfoBean.DATA_TYPE_HIGH:
                        drawCylinder(m, (int) (baseline_y / 3 * 2), canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        break;
                    case ItemInfoBean.DATA_TYPE_MIDDLE:
                        drawCylinder(m, (int) (baseline_y / 9 * 4), canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        break;
                    case ItemInfoBean.DATA_TYPE_LOW:
                        drawCylinder(m, (int) (baseline_y / 9 * 2), canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        break;
                    case ItemInfoBean.DATA_TYPE_TRIANGLE:
                        drawTriangle(m, canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        break;
                    case ItemInfoBean.DATA_TYPE_BOTTOM:
                        drawCylinder(m, (int) (baseline_y / 10), canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        break;
                }
            }
        }


        // 夜晚和白天的按钮
        if (isDayTime) {
            night_bitmap_rect.set(getPaddingStart() + 50, getPaddingTop() + 50, getPaddingStart() + 120, getPaddingTop() + 120);
            canvas.drawBitmap(night_normal_bitmap, night_bitmap_show_rect, night_bitmap_rect, text_paint);
        } else {
            night_bitmap_rect.set(getPaddingStart() + 50, getPaddingTop() + 50, getPaddingStart() + 120, getPaddingTop() + 120);
            canvas.drawBitmap(night_selected_bitmap, night_bitmap_show_rect, night_bitmap_rect, text_paint);
        }

        if (isHas_day_time()) {
            if (isDayTime) {
                daytime_bitmap_rect.set(getWidth() - getPaddingEnd() - 120, getPaddingTop() + 50, getWidth() - getPaddingEnd() - 50, getPaddingTop() + 120);
                canvas.drawBitmap(daytime_selected_bitmap, daytime_bitmap_show_rect, daytime_bitmap_rect, text_paint);
            } else {
                daytime_bitmap_rect.set(getWidth() - getPaddingEnd() - 120, getPaddingTop() + 50, getWidth() - getPaddingEnd() - 50, getPaddingTop() + 120);
                canvas.drawBitmap(daytime_normal_bitmap, daytime_bitmap_show_rect, daytime_bitmap_rect, text_paint);
            }
        }


        // 如果是 止鼾模式 下需要绘制头
        if (current_report_type == SNORE_REPORT) {

            // 中间条码长度
            float top_width = getWidth() / 2;
            // y
            float top_y = getPaddingTop() + 90;
            // 最左边的x
            float top_start_x = top_width / 2;

            // 分成100 分  最小单位
            float unit = top_width / 100;

            // 弱鼾声占比 20%
            float weak_snore = unit * weak_percent;
            Log.e(TAG, "weak_percent: " + weak_percent);

            // 中鼾声占比 45%
            float med_snore = unit * med_percent;
            Log.e(TAG, "med_percent: " + med_percent);

            // 强鼾声占比 35%
            float strength_snore = unit * strength_percent;
            Log.e(TAG, "strength_percent: " + strength_percent);

            // 弱鼾声绘制 文字
            top_paint.setColor(Color.parseColor("#BFD6D0"));
            top_paint.getTextBounds("弱鼾声", 0, "弱鼾声".length(), bounds);
            canvas.drawText("弱鼾声", top_start_x + weak_snore / 2, DrawUtil.draw_text_middle_y(top_paint, top_y - bounds.height()), top_paint);
            // 弱鼾声绘制 条形
            cylinder_paint.setColor(Color.parseColor("#BFD6D0"));
            canvas.drawRect(top_start_x, top_y, top_start_x + weak_snore, top_y + 30, cylinder_paint);
            // 弱鼾声绘制 百分比
            top_paint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawText(weak_percent + "%", top_start_x + weak_snore / 2, DrawUtil.draw_text_middle_y(top_paint, top_y + 15), top_paint);

            // 中鼾声开始x
            float med_snore_x = top_start_x + weak_snore;
            // 中鼾声绘制 文字
            top_paint.setColor(Color.parseColor("#679186"));
            top_paint.getTextBounds("中鼾声", 0, "中鼾声".length(), bounds);
            canvas.drawText("中鼾声", med_snore_x + med_snore / 2, DrawUtil.draw_text_middle_y(top_paint, top_y - bounds.height()), top_paint);
            // 中鼾声绘制 条形
            cylinder_paint.setColor(Color.parseColor("#679186"));
            canvas.drawRect(med_snore_x, top_y, med_snore_x + med_snore, top_y + 30, cylinder_paint);
            // 弱鼾声绘制 百分比
            top_paint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawText(med_percent + "%", med_snore_x + med_snore / 2, DrawUtil.draw_text_middle_y(top_paint, top_y + 15), top_paint);

            // 强鼾声开始x
            float strength_snore_x = med_snore_x + med_snore;
            // 强鼾声绘制 文字
            top_paint.setColor(Color.parseColor("#F9B4AB"));
            top_paint.getTextBounds("强鼾声", 0, "强鼾声".length(), bounds);
            canvas.drawText("强鼾声", strength_snore_x + strength_snore / 2, DrawUtil.draw_text_middle_y(top_paint, top_y - bounds.height()), top_paint);
            // 强鼾声绘制 条形
            cylinder_paint.setColor(Color.parseColor("#F9B4AB"));
            canvas.drawRect(strength_snore_x, top_y, strength_snore_x + strength_snore, top_y + 30, cylinder_paint);
            // 弱鼾声绘制 百分比
            top_paint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawText(strength_percent + "%", strength_snore_x + strength_snore / 2, DrawUtil.draw_text_middle_y(top_paint, top_y + 15), top_paint);
        }


    }

    /**
     * 画baseline 下的三角
     *
     * @param m        数据 “00：00：00”：秒   在哪一秒
     * @param canvas   图纸
     * @param start_x  总坐标起始点 x
     * @param start_y  总坐标起始点 y
     * @param min_unit 最小单位   即  1s  代表的像素点
     */
    private void drawTriangle(Map.Entry<String, String> m, Canvas canvas, float start_x, float start_y, float min_unit) {
        // 横坐标原点 到 起始点 相差多少秒
        int axis_origin2time_start = 0;
        // 起始点横坐标
        float time_start_x;


        // 白天
        if (isDayTime && DateUtil.in8to20(m.getKey())) {
            Log.e(TAG, "drawTriangle白天:" + m.getKey());
            axis_origin2time_start = DateUtil.time_diff("08:00:00", m.getKey());
            time_start_x = start_x + axis_origin2time_start * min_unit;
            drawTriangleBaseTop(canvas, time_start_x, start_y, cylinder_paint);
        } else if (!isDayTime && !DateUtil.in8to20(m.getKey())) {
            Log.e(TAG, "drawTriangle黑夜:" + m.getKey());
            axis_origin2time_start = DateUtil.time_diff("20:00:00", m.getKey());
            time_start_x = start_x + axis_origin2time_start * min_unit;
            drawTriangleBaseTop(canvas, time_start_x, start_y, cylinder_paint);
        }
    }

    private void drawTriangleBaseTop(Canvas canvas, float top_x, float top_y, Paint paint) {
        Path path = new Path();
        path.moveTo(top_x, top_y);
        path.lineTo((float) (top_x - Math.sqrt(3) / 2 * radius_item_info_circle), (float) (top_y + 1.5 * radius_item_info_circle));
        path.lineTo((float) (top_x + Math.sqrt(3) / 2 * radius_item_info_circle), (float) (top_y + 1.5 * radius_item_info_circle));
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 画图 单柱
     *
     * @param m             数据 “00：00：00”：秒    在某一时间开始，持续了多少秒
     * @param cylinder_high 数据类型 决定数据柱状图的高低
     * @param canvas        图纸
     * @param start_x       总坐标起始点 x
     * @param start_y       总坐标起始点 y
     * @param min_unit      最小单位   即  1s  代表的像素点
     */
    private void drawCylinder(Map.Entry<String, String> m, int cylinder_high, Canvas canvas, float start_x, float start_y, float min_unit) {
        // 横坐标原点 到 起始点 相差多少秒
        int axis_origin2time_start = 0;
        // 起始点横坐标
        float time_start_x;
        // 起始点 到 结束点 相差多少秒
        int time_start2time_end = 0;
        // 结束点横坐标
        float time_end_x;

        /*
         *  1. 起始时间 在 20~24 点之间
         *      （1）结束时间 在  20~24 点之间
         *              正常画图
         *      （2）结束时间 在  0~8   点之间
         *              起始点 到 24 点
         *              24 点 到 结束点
         *      （3）结束时间 在  8~20  点之间
         *              如果在白天
         *                  8 点 到 结束点
         *              如果在夜晚
         *                  起始点 到 24 点
         *                  24 点 到 结束点
         *
         *  2. 起始时间 在 0~8   点之间
         *      （1）结束时间 在  0~8 点之间
         *              正常画图
         *      （2）结束时间 在  8~20  点之间
         *              如果在白天
         *                  8 点 到 结束点
         *              如果在夜晚
         *                  起始点 到 8 点
         *  3. 起始时间 在 8~20  点之间
         *       （1）结束时间 在  8~20  点之间
         *              正常画图
         * */


        if (DateUtil.in20to24(m.getKey())) {
            Log.e(TAG, m.getKey() + "在in20to24: " + true);
            if (DateUtil.in20to24(m.getValue())) {
                if (isDayTime) return;
                axis_origin2time_start = DateUtil.time_diff("20:00:00", m.getKey());
                time_start_x = start_x + axis_origin2time_start * min_unit;
                time_start2time_end = DateUtil.time_diff(m.getKey(), m.getValue());
                time_end_x = time_start_x + time_start2time_end * min_unit;
                canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);
            } else if (DateUtil.in0to8(m.getValue())) {
                if (isDayTime) return;
                // 画图 起始点 到 24点
                axis_origin2time_start = DateUtil.time_diff("20:00:00", m.getKey());
                time_start_x = start_x + axis_origin2time_start * min_unit;
                time_start2time_end = DateUtil.time_diff(m.getKey(), "24:00:00");
                time_end_x = time_start_x + time_start2time_end * min_unit;
                canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);

                // 画图 0点 到 结束点
                axis_origin2time_start = DateUtil.time_diff("20:00:00", "24:00:00");
                time_start_x = start_x + axis_origin2time_start * min_unit;
                time_start2time_end = DateUtil.time_diff("00:00:00", m.getValue());
                time_end_x = time_start_x + time_start2time_end * min_unit;
                canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);
            } else if (DateUtil.in8to20(m.getValue())) {
                if (isDayTime) {
                    // 如果是白天
                    time_start_x = start_x;
                    time_start2time_end = DateUtil.time_diff("08:00:00", m.getValue());
                    time_end_x = time_start_x + time_start2time_end * min_unit;
                    canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);
                } else {
                    // 画图 起始点 到 24点
                    axis_origin2time_start = DateUtil.time_diff("20:00:00", m.getKey());
                    time_start_x = start_x + axis_origin2time_start * min_unit;
                    time_start2time_end = DateUtil.time_diff(m.getKey(), "24:00:00");
                    time_end_x = time_start_x + time_start2time_end * min_unit;
                    canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);

                    // 画图 0点 到 结束点
                    axis_origin2time_start = DateUtil.time_diff("20:00:00", "24:00:00");
                    time_start_x = start_x + axis_origin2time_start * min_unit;
                    time_start2time_end = DateUtil.time_diff("00:00:00", "08:00:00");
                    time_end_x = time_start_x + time_start2time_end * min_unit;
                    canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);
                }
            }
        } else if (DateUtil.in0to8(m.getKey())) {
            Log.e(TAG, m.getKey() + "在in0to8: " + true);
            if (DateUtil.in0to8(m.getValue())) {
                if (isDayTime) return;
                Log.e(TAG, m.getValue() + "在in0to8: " + true);
                // 正常画图
                axis_origin2time_start = DateUtil.time_diff("20:00:00", m.getKey());
                time_start_x = start_x + axis_origin2time_start * min_unit;
                time_start2time_end = DateUtil.time_diff(m.getKey(), m.getValue());
                time_end_x = time_start_x + time_start2time_end * min_unit;
                canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);
            } else if (DateUtil.in8to20(m.getValue())) {
                Log.e(TAG, m.getValue() + "在in8to20: " + true);
                // 如果是在白天
                if (isDayTime) {
                    time_start_x = start_x;
                    time_start2time_end = DateUtil.time_diff("08:00:00", m.getValue());
                    time_end_x = time_start_x + time_start2time_end * min_unit;
                    canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);
                } else {
                    // 正常画图
                    axis_origin2time_start = DateUtil.time_diff("20:00:00", m.getKey());
                    time_start_x = start_x + axis_origin2time_start * min_unit;
                    time_start2time_end = DateUtil.time_diff(m.getKey(), "08:00:00");
                    time_end_x = time_start_x + time_start2time_end * min_unit;
                    canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);
                }
            }
        } else if (DateUtil.in8to20(m.getKey())) {
            Log.e(TAG, m.getKey() + "在in8to20: " + true);
            if (DateUtil.in8to20(m.getValue())) {
                if (!isDayTime) return;
                // 正常画图
                axis_origin2time_start = DateUtil.time_diff(isDayTime ? "08:00:00" : "20:00:00", m.getKey());
                time_start_x = start_x + axis_origin2time_start * min_unit;
                time_start2time_end = DateUtil.time_diff(m.getKey(), m.getValue());
                time_end_x = time_start_x + time_start2time_end * min_unit;
                canvas.drawRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, cylinder_paint);
            }
        }
    }


    /**
     * 获得 单项信息 的一个的宽度
     *
     * @param iib
     * @return
     */
    private float getItem_width(ItemInfoBean iib) {
        bounds = new Rect();
        item_text_paint.getTextBounds(iib.getItem_text(), 0, iib.getItem_text().length(), bounds);
        switch (iib.getItem_shape()) {
            case ItemInfoBean.CIRCLE:
            case ItemInfoBean.TRIANGLE:
                return bounds.width()
                        + radius_item_info_circle * 2
                        + 2;
            case ItemInfoBean.BOTTOM_RECT:
                return bounds.width()
                        + radius_item_info_circle * 3
                        + 2;
            default:
                return bounds.width()
                        + radius_item_info_circle
                        + 2;
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec, width);
        //设置宽高
        setMeasuredDimension(width, height);
        initParams();
    }

    private void initParams() {
//         height
//        int canvas_height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
//         width
//        int canvas_width = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();
    }

    public boolean isHas_day_time() {
        return has_day_time;
    }

    public void setHas_day_time(boolean has_day_time) {
        this.has_day_time = has_day_time;
    }


    //根据xml的设定获取宽度
    private int measureWidth(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);

        int specSize = MeasureSpec.getSize(measureSpec);

        //wrap_content
//
//        if (specMode == MeasureSpec.AT_MOST) {
//
//        }

        //fill_parent或者精确值

//        else if (specMode == MeasureSpec.EXACTLY) {
//
//        }

//        Log.i("这个控件的宽度----------", "specMode=" + specMode + " specSize=" + specSize);

        return specSize;

    }

    //根据xml的设定获取高度

    private int measureHeight(int measureSpec, int width) {

        int specMode = MeasureSpec.getMode(measureSpec);

        int specSize = MeasureSpec.getSize(measureSpec);

//        //wrap_content
//        if (specMode == MeasureSpec.AT_MOST) {
//        }
//        //fill_parent或者精确值
//        else if (specMode == MeasureSpec.EXACTLY) {
//        }
        specSize = (int) (width / ratio);
//        Log.i("这个控件的高度----------", "specMode:" + specMode + " specSize:" + specSize);
        return specSize;

    }


    // 白天黑夜点击接口
    public interface OnDayTimeClick {
        void onDaytimeClick();

        void onNightClick();
    }

    public OnDayTimeClick onDayTimeClick;

    public void setOnDayTimeClick(OnDayTimeClick onDayTimeClick) {
        this.onDayTimeClick = onDayTimeClick;
    }
}
