package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("task")
public class Task {
    @XStreamAsAttribute()
    @XStreamAlias("id")
    private String id;//id
    @XStreamAsAttribute()
    @XStreamAlias("name")
    private String name;
    @XStreamAlias("rand")
    private String rand;//是否随机播放
    @XStreamAsAttribute()
    @XStreamAlias("force")
    private String force;//是否强切信号
    @XStreamAsAttribute()
    @XStreamAlias("power")
    private String power;//是否控制功放电源
    @XStreamAsAttribute()
    @XStreamAlias("type")
    private String type;//任务类型
    @XStreamAsAttribute()
    @XStreamAlias("sdate")
    private String sdate;//开始时间
    @XStreamAsAttribute()
    @XStreamAlias("etm")
    private String etm;//执行时间
    @XStreamAsAttribute()
    @XStreamAlias("end")
    private String end;//是否可编辑截止日期
    @XStreamAsAttribute()
    @XStreamAlias("edate")
    private String edate;//截止日期
    @XStreamAsAttribute()
    @XStreamAlias("dura")
    private String dura;//持续时间
    @XStreamAsAttribute()
    @XStreamAlias("week")
    private String week;//星期选择
    @XStreamAsAttribute()
    @XStreamAlias("tt-long")
    private String tt_long;//文件总长
    @XStreamAsAttribute()
    @XStreamAlias("status")
    private String status;//执行类型
    @XStreamAlias("terms")
    private Terms terms;//执行任务终端
    @XStreamAlias("files")
    private Files files;//文件库

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }

    public String getTt_long() {
        return tt_long;
    }

    public void setTt_long(String tt_long) {
        this.tt_long = tt_long;
    }

    public Files getFiles() {
        return files;
    }

    public void setFiles(Files files) {
        this.files = files;
    }

    public Terms getTerm() {
        return terms;
    }

    public void setTerm(Terms terms) {
        this.terms = terms;
    }

    public Files getFile() {
        return files;
    }

    public void setFile(Files files) {
        this.files = files;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRand() {
        return rand;
    }

    public void setRand(String rand) {
        this.rand = rand;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtm() {
        return etm;
    }

    public void setEtm(String etm) {
        this.etm = etm;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDura() {
        return dura;
    }

    public void setDura(String dura) {
        this.dura = dura;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", rand='" + rand + '\'' +
                ", force='" + force + '\'' +
                ", power='" + power + '\'' +
                ", type='" + type + '\'' +
                ", sdate='" + sdate + '\'' +
                ", etm='" + etm + '\'' +
                ", end='" + end + '\'' +
                ", edate='" + edate + '\'' +
                ", dura='" + dura + '\'' +
                ", week='" + week + '\'' +
                ", term=" + terms +
                ", file=" + files +
                '}';
    }
}
