package com.example.administrator.android_sta_vod.utils;

import android.text.TextUtils;
import android.util.Log;

import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.bean.FileBean;
import com.example.administrator.android_sta_vod.bean.MusicBean;
import com.example.administrator.android_sta_vod.bean.Path;
import com.example.administrator.android_sta_vod.bean.Prog;
import com.example.administrator.android_sta_vod.bean.Progs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.administrator.android_sta_vod.base.Const.pass_word;
import static com.example.administrator.android_sta_vod.base.Const.servers_address;
import static com.example.administrator.android_sta_vod.base.Const.user_name;

/**
 * Created by AVSZ on 2017/3/10.
 */

public class Beacon_util {
    private static String tag = "BEACON_UTIL";

    public static void login()
    {
        String username = SPUtils.getString(Ui_utils.get_context(), user_name);
        String password = SPUtils.getString(Ui_utils.get_context(), pass_word);
        String server_address = SPUtils.getString(Ui_utils.get_context(), servers_address);
        boolean net_connect = NetUtils.isConnected(Ui_utils.get_context());
        L.d(tag, "login");
        if (!(TextUtils.isEmpty(username) && TextUtils.isEmpty(password) && TextUtils.isEmpty(server_address)))
        {
            if (net_connect)
            {
                ndk_wrapper.instance().avsz_init(server_address, (short) 1220, username, password);
            } else
            {
                T.show_short(Ui_utils.get_string(R.string.net_connect_failure));
            }
        }
    }

    public static ArrayList<FileBean> progs_to_file(Progs progs)
    {
        return get_paths(progs);
    }

    //通过progs获取listview的data
    private static ArrayList<FileBean> get_paths(Progs progs)
    {
        List<Path> paths = new ArrayList<>();
        String path = "";
        String p1 = "";
        String p2 = "";
        String p3 = "";
        String p4="";
        if (null == progs || null == progs.getProgs())
        {
            return null;
        }
        L.d("size()=" + progs.getProgs().size());
        for (Prog prog : progs.getProgs())
        {
            if (TextUtils.isEmpty(prog.getName()))
            {
                path = prog.getPath();
                p1 = path.replace("/", "/");
                p2 = p1.replace("\\", "/");
                p3 = p2.substring(p2.indexOf("/") + 1);
            } else
            {
                path = prog.getPath();
                p1 = path.replace("/", "/");
                p2 = p1.replace("\\", "/");
                int i = p2.indexOf("/");
                L.d("i=" + i);
                p3 = p2.substring(p2.indexOf("/") + 1);
                p4=p2.substring(0,p2.indexOf("/")+1);
                SPUtils.putString(Ui_utils.get_context(), Const.download,p4);
                p3 = p3 + "/" + prog.getName();
            }
            paths.add(new Path(p3,prog.getLength(),path));
        }
        Collections.reverse(paths);
        Log.d(tag,"paths == " + paths.toString());
        return init_data(paths);
    }

    public static ArrayList<FileBean> init_data(List<Path> paths)
    {
        ArrayList<FileBean> datas;

        if (null == paths || 0 == paths.size())
        {
            datas = new ArrayList<>();
            return datas;
        }

        String root_path = paths.get(paths.size()-1).getPath();
        //根路径
        ArrayList<FileBean> root = new ArrayList<>();

        ArrayList<FileBean> files = new ArrayList<>();

        for (Path path : paths)
        {
            FileBean fileBean;
            String name = path.getPath().substring(path.getPath().lastIndexOf("/") + 1);
            if (name.contains(".mp3") || name.contains("avi"))
            {
                fileBean = new MusicBean();
                ((MusicBean)fileBean).setLength(path.getLength());
            } else
            {
                fileBean = new FileBean();
            }

            fileBean.setId(path.getPath());
            fileBean.setPath(path.getId());
            fileBean.setName(name);
            String[] split = path.getPath().split("/");
            if (path.getPath().equals(root_path))
            {
                fileBean.setPid(null);
            } else
            {
                int index = path.getPath().lastIndexOf("/");
                String pid = path.getPath().substring(0, index);
                fileBean.setPid(pid);
                L.d(fileBean.toString());
            }

            files.add(fileBean);
        }

        //设置children father属性
        for (FileBean file : files)
        {
            ArrayList<FileBean> children = new ArrayList<>();
            for (FileBean f : files)
            {
                //如果id == pid 就互设为父子
                if (file.getId().equals(f.getPid()))
                {
                    children.add(f);
                    f.setFather(file);
                }
            }
            if (0 != children.size())
            {
                file.setChildren(children);
            } else
            {
                Log.d(tag, "file_name" + file.getName());
            }
            if (0 == children.size() && !file.getName().contains("mp3"))
            {
                file.setChildren(new ArrayList<FileBean>());
            }
            if (root_path.equals(file.getPid()))
            {
                root.add(file);
            }

        }
        //最开始数据源为根路径
        datas = root;
        Log.d(tag,"root == " + root.toString());

        return root;
    }
}
