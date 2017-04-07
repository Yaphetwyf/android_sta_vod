package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("progs")
public class Progs {

    public List<Prog> getProgs() {
        return progs;
    }

    public void setProgs(List<Prog> progs) {
        this.progs = progs;
    }
    @XStreamImplicit
    private List<Prog> progs;
}
