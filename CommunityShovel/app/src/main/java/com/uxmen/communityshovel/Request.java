package com.uxmen.communityshovel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Request implements Parcelable {
    private String creatorId;
    private String info;
    private ArrayList<String> volunteers;
    private ArrayList<String> comments;
    private int time;
    private int upvotes;
    private double xCoord;
    private double yCoord;

    private String request_id;

    public Request(String creatorId, String info, ArrayList<String> volunteers, ArrayList<String> comments, int time, int upvotes, double xCoord, double yCoord, String request_id) {
        this.creatorId = creatorId;
        this.info = info;
        this.volunteers = volunteers;
        this.comments = comments;
        this.time = time;
        this.upvotes = upvotes;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.request_id = request_id;
    }

    public Request(Parcel source) {
        this.creatorId = source.readString();
        this.info = source.readString();
        this.volunteers = source.createStringArrayList();
        this.comments = source.createStringArrayList();
        this.time = source.readInt();
        this.upvotes = source.readInt();
        this.xCoord = source.readDouble();
        this.yCoord = source.readDouble();
        this.request_id = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.creatorId);
        dest.writeString(this.info);
        dest.writeStringList(this.volunteers);
        dest.writeStringList(this.comments);
        dest.writeInt(this.time);
        dest.writeInt(this.upvotes);
        dest.writeDouble(this.xCoord);
        dest.writeDouble(this.yCoord);
        dest.writeString(this.request_id);
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

    public String getCreatorId() { return this.creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public String getInfo() { return this.info; }
    public void setInfo(String info) { this.info = info; }

    public ArrayList<String> getVolunteers() { return this.volunteers; }
    public void setVolunteers(ArrayList<String> volunteers) { this.volunteers = volunteers; }

    public ArrayList<String> getComments() { return this.comments; }
    public void setComments(ArrayList<String> comments) { this.comments = comments; }

    public int getTime() { return this.time; }
    public void setTime(int time) { this.time = time; }

    public int getUpvotes() { return this.upvotes; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }

    public double getXCoord() { return this.xCoord; }
    public void setXCoord(double xCoord) { this.xCoord = xCoord; }

    public double getYCoord() { return this.yCoord; }
    public void setYCoord(double yCoord) { this.yCoord = yCoord; }

    public String getRequestID() { return this.request_id; }
}
