package com.example.administrator.android_sta_vod.ui.activity.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.administrator.android_sta_vod.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AVSZ on 2017/3/1.
 */

public class Dialog_answer extends Dialog {
    @BindView(R.id.tv_userid)
    TextView tvUserid;
    @BindView(R.id.btn_refuse)
    ImageButton btnRefuse;
    @BindView(R.id.btn_answer)
    ImageButton btnAnswer;
    @BindView(R.id.btn_transfer)
    ImageButton btnTransfer;

    private On_refuse_listener on_refuse_listener;
    private On_answer_listener on_answer_listener;
    private On_transfer_listener on_transfer_listener;
    public Dialog_answer(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_answer);
        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

    }

    public void set_user(String username){

        tvUserid.setText(username);
    }

    @OnClick({R.id.btn_refuse, R.id.btn_answer, R.id.btn_transfer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_refuse:
                on_refuse_listener.refuse_listener();
                break;
            case R.id.btn_answer:
                on_answer_listener.answer_listener();
                break;
            case R.id.btn_transfer:
                on_transfer_listener.transfer_listener();
                break;
        }
    }

    public interface On_refuse_listener{
        void refuse_listener();
    }
    public void set_on_refuse_listener(On_refuse_listener on_refuse_listener){
        this.on_refuse_listener = on_refuse_listener;
    }
    public interface On_answer_listener{
        void answer_listener();
    }
    public void set_on_answer_listener(On_answer_listener on_answer_listener){
        this.on_answer_listener = on_answer_listener;
    }
    public interface On_transfer_listener{
        void transfer_listener();
    }
    public void set_on_transfer_listener(On_transfer_listener on_transfer_listener){
        this.on_transfer_listener = on_transfer_listener;
    }
}
