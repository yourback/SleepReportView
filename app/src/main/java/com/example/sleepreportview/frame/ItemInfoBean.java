package com.example.sleepreportview.frame;

import java.util.Map;

public class ItemInfoBean {
    private String item_text;
    private int item_shape;
    private int item_color;
    private Map<String, Integer> data_map;
    private int item_data_type;

    public static final int CIRCLE = 0;
    public static final int RECT = 1;
    public static final int TRIANGLE = 2;
    public static final int DATA_TYPE_HIGH = 3;
    public static final int DATA_TYPE_MIDDLE = 4;
    public static final int DATA_TYPE_LOW = 5;

    public int getItem_data_type() {
        return item_data_type;
    }

    public void setItem_data_type(int item_data_type) {
        this.item_data_type = item_data_type;
    }

    public ItemInfoBean(String item_text, int item_shape, int item_color, Map<String, Integer> data_map, int item_data_type) {
        this.item_text = item_text;
        this.item_shape = item_shape;
        this.item_color = item_color;
        this.data_map = data_map;
        this.item_data_type = item_data_type;
    }

    public Map<String, Integer> getData_map() {
        return data_map;
    }

    public void setData_map(Map<String, Integer> data_map) {
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
