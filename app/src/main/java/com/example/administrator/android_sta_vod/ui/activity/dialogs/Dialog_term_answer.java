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

public class Dialog_term_answer extends Dialog implements View.OnClickListener{


    private On_refuse_listener on_refuse_listener;
    private On_answer_listener on_answer_listener;
    private On_transfer_listener on_transfer_listener;
    private ImageButton term_refuse;
    private ImageButton term_answer;
    private TextView term_userid;

    public Dialog_term_answer(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_term_answer);
        term_refuse = (ImageButton) findViewById(R.id.btn_term_refuse);
        term_answer= (ImageButton) findViewById(R.id.btn_term_answer);
        term_userid = (TextView) findViewById(R.id.tv_term_userid);
        term_refuse.setOnClickListener(this);
        term_answer.setOnClickListener(this);
        findViewById(R.id.btn_cancel);
        setCanceledOnTouchOutside(false);


    }

    public void set_user(String username){

        term_userid.setText(username);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_term_refuse:
                on_refuse_listener.refuse_listener();
                break;
            case R.id.btn_term_answer:
                on_answer_listener.answer_listener();
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
