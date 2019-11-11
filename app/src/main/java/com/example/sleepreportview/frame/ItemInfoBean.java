package com.example.sleepreportview.frame;

import java.util.Map;

public class ItemInfoBean {
    // 单项信息内容
    private String item_text;
    // 单项信息形状   圆 三角 底部方形 顶部方形
    private int item_shape;
    // 单项信息颜色
    private int item_color;
    // 信息所包含时间
    private Map<String, String> data_map;
    // 信息类型   高中低 底部 三角
    private int item_data_type;

    public static final int CIRCLE = 0;
    public static final int BOTTOM_RECT = 1;
    public static final int TRIANGLE = 2;
    public static final int TOP_RECT = 8;
    public static final int DATA_TYPE_HIGH = 3;
    public static final int DATA_TYPE_MIDDLE = 4;
    public static final int DATA_TYPE_LOW = 5;
    public static final int DATA_TYPE_TRIANGLE = 6;
    public static final int DATA_TYPE_BOTTOM = 7;

    public int getItem_data_type() {
        return item_data_type;
    }

    public void setItem_data_type(int item_data_type) {
        this.item_data_type = item_data_type;
    }

    public ItemInfoBean(String item_text, int item_shape, int item_color, Map<String, String> data_map, int item_data_type) {
        this.item_text = item_text;
        this.item_shape = item_shape;
        this.item_color = item_color;
        this.data_map = data_map;
        this.item_data_type = item_data_type;
    }

    public Map<String, String> getData_map() {
        return data_map;
    }

    public void setData_map(Map<String, String> data_map) {
        this.data_map = data_map;
    }

    public int getItem_color() {
        return item_color;
    }

    public void setItem_color(int item_color) {
        this.item_color = item_color;
    }

    public String getItem_text() {
        return item_text;
    }

    public void setItem_text(String item_text) {
        this.item_text = item_text;
    }

    public int getItem_shape() {
        return item_shape;
    }

    public void setItem_shape(int item_shape) {
        this.item_shape = item_shape;
    }
}
