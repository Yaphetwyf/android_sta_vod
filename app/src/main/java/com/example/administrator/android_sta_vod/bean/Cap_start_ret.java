package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by AVSZ on 2017/4/26.
 */
@XStreamAlias("root")
public class Cap_start_ret {
    @XStreamAsAttribute()
    @XStreamAlias("ord")
    private String ord;
    @XStreamAsAttribute()
    @XStreamAlias("ret")
    private String ret;
    @XStreamAsAttribute()
    @XStreamAlias("sess")
    private String sess;

    @XStreamAlias("terms")
    private Terms terms;

    public String getOrd()
    {
        return ord;
    }

    public void setOrd(String ord)
    {
        this.ord = ord;
    }

    public String getRet()
    {
        return ret;
    }

    public void setRet(String ret)
    {
        this.ret = ret;
    }

    public String getSess()
    {
        return sess;
    }

    public void setSess(String sess)
    {
        this.sess = sess;
    }

    public Terms getTerms()
    {
        return terms;
    }

    public void setTerms(Terms terms)
    {
        this.terms = terms;
    }
}
