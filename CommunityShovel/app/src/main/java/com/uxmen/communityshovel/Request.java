package com.uxmen.communityshovel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Request implements Parcelable {
    private String requestId;
    private String creatorId;
    private String info;
    private String volunteers;
    private String comments;
    private int time;
    private int upvotes;
    private int status;
    private double xCoord;
    private double yCoord;

    public Request(String requestId, String creatorId, String info, String volunteers, String comments, int time, int upvotes, int status, double xCoord, double yCoord) {
        this.requestId = requestId;
        this.creatorId = creatorId;
        this.info = info;
        this.volunteers = volunteers;
        this.comments = comments;
        this.time = time;
        this.upvotes = upvotes;
        this.status = status;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public Request(Parcel source) {
        this.requestId = source.readString();
        this.creatorId = source.readString();
        this.info = source.readString();
        this.volunteers = source.readString();
        this.comments = source.readString();
        this.time = source.readInt();
        this.upvotes = source.readInt();
        this.status = source.readInt();
        this.xCoord = source.readDouble();
        this.yCoord = source.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.requestId);
        dest.writeString(this.creatorId);
        dest.writeString(this.info);
        dest.writeString(this.volunteers);
        dest.writeString(this.comments);
        dest.writeInt(this.time);
        dest.writeInt(this.upvotes);
        dest.writeInt(this.status);
        dest.writeDouble(this.xCoord);
        dest.writeDouble(this.yCoord);
    }

    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {

        @Override
        public Request createFromParcel(Parcel source) {
            return new Request(source);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public String getRequestId() { return this.requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getCreatorId() { return this.creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public String getInfo() { return this.info; }
    public void setInfo(String info) { this.info = info; }

    public String getVolunteers() { return this.volunteers; }
    public void setVolunteers(String volunteers) { this.volunteers = volunteers; }

    public String getComments() { return this.comments; }
    public void setComments(String comments) { this.comments = comments; }

    public int getTime() { return this.time; }
    public void setTime(int time) { this.time = time; }

    public int getUpvotes() { return this.upvotes; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }

    public int getStatus() { return this.status; }
    public void setStatus(int status) { this.status = status; }

    public double getXCoord() { return this.xCoord; }
    public void setXCoord(double xCoord) { this.xCoord = xCoord; }

    public double getYCoord() { return this.yCoord; }
    public void setYCoord(double yCoord) { this.yCoord = yCoord; }
}
