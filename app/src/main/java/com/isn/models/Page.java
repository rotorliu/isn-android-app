package com.isn.models;

import java.util.List;

/**
 * Created by 刘宝 on 2016/1/12.
 */
public class Page<T> {

    private List<T> content;
    private boolean last;
    private int totalPages;
    private int totalElements;
    private int size;
    private int number;
    private boolean first;
    private int numberOfElements;
}
