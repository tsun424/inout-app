package com.tsun.inout.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *	record all information of activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:27
 ************************************************************************
 *	update time			editor				updated information
 */

public class ActivityBean implements Parcelable{

    private String startTime;
    private String endTime;
    private String description;
    private String status;
    private String duration;
    private String type;
    private String id;

    public ActivityBean(){

    }
    public ActivityBean(String id, String startTime, String endTime, String description, String status, String duration){
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.status = status;
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.description);
        dest.writeString(this.status);
        dest.writeString(this.duration);
    }

    public static final Parcelable.Creator<ActivityBean> CREATOR
            = new Parcelable.Creator<ActivityBean>() {
        public ActivityBean createFromParcel(Parcel in) {
            return new ActivityBean(in);
        }

        public ActivityBean[] newArray(int size) {
            return new ActivityBean[size];
        }
    };
    private ActivityBean(Parcel in) {
        this.id = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.description = in.readString();
        this.status = in.readString();
        this.duration = in.readString();
    }



    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
