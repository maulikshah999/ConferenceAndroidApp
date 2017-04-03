package com.conference.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.conference.R;

/**
 * Created by maulik on 11/28/16.
 */

public class JoinDialog extends Dialog{

    Activity mActivity;

    Button btnJoin, btnCancel;

    public JoinDialog(Activity activity) {
        super(activity);
        mActivity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_join_layout);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);

        btnJoin = (Button) findViewById(R.id.btnJoinCustDialog);
        //btnJoin.setOnClickListener(mActivity.this);
        btnCancel = (Button) findViewById(R.id.btnCancelCustDialog);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });
    }





}
