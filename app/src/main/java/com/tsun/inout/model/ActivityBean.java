package com.tsun.inout.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *	record all information of activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:27
 ************************************************************************
 *	update time			editor				updated information
 */

public class ActivityBean implements Parcelable{

    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String startDateTime;
    private String endDateTime;
    private String comments;
    private String status;
    private String activityTypeId;
    private String id;
    private int isRepeat;
    private String repeatId;
    private String repeatUnitId;
    private String repeatUnitName;
    private String repeatStartDate;
    private String repeatEndDate;
    private int repeatFrequency;
    private int isWorkingAlone;
    private String contact;
    private String activityType;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
    private String userName;
    private String groupName;
    private String userId;
    private ArrayList<Integer> selectedGroups;

    public ActivityBean(){
        selectedGroups = new ArrayList<Integer>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.startDateTime);
        dest.writeString(this.endDateTime);
        dest.writeString(this.comments);
        dest.writeString(this.status);
        dest.writeString(this.activityTypeId);
        dest.writeString(this.contact);
        dest.writeInt(this.isWorkingAlone);
        dest.writeInt(this.isRepeat);
        dest.writeString(this.repeatUnitId);
        dest.writeString(this.repeatUnitName);
        dest.writeInt(this.repeatFrequency);
        dest.writeString(this.repeatStartDate);
        dest.writeString(this.repeatEndDate);
        dest.writeString(this.repeatId);
        dest.writeString(this.groupName);
        dest.writeString(this.activityType);
        dest.writeSerializable(this.selectedGroups);
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
        this.startDateTime = in.readString();
        this.endDateTime = in.readString();
        this.comments = in.readString();
        this.status = in.readString();
        this.activityTypeId = in.readString();
        this.contact = in.readString();
        this.isWorkingAlone = in.readInt();
        this.isRepeat = in.readInt();
        this.repeatUnitId = in.readString();
        this.repeatUnitName = in.readString();
        this.repeatFrequency = in.readInt();
        this.repeatStartDate = in.readString();
        this.repeatEndDate = in.readString();
        this.repeatId = in.readString();
        this.groupName = in.readString();
        this.activityType = in.readString();
        this.selectedGroups =  (ArrayList<Integer>) in.readSerializable();
    }



    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getComments() {
        return comments;
    }

    public String getStatus() {
        return status;
    }

    public String getActivityTypeId() {
        return activityTypeId;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActivityTypeId(String type) {
        this.activityTypeId = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepeatUnitId() {
        return repeatUnitId;
    }

    public void setRepeatUnitId(String repeatUnitId) {
        this.repeatUnitId = repeatUnitId;
    }

    public int getIsWorkingAlone() {
        return isWorkingAlone;
    }

    public void setIsWorkingAlone(int isWorkingAlone) {
        this.isWorkingAlone = isWorkingAlone;
    }

    public void setRepeatStartDate(String repeatStartDate) {
        this.repeatStartDate = repeatStartDate;
    }

    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }

    public String getRepeatStartDate() {
        return repeatStartDate;
    }

    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setRepeatFrequency(int repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }

    public int getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endEndDate) {
        this.endDate = endEndDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setIsRepeat(int isRepeat) {
        this.isRepeat = isRepeat;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsRepeat() {
        return isRepeat;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getUserId() {
        return userId;
    }

    public void setRepeatUnitName(String repeatUnitName) {
        this.repeatUnitName = repeatUnitName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRepeatUnitName() {
        return repeatUnitName;
    }

    public String getUserName() {
        return userName;
    }

    public void setSelectedGroups(ArrayList<Integer> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public ArrayList<Integer> getSelectedGroups() {
        return selectedGroups;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("startDate", this.getStartDate());
            jsonObject.put("startTime", this.getStartTime());

            if(!("".equals(this.getEndDate())) && this.getEndDate() != null){
                jsonObject.put("endDate", this.getEndDate());
                jsonObject.put("endTime", this.getEndTime());
            }
            jsonObject.put("activityTypeId",this.getActivityTypeId());
            jsonObject.put("contact", this.getContact());
            jsonObject.put("isWorkingAlone", this.getIsWorkingAlone());
            jsonObject.put("comments", this.getComments());
            if(this.getRepeatFrequency() != 0 && !("".equals(this.getEndDate())) && this.getEndDate() != null){
                jsonObject.put("repeatFrequency", this.getRepeatFrequency());
                jsonObject.put("repeatUnit", this.getRepeatUnitId());
                jsonObject.put("repeatStartDate", this.getRepeatStartDate());
                jsonObject.put("repeatEndDate", this.getRepeatEndDate());
            }
            if(!selectedGroups.isEmpty()){
                JSONArray jsonArray = new JSONArray(selectedGroups);
                try {
                    jsonObject.put("selectedGroups",jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
