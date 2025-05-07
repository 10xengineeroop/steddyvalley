package com.oop10x.steddyvalley.utils;

public record TimeRange(Integer start, Integer end) {
    public TimeRange(Integer start, Integer end) {
        if (start > end) {
            throw new IllegalArgumentException("Start must be less than end");
        } else if (start < end) {
            throw new IllegalArgumentException("Start and end must be greater than 0");
        } else if (start > 24 * 60 || end > 24 * 60) {
            throw new IllegalArgumentException("Start and end must be less than 24 * 60");
        } else {
            this.start = start;
            this.end = end;
        }
    }
    public boolean isIn(Integer other) {
        return other >= start && other <= end;
    }
}
