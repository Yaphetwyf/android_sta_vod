package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("Areas")
public class Areas {
    @XStreamImplicit
    private List<Area> areas;

    public Areas(){

    }

    public Areas(List<Area> areas){
        this.areas = areas;
    }
    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }
}
