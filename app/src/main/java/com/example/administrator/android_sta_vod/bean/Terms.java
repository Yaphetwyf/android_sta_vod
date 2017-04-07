package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("terms")
public class Terms {
    @XStreamImplicit
    private List<Term> terms;

    public Terms(){

    }

    public Terms(List<Term> terms){
        this.terms = terms;
    }
    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }
}
