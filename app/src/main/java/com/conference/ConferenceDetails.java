package com.conference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.conference.custom.Constants;
import com.conference.custom.JoinDialog;
import com.conference.custom.ObjectSerializer;
import com.conference.model.Conference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by maulik on 11/27/16.
 */

public class ConferenceDetails extends AppCompatActivity {

    Bundle bundle;
    Conference confDetail;
    TextView tvConfTitle, tvConfDetails, tvConfLocation, tvConfDate, tvConfOrganizer, tvConfTime;
    Button btnJoin;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ArrayList<String> alJoinedConf = new ArrayList<String>();
    ImageLoaderConfiguration imageLoaderConfig;
    DisplayImageOptions options;
    ImageView ivConf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conference_details);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            confDetail = bundle.getParcelable(Constants.OBJ_CONFERENCE);
        }

        tvConfTitle = (TextView) findViewById(R.id.tvConfTitle);
        tvConfDate = (TextView) findViewById(R.id.tvConfDateCD);
        tvConfTime = (TextView) findViewById(R.id.tvConfTimeCD);
        tvConfLocation = (TextView) findViewById(R.id.tvConfLocationCD);
        tvConfOrganizer = (TextView) findViewById(R.id.tvOrganizerCD);
        tvConfDetails = (TextView) findViewById(R.id.tvConfDetailsCD);
        ivConf = (ImageView) findViewById(R.id.ivImage);
        setConferenceDetail();
    }

    private void setConferenceDetail() {
        setTitle(R.string.txt_conf_details);
        tvConfTitle.setText(confDetail.getConf_title());
        tvConfDate.setText(confDetail.getConf_date());
        tvConfTime.setText(confDetail.getConf_time());
        tvConfLocation.setText(confDetail.getConf_location());
        tvConfOrganizer.setText(confDetail.getGuest_speaker());
        tvConfDetails.setText(confDetail.getConf_details());

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        sharedPref.getString(Constants.SHARED_USERNAME, "");
        sharedPref.getString(Constants.SHARED_EMAIL, "");
        sharedPref.getString(Constants.SHARED_PHONE, "");
        sharedPref.getString(Constants.SHARED_CONF_IDS,"");
        btnJoin = (Button) findViewById(R.id.btnJoinConfDetails);
        retriveSharedPrefConfID();

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ConferenceLlistAdapter",">>>"+alJoinedConf);
                if (sharedPref.getString(Constants.SHARED_USERNAME, "").length() <= 0) {
                    createJoinDialog(confDetail.getConf_ID());
                } else if (!alJoinedConf.contains(confDetail.getConf_ID())) {
                    addConfIDIntoSharedPref(confDetail.getConf_ID());
                } else if(alJoinedConf.contains(confDetail.getConf_ID())){
                    removeConfIDIntoSharedPref(confDetail.getConf_ID());
                    Log.d("After removed >>>",">>>"+alJoinedConf);
                }
            }
        });

        options = new DisplayImageOptions.Builder().showImageOnFail(R.drawable.icon_conference)
                .showImageOnLoading(R.drawable.icon_conference)
                .showImageForEmptyUri(R.drawable.icon_conference)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoaderConfig = new ImageLoaderConfiguration.Builder(ConferenceDetails.this).defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(imageLoaderConfig);
        ImageLoader.getInstance().displayImage(confDetail.getImage(), ivConf);
    }

    private void addConfIDIntoSharedPref(String confID) {
        alJoinedConf.add(confID);

        try {
            editor.putString(Constants.SHARED_CONF_IDS, ObjectSerializer.serialize(alJoinedConf));
        } catch (Exception e) {
            e.printStackTrace();
        }

        editor.commit();

    }

    private void removeConfIDIntoSharedPref(String confID) {
        alJoinedConf.remove(confID);
        try {
            editor.putString(Constants.SHARED_CONF_IDS, ObjectSerializer.serialize(alJoinedConf));
            // alJoinedConf = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString(TASKS, ObjectSerializer.serialize(new ArrayList<task>()))
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        editor.commit();

    }

    private void retriveSharedPrefConfID() {
        try {
            alJoinedConf = (ArrayList<String>) ObjectSerializer.deserialize(sharedPref.getString(Constants.SHARED_CONF_IDS, ObjectSerializer.serialize(new ArrayList<String>())));
            Log.d("Join Conf IDs",">>>"+alJoinedConf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (alJoinedConf.contains(confDetail.getConf_ID())) {
            btnJoin.setText("Joined");
        } else {
            btnJoin.setText("Join");
        }
    }

    private void createJoinDialog(final String confID) {
        final JoinDialog joinDialog = new JoinDialog(ConferenceDetails.this);
        final EditText edUsername = (EditText) joinDialog.findViewById(R.id.edUsernameCustomDialog);
        final EditText edPhoneNo = (EditText) joinDialog.findViewById(R.id.edPhoneNoCustDialog);
        final EditText edEmail = (EditText) joinDialog.findViewById(R.id.edEmailCustDialog);


        final Button btnJoin = (Button) joinDialog.findViewById(R.id.btnJoinCustDialog);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edUsername.getText().toString();
                String email = edEmail.getText().toString();
                String phoneNo = edPhoneNo.getText().toString();
                if (username.length() <= 0) {
                    edUsername.setError("Username is required");
                }
                if (email.length() <= 0) {
                    edEmail.setError("Email is required");
                }
                if (phoneNo.length() <= 0) {
                    edPhoneNo.setError("Phone Number is required");
                } else {
                    editor.putString(Constants.SHARED_USERNAME, username);
                    editor.putString(Constants.SHARED_EMAIL, email);
                    editor.putString(Constants.SHARED_PHONE, phoneNo);
                    addConfIDIntoSharedPref(confID);
                    editor.commit();
                    joinDialog.dismiss();
                }
            }
        });
        joinDialog.show();
    }

}
