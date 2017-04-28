package com.example.administrator.android_sta_vod.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class FileBean implements Parcelable{
    private String id;
    private String pid;
    private ArrayList<FileBean> children;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;
    protected FileBean(Parcel in)
    {
        id = in.readString();
        pid = in.readString();
        children = in.createTypedArrayList(FileBean.CREATOR);
        select = in.readByte() != 0;
        father = in.readParcelable(FileBean.class.getClassLoader());
        name = in.readString();
    }

    public static final Creator<FileBean> CREATOR = new Creator<FileBean>() {
        @Override
        public FileBean createFromParcel(Parcel in)
        {
            return new FileBean(in);
        }

        @Override
        public FileBean[] newArray(int size)
        {
            return new FileBean[size];
        }
    };

    public boolean isSelect()
    {
        return select;
    }

    public void setSelect(boolean select)
    {
        this.select = select;
    }

    private boolean select;
    public FileBean getFather() {
        return father;
    }

    public void setFather(FileBean father) {
        this.father = father;
    }

    private FileBean father;

    public FileBean(){

    }

    public FileBean(String id, String pid, String name){
        this.id = id;
        this.pid = pid;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", children=" + children +
                ", name='" + name + '\'' +
                '}';
    }

    //添加子目录
    public void add_child(FileBean fileBean){
        children.add(fileBean);
    }

    //删除子目录
    public void delete_child(FileBean fileBean){
        children.remove(fileBean);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;

    public ArrayList<FileBean> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<FileBean> children) {
        this.children = children;
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(pid);
        dest.writeTypedList(children);
        dest.writeByte((byte) (select ? 1 : 0));
        dest.writeParcelable(father, flags);
        dest.writeString(name);
    }
}
