package com.example.administrator.android_sta_vod.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.FileBean;
import com.example.administrator.android_sta_vod.bean.MusicBean;

import java.util.ArrayList;
import java.util.List;



public class MusicAdapter extends RecyclerView.Adapter {
    private static final String TAG = "MUSICADAPTER";
    private static final int ITEM_TYPE_MUSIC = 1;
    private static final int ITEM_TYPE_FOLDER = 2;
    private List<FileBean> mDatas;
    private LayoutInflater inflater;

    public MusicAdapter(Context context, List<FileBean> data)
    {
        inflater = LayoutInflater.from(context);
        mDatas = data;
    }

    public interface OnMusicClickLitener {
        void onMusicClick(View view, int position);
    }

    private OnMusicClickLitener mOnMusicClickLitener;

    public void setOnMusicClickLitener(OnMusicClickLitener mOnMusicClickLitener)
    {
        this.mOnMusicClickLitener = mOnMusicClickLitener;
    }


    public interface OnFolderClickLitener {
        void onFolderClick(View view, int position);

        void onFolderLongClick(View view, int position);
    }

    private OnFolderClickLitener mOnFolderClickLitener;

    public void setOnFolderClickLitener(OnFolderClickLitener mOnFolderClickLitener)
    {
        this.mOnFolderClickLitener = mOnFolderClickLitener;
    }

    public void set_data(ArrayList<FileBean> data){
        mDatas = data;
        notifyDataSetChanged();
    }
    public void add_data(int positon, FileBean data)
    {
        mDatas.add(positon, data);
        notifyItemInserted(positon);
    }

    public void remove_data(int positon)
    {
        mDatas.remove(positon);
        notifyItemRemoved(positon);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ITEM_TYPE_MUSIC:
                return new Music_holder(inflater.inflate(R.layout.item_music, parent, false));
            case ITEM_TYPE_FOLDER:
                return new Folder_holder(inflater.inflate(R.layout.item_folder, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
    {
        Log.d(TAG, "postion == " + position);
        if (holder instanceof Music_holder)
        {
            ((Music_holder) holder).tv_music.setText(mDatas.get(position).getName());
            if(((MusicBean)mDatas.get(position)).get_select()){
                ((Music_holder) holder).iv_addmusic.setImageResource(R.drawable.addmusic_select);
            }else {
                ((Music_holder) holder).iv_addmusic.setImageResource(R.drawable.addmusic_normal);
            }
            if (null != mOnMusicClickLitener)
            {
                ((Music_holder) holder).ll_musicitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        MusicBean musicBean = (MusicBean) mDatas.get(pos);
                        if (musicBean.get_select())
                        {
                            musicBean.set_select(false);
                            ((Music_holder) holder).iv_addmusic.setImageResource(R.drawable.addmusic_normal);
                        } else
                        {
                            musicBean.set_select(true);
                            ((Music_holder) holder).iv_addmusic.setImageResource(R.drawable.addmusic_select);
                        }
                        mOnMusicClickLitener.onMusicClick(holder.itemView, pos);
                    }
                });

            }
        }

        if (holder instanceof Folder_holder)
        {
            ((Folder_holder) holder).tv_folder.setText(mDatas.get(position).getName());
            if(mDatas.get(position).isSelect()){
                ((Folder_holder) holder).iv_addmusic.setVisibility(View.VISIBLE);
            }else {
                ((Folder_holder) holder).iv_addmusic.setVisibility(View.INVISIBLE);
            }
            if (null != mOnFolderClickLitener)
            {
                ((Folder_holder) holder).ll_folderitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnFolderClickLitener.onFolderClick(holder.itemView, pos);
                    }
                });

                ((Folder_holder) holder).ll_folderitem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        int pos = holder.getLayoutPosition();

                        if (mDatas.get(pos).isSelect())
                        {
                            mDatas.get(pos).setSelect(false);
                            ((Folder_holder) holder).iv_addmusic.setVisibility(View.INVISIBLE);
                        } else
                        {
                            mDatas.get(pos).setSelect(true);
                            ((Folder_holder) holder).iv_addmusic.setVisibility(View.VISIBLE);
                        }
                        mOnFolderClickLitener.onFolderLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mDatas.get(position) instanceof MusicBean)
        {
            return ITEM_TYPE_MUSIC;
        } else
        {
            return ITEM_TYPE_FOLDER;
        }
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }


    class Folder_holder extends RecyclerView.ViewHolder {
        ImageView iv_addmusic;
        TextView tv_folder;
        ImageView iv_folder;
        LinearLayout ll_folderitem;

        public Folder_holder(View itemView)
        {
            super(itemView);
            iv_addmusic = (ImageView) itemView.findViewById(R.id.iv_addmusic);
            tv_folder = (TextView) itemView.findViewById(R.id.tv_folder);
            iv_folder = (ImageView) itemView.findViewById(R.id.iv_folder);
            ll_folderitem = (LinearLayout) itemView.findViewById(R.id.ll_folderitem);
        }
    }

    class Music_holder extends RecyclerView.ViewHolder {
        ImageView iv_addmusic;
        TextView tv_music;
        ImageView iv_music;
        LinearLayout ll_musicitem;

        public Music_holder(View itemView)
        {
            super(itemView);
            iv_addmusic = (ImageView) itemView.findViewById(R.id.iv_addmusic);
            tv_music = (TextView) itemView.findViewById(R.id.tv_music);
            iv_music = (ImageView) itemView.findViewById(R.id.iv_music);
            ll_musicitem = (LinearLayout) itemView.findViewById(R.id.ll_musicitem);
        }

    }
}
