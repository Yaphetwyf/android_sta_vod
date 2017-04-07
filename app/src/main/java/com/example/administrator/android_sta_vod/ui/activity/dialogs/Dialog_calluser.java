package com.example.administrator.android_sta_vod.ui.activity.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;

/**
 * Created by Administrator on 2017/3/23 0023.
 */

public class Dialog_calluser extends Dialog implements View.OnClickListener{

    private ImageButton cancel;
    private On_cancel_listener on_cancel_listener;
    private TextView user_id;


    public Dialog_calluser(Context context) {
        super(context);
    }

    public Dialog_calluser(Context context, int themeResId, On_cancel_listener on_cancel_listener) {
        super(context, themeResId);
        this.on_cancel_listener = on_cancel_listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_calling);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        cancel = (ImageButton) findViewById(R.id.btn_call_cancel);
        user_id = (TextView) findViewById(R.id.tv_call_userid);
        cancel.setOnClickListener(this);
    }
    public void set_user(String username){

        user_id.setText(username);
    }
    @Override
    public void onClick(View v) {
        on_cancel_listener.onClick(v);
    }
    public interface On_cancel_listener {
        void  onClick(View v);
    }

}
