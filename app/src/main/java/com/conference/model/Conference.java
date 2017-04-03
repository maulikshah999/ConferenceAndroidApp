package com.conference.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Conference implements Parcelable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    String conf_ID = "", conf_name = "", conf_details = "", conf_title = "", conf_location = "", conf_time = "",
            conf_date = "", guest_speaker = "",image="";

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getConf_ID() {
        return conf_ID;
    }

    public void setConf_ID(String conf_ID) {
        this.conf_ID = conf_ID;
    }

    public String getConf_name() {
        return conf_name;
    }

    public void setConf_name(String conf_name) {
        this.conf_name = conf_name;
    }

    public String getConf_details() {
        return conf_details;
    }

    public void setConf_details(String conf_details) {
        this.conf_details = conf_details;
    }

    public String getConf_title() {
        return conf_title;
    }

    public void setConf_title(String conf_title) {
        this.conf_title = conf_title;
    }

    public String getConf_location() {
        return conf_location;
    }

    public void setConf_location(String conf_location) {
        this.conf_location = conf_location;
    }

    public String getConf_time() {
        return conf_time;
    }

    public void setConf_time(String conf_time) {
        this.conf_time = conf_time;
    }

    public String getConf_date() {
        return conf_date;
    }

    public void setConf_date(String conf_date) {
        this.conf_date = conf_date;
    }

    public String getGuest_speaker() {
        return guest_speaker;
    }

    public void setGuest_speaker(String guest_speaker) {
        this.guest_speaker = guest_speaker;
    }

    public Conference(){}

    private Conference(Parcel in) {
        /*Order matters here: it should be same as write.*/
        conf_ID = in.readString();
        conf_name = in.readString();
        conf_details = in.readString();
        conf_title = in.readString();
        conf_location = in.readString();
        conf_time = in.readString();
        conf_date = in.readString();
        guest_speaker = in.readString();
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        /*Order matters*/
        parcel.writeString(conf_ID);
        parcel.writeString(conf_name);
        parcel.writeString(conf_details);
        parcel.writeString(conf_title);
        parcel.writeString(conf_location);
        parcel.writeString(conf_time);
        parcel.writeString(conf_date);
        parcel.writeString(guest_speaker);
        parcel.writeString(image);
    }

    public static final Creator<Conference> CREATOR = new Creator<Conference>() {
        @Override
        public Conference createFromParcel(Parcel in) {
            return new Conference(in);
        }

        @Override
        public Conference[] newArray(int size) {
            return new Conference[size];
        }
    };
}
