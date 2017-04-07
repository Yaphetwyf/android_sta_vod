package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("tasks")
public class Tasks {
    @XStreamImplicit
    private List<Task> tasks;

    public Tasks() {

    }

    public Tasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
