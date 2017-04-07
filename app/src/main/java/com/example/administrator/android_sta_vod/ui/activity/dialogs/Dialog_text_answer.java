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
 * Created by Administrator on 2017/3/24 0024.
 */

public class Dialog_text_answer extends Dialog implements View.OnClickListener{

    private ImageButton refuse;
    private ImageButton answer;
    private ImageButton transfer;
    Dialog_text_answer.On_refuse_listener on_refuse_listener;
    Dialog_text_answer.On_answer_listener on_answer_listener;
    Dialog_text_answer.On_transfer_listener on_transfer_listener;
    private TextView tvUserid;

    public Dialog_text_answer(Context context) {
        super(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_answer);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        refuse = (ImageButton) findViewById(R.id.btn_refuse);
        answer = (ImageButton) findViewById(R.id.btn_answer);
        transfer = (ImageButton) findViewById(R.id.btn_transfer);
        tvUserid = (TextView) findViewById(R.id.tv_userid);
        refuse.setOnClickListener(this);
        answer.setOnClickListener(this);
        transfer.setOnClickListener(this);
    }
    public void set_user(String username){

        tvUserid.setText(username);
    }
    @Override
    public void onClick(View v) {
         switch (v.getId()){
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

    public void set_on_refuse_listener(Dialog_text_answer.On_refuse_listener on_refuse_listener){
        this.on_refuse_listener = on_refuse_listener;
    }
    public interface On_refuse_listener {
        void refuse_listener();
    }
    public void set_on_answer_listener(Dialog_text_answer.On_answer_listener on_answer_listener){
        this.on_answer_listener = on_answer_listener;
    }
    public interface On_answer_listener {
        void answer_listener();
    }
    public void set_on_transfer_listener(Dialog_text_answer.On_transfer_listener on_transfer_listener){
        this.on_transfer_listener = on_transfer_listener;
    }
    public interface On_transfer_listener {
        void transfer_listener();
    }

}
