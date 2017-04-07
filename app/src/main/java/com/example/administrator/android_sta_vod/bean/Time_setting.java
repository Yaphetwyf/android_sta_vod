package com.example.administrator.android_sta_vod.bean;

/**
 * Created by AVSZ on 2017/1/5.
 */

public class Time_setting {
    private String task_type;
    private String start_date;
    private String excute_time;
    private String continu_time;
    private String end_date;
    private String weeks;
    private String endselect;

    public String getEndselect() {
        return endselect;
    }

    public void setEndselect(String endselect) {
        this.endselect = endselect;
    }

    @Override
    public String toString() {
        return "Time_setting{" +
                "task_type='" + task_type + '\'' +
                ", start_date='" + start_date + '\'' +
                ", excute_time='" + excute_time + '\'' +
                ", continu_time='" + continu_time + '\'' +
                ", end_date='" + end_date + '\'' +
                ", weeks=" +weeks +
                ", endselect='" + endselect + '\'' +
                '}';
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getExcute_time() {
        return excute_time;
    }

    public void setExcute_time(String excute_time) {
        this.excute_time = excute_time;
    }

    public String getContinu_time() {
        return continu_time;
    }

    public void setContinu_time(String continu_time) {
        this.continu_time = continu_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

}
