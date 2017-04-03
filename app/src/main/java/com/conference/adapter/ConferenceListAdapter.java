package com.conference.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.conference.R;
import com.conference.custom.Constants;
import com.conference.custom.JoinDialog;
import com.conference.custom.ObjectSerializer;
import com.conference.model.Conference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.media.CamcorderProfile.get;

/**
 * Created by maulik on 11/27/16.
 */

public class ConferenceListAdapter extends BaseAdapter {

    Activity mContext;
    ArrayList<Conference> alConfList = new ArrayList<Conference>();
    ArrayList<String> alJoinedConf = new ArrayList<String>();
    LayoutInflater inflater;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ImageLoaderConfiguration imageLoaderConfig;
    DisplayImageOptions options;

    public ConferenceListAdapter(Activity activity, ArrayList<Conference> alConfList) {
        mContext = activity;
        sharedPref = mContext.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        sharedPref.getString(Constants.SHARED_EMAIL, "");
        sharedPref.getString(Constants.SHARED_PHONE, "");
        this.alConfList = alConfList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        retriveSharedPrefConfID();
        options = new DisplayImageOptions.Builder().showImageOnFail(R.drawable.icon_conference)
                .showImageOnLoading(R.drawable.icon_conference)
                .showImageForEmptyUri(R.drawable.icon_conference)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoaderConfig = new ImageLoaderConfiguration.Builder(mContext).defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(imageLoaderConfig);
    }

    @Override
    public int getCount() {
        return alConfList.size();
    }

    @Override
    public Object getItem(int position) {
        return alConfList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {

        TextView tvConfTitle;
        TextView tvConfTime;
        TextView tvConfLocation;
        TextView tvConfDate;
        Button btnJoin;
        ImageView ivConf;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.raw_conf_list, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.tvConfTitle = (TextView) vi.findViewById(R.id.tvConfTitle);
            holder.tvConfDate = (TextView) vi.findViewById(R.id.tvConfDate);
            holder.tvConfTime = (TextView) vi.findViewById(R.id.tvConfTime);
            holder.tvConfLocation = (TextView) vi.findViewById(R.id.tvConfLocation);
            holder.btnJoin = (Button) vi.findViewById(R.id.ivJoin);
            holder.ivConf = (ImageView) vi.findViewById(R.id.ivConfRawList);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        holder.tvConfTitle.setText(alConfList.get(position).getConf_title());
        holder.tvConfDate.setText(alConfList.get(position).getConf_date());
        holder.tvConfTime.setText(alConfList.get(position).getConf_time());
        holder.tvConfLocation.setText(alConfList.get(position).getConf_location());
        ImageLoader.getInstance().displayImage(alConfList.get(position).getImage(), holder.ivConf);
        final String confID = alConfList.get(position).getConf_ID();
        if (alJoinedConf.contains(confID)) {
            holder.btnJoin.setText("Joined");
        } else {
            holder.btnJoin.setText("Join");
        }

        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // first check in shared preference if user doesn't exist then call create join dialog
                if (sharedPref.getString(Constants.SHARED_USERNAME, "").length() <= 0) {
                    createJoinDialog(confID);
                } else if (!alJoinedConf.contains(confID)) {
                    addConfIDIntoSharedPref(confID);
                } else if (alJoinedConf.contains(confID)) {
                    Log.d("ConferenceLlistAdapter", ">>>" + alJoinedConf);
                    removeConfIDIntoSharedPref(confID);
                    Log.d("After removed >>>", ">>>" + alJoinedConf);
                }
            }
        });
        return vi;
    }

    private void addConfIDIntoSharedPref(String confID) {
        alJoinedConf.add(confID);

        try {
            editor.putString(Constants.SHARED_CONF_IDS, ObjectSerializer.serialize(alJoinedConf));
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
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
        notifyDataSetChanged();
    }

    private void retriveSharedPrefConfID() {
        try {
            alJoinedConf = (ArrayList<String>) ObjectSerializer.deserialize(sharedPref.getString(Constants.SHARED_CONF_IDS, ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    private void createJoinDialog(final String confID) {
        final JoinDialog joinDialog = new JoinDialog(mContext);
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
