package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.File;
import java.util.List;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("files")
public class Files {
    @XStreamImplicit
    private List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
