package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("root")
public class Root {
    @XStreamAsAttribute()
    @XStreamAlias("ord")
    private String ord;
    @XStreamAsAttribute()
    @XStreamAlias("ret")
    private String ret;
    @XStreamAsAttribute()
    @XStreamAlias("sess")
    private String sess;

    @XStreamAlias("areas")
    private Areas areas;
    @XStreamAlias("terms")
    private Terms terms;
    @XStreamAlias("progs")
    private Progs progs;
    @XStreamAlias("tasks")
    private Tasks tasks;
    @XStreamAlias("usrs")
    private Users users;

    public Areas getAreas() {
        return areas;
    }

    public void setAreas(Areas areas) {
        this.areas = areas;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }

    public Progs getProgs() {
        return progs;
    }

    public void setProgs(Progs progs) {
        this.progs = progs;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getOrd() {
        return ord;
    }

    public void setOrd(String ord) {
        this.ord = ord;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getSess() {
        return sess;
    }

    public void setSess(String sess) {
        this.sess = sess;
    }

    @Override
    public String toString() {
        return "Root{" +
                "ord='" + ord + '\'' +
                ", ret='" + ret + '\'' +
                ", sess='" + sess + '\'' +
                ", areas=" + areas +
                ", terms=" + terms +
                ", progs=" + progs +
                ", tasks=" + tasks +
                ", users=" + users +
                '}';
    }
}
