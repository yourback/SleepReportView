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
import android.util.EventLog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
    private boolean has_day_time;
    // current show  daytime / nighttime
    private boolean isDayTime = false;
    private static final String DAYTIME = "08:00";
    private static final String NIGHTTIME = "20:00";


    // current report type
    private int current_report_type = 0;
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
    private List<ItemInfoBean> dl;

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

    public SleepTimingChart(Context context) {
        super(context);
        mContext = context;
    }

    public SleepTimingChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SleepTimingChart);
        ratio = ta.getFloat(R.styleable.SleepTimingChart_ratio, 1.0f);
        current_report_type = ta.getInteger(R.styleable.SleepTimingChart_report_type, SLEEP_REPORT);
        text_size = ta.getDimension(R.styleable.SleepTimingChart_text_size, 1);
        radius_side_circle = ta.getDimension(R.styleable.SleepTimingChart_radius_side_circle, 1);
        radius_middle_circle = ta.getDimension(R.styleable.SleepTimingChart_radius_middle_circle, 1);
        radius_item_info_circle = ta.getDimension(R.styleable.SleepTimingChart_radius_item_info_circle, 1);
        ta.recycle();
        initPaint();
        initData();
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
//                Toast.makeText(mContext, "点击夜晚", Toast.LENGTH_SHORT).show();
                if (onDayTimeClick != null) {
                    onDayTimeClick.onNightClick();
                }
            } else if (x < daytime_bitmap_rect.right && daytime_bitmap_rect.left < x && y > daytime_bitmap_rect.top && y < daytime_bitmap_rect.bottom) {
//                Toast.makeText(mContext, "点击白天", Toast.LENGTH_SHORT).show();
                if (onDayTimeClick != null) {
                    onDayTimeClick.onDaytimeClick();
                }
            }
        }

        return true;
    }

    private void initData() {
        dl = new ArrayList<>();
        Map<String, Integer> m = new HashMap<>();
        m.put("22:10:20", 30);
        m.put("00:10:20", 30);
        m.put("02:10:20", 30);
        m.put("06:10:20", 30);
        m.put("07:10:20", 30);


        Map<String, Integer> m1 = new HashMap<>();
        m1.put("22:40:20", 200);
        m1.put("00:40:20", 30);
        m1.put("02:40:20", 30);
        m1.put("06:40:20", 30);
        m1.put("07:40:20", 30);


        Map<String, Integer> m2 = new HashMap<>();
        m2.put("22:00:20", 30);
        m2.put("00:00:20", 30);
        m2.put("02:00:20", 30);
        m2.put("06:00:20", 30);
        m2.put("07:00:20", 3000);


        dl.add(new ItemInfoBean("弱鼾声", ItemInfoBean.CIRCLE, R.color.colorPrimaryDark, m, ItemInfoBean.DATA_TYPE_LOW));
        dl.add(new ItemInfoBean("中鼾声", ItemInfoBean.CIRCLE, R.color.colorPrimary, m1, ItemInfoBean.DATA_TYPE_MIDDLE));
        dl.add(new ItemInfoBean("强鼾声", ItemInfoBean.CIRCLE, R.color.colorAccent, m2, ItemInfoBean.DATA_TYPE_HIGH));
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
        // side circle
        float start_side_circle_x = getPaddingStart() + radius_side_circle + text_size * 2;
        float end_side_circle_x = getMeasuredWidth() - getPaddingStart() - radius_side_circle - text_size * 2;
        canvas.drawCircle(start_side_circle_x, side_circle_y, radius_side_circle, side_circle_paint);
        canvas.drawCircle(end_side_circle_x, side_circle_y, radius_side_circle, side_circle_paint);


        // middle circle
        float middle_circle_width = end_side_circle_x - start_side_circle_x;
        float middle_circle_width_unit = middle_circle_width / 12;
        for (int i = 1; i < 12; i++) {
            float middle_circle_x = start_side_circle_x + i * middle_circle_width_unit;
            canvas.drawCircle(middle_circle_x, side_circle_y, radius_middle_circle, middle_circle_paint);
        }

        // time text
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
        // total width
        float total_width = 0;
        for (ItemInfoBean iib : dl) {
            // item width
            float item_weight = getItem_width(iib);
            Log.e(iib.getItem_text() + "长度:", "onDraw: " + item_weight);
            total_width += item_weight;
        }
        total_width += radius_item_info_circle * 2  // blank  between item and item
                * (dl.size() - 1) // len - 1
        ;
        // first item x
        float both_side_width = (end_side_circle_x - start_side_circle_x - total_width) / 2;
        float first_item_x = start_side_circle_x + both_side_width;
        // draw
        float current_item_x = first_item_x;
        for (ItemInfoBean iib : dl) {
            item_text_paint.setColor(ContextCompat.getColor(mContext, iib.getItem_color()));
            switch (iib.getItem_shape()) {
                case ItemInfoBean.CIRCLE:
                    canvas.drawCircle(current_item_x + radius_item_info_circle, item_text_y, radius_item_info_circle, item_text_paint);
                    item_text_paint.setColor(Color.BLACK);
                    canvas.drawText(iib.getItem_text(), current_item_x + radius_item_info_circle * 2 + 2, DrawUtil.draw_text_middle_y(item_text_paint, item_text_y), item_text_paint);
                    break;
                case ItemInfoBean.RECT:
                    canvas.drawRoundRect(current_item_x, item_text_y - radius_item_info_circle / 2, first_item_x + radius_item_info_circle * 3, item_text_y + radius_item_info_circle / 2, 5, 5, item_text_paint);
                    item_text_paint.setColor(Color.BLACK);
                    canvas.drawText(iib.getItem_text(), current_item_x + radius_item_info_circle * 3 + 2, DrawUtil.draw_text_middle_y(item_text_paint, item_text_y), item_text_paint);
                    break;
                case ItemInfoBean.TRIANGLE:
                    Path path = new Path();
                    path.moveTo(current_item_x + radius_item_info_circle, (float) (item_text_y - 0.75 * radius_item_info_circle));
                    path.lineTo((float) (current_item_x + (1 - Math.sqrt(3) / 2) * radius_item_info_circle), (float) (item_text_y + 0.75 * radius_item_info_circle));
                    path.lineTo((float) (current_item_x + (1 + Math.sqrt(3) / 2) * radius_item_info_circle), (float) (item_text_y + 0.75 * radius_item_info_circle));
                    path.close();
                    canvas.drawPath(path, item_text_paint);
                    item_text_paint.setColor(Color.BLACK);
                    canvas.drawText(iib.getItem_text(), current_item_x + radius_item_info_circle * 2 + 2, DrawUtil.draw_text_middle_y(item_text_paint, item_text_y), item_text_paint);
                    break;
            }
            current_item_x += getItem_width(iib) + radius_item_info_circle * 2;
        }


        // baseline
        float baseline_y = 0;
        if (current_report_type == SLEEP_REPORT) {
            baseline_y = side_circle_y - radius_item_info_circle * 2;
        } else {
            baseline_y = side_circle_y - radius_item_info_circle * 4;
        }
        canvas.drawLine(0, baseline_y, getWidth(), baseline_y, baseline_paint);

        // data chart
        float chart_min_unit = (end_side_circle_x - start_side_circle_x) / (12 * 60 * 60);


        // traversing data list
        for (ItemInfoBean iib : dl) {
            Log.e(TAG, "dl:" + iib.getItem_text());
            // 设置画笔颜色
            cylinder_paint.setColor(ContextCompat.getColor(mContext, iib.getItem_color()));
            Map<String, Integer> data_map = iib.getData_map();
            for (Map.Entry<String, Integer> m : data_map.entrySet()) {
                switch (iib.getItem_data_type()) {
                    case ItemInfoBean.DATA_TYPE_HIGH:
                        drawCylinder(m, (int) (baseline_y / 3 * 2), canvas, start_side_circle_x, baseline_y, chart_min_unit);
//                        drawCylinder(m, 360, canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        break;
                    case ItemInfoBean.DATA_TYPE_MIDDLE:
//                        drawCylinder(m, 240, canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        drawCylinder(m, (int) (baseline_y / 9 * 4), canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        break;
                    case ItemInfoBean.DATA_TYPE_LOW:
                        drawCylinder(m, (int) (baseline_y / 9 * 2), canvas, start_side_circle_x, baseline_y, chart_min_unit);
//                        drawCylinder(m, 120, canvas, start_side_circle_x, baseline_y, chart_min_unit);
                        break;
                }

            }
        }


        // 夜晚和白天的按钮
        // 绘制夜晚
        // x
//        float daytime_x = getPaddingStart();
//        float daytime_y = getPaddingTop();
        night_bitmap_rect.set(getPaddingStart() + 50, getPaddingTop() + 50, getPaddingStart() + 150, getPaddingTop() + 150);
        canvas.drawBitmap(night_selected_bitmap, night_bitmap_show_rect, night_bitmap_rect, text_paint);
        daytime_bitmap_rect.set(getWidth() - getPaddingEnd() - 150, getPaddingTop() + 50, getWidth() - getPaddingEnd() - 50, getPaddingTop() + 150);
        canvas.drawBitmap(daytime_normal_bitmap, daytime_bitmap_show_rect, daytime_bitmap_rect, text_paint);


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
    private void drawCylinder(Map.Entry<String, Integer> m, int cylinder_high, Canvas canvas, float start_x, float start_y, float min_unit) {
        // 计算 和 其实坐标 差多少像素点
        Log.e("drawCylinder", "drawCylinder: ");
        int i = DateUtil.time_diff(isDayTime ? "08:00:00" : "22:00:00", m.getKey());
        Log.e("drawCylinder", "drawCylinder: " + i);
        float time_start_x = start_x + i * min_unit;
        float time_end_x = time_start_x + m.getValue() * min_unit;
        Log.e("drawRoundRect", "draw");
        canvas.drawRoundRect(time_start_x, start_y - cylinder_high, time_end_x, start_y, 10, 10, cylinder_paint);
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
            case ItemInfoBean.RECT:
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
        // height
        int canvas_height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        // width
        int canvas_width = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();
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
