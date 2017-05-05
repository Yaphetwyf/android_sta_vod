package com.example.administrator.android_sta_vod.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("term")
public class Term implements Parcelable {
    @XStreamAsAttribute()
    @XStreamAlias("name")
    private String name;
    @XStreamAsAttribute()
    @XStreamAlias("id")
    private String id;
    @XStreamAsAttribute()
    @XStreamAlias("pid")
    private String pid;
    @XStreamAsAttribute()
    @XStreamAlias("status")
    private String status;

    public Term(){

    }

    public Term(String name,String id,String pid,String status){
        this.name = name;
        this.id = id;
        this.pid = pid;
        this.status = status;
    }
    protected Term(Parcel in)
    {
        name = in.readString();
        id = in.readString();
        pid = in.readString();
        status=in.readString();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Term{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    public static final Creator<Term> CREATOR = new Creator<Term>() {
        @Override
        public Term createFromParcel(Parcel in)
        {
            return new Term(in);
        }

        @Override
        public Term[] newArray(int size)
        {
            return new Term[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(pid);
        dest.writeString(status);
    }
}
