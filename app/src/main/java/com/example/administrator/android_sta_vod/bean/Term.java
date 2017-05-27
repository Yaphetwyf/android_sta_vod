package com.example.administrator.android_sta_vod.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/5/11.
 */
@Entity
@XStreamAlias("term")
public class Term implements Parcelable{
    @XStreamAsAttribute()
    @XStreamAlias("id")
    private String id;
    @XStreamAsAttribute()
    @XStreamAlias("name")
    private String name;
    @XStreamAsAttribute()
    @XStreamAlias("pid")
    private String pid;
    @XStreamAsAttribute()
    @XStreamAlias("status")
    private String status;
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPid() {
        return this.pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }

    private boolean isSpeaking;
    @Generated(hash = 1021206145)
    public Term(String id, String name, String pid, String status) {
        this.id = id;
        this.name = name;
        this.pid = pid;
        this.status = status;
    }
    protected Term(Parcel in)
    {
        id = in.readString();
        name = in.readString();

        pid = in.readString();
        status=in.readString();
    }
    @Generated(hash = 142182234)
    public Term() {
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
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(pid);
        dest.writeString(status);
    }
}
