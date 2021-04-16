package com.uxmen.communityshovel;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private int distanceShoveled;
    private int peopleImpacted;

    public User(String email, String firstName, String lastName, String bio, int distanceShoveled, int peopleImpacted) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.distanceShoveled = distanceShoveled;
        this.peopleImpacted = peopleImpacted;
    }

    public User(Parcel source) {
        email = source.readString();
        firstName = source.readString();
        lastName = source.readString();
        bio = source.readString();
        distanceShoveled = source.readInt();
        peopleImpacted = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(bio);
        dest.writeInt(distanceShoveled);
        dest.writeInt(peopleImpacted);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getDistanceShoveled() {
        return distanceShoveled;
    }

    public void setDistanceShoveled(int distanceShoveled) {
        this.distanceShoveled = distanceShoveled;
    }

    public int getPeopleImpacted() {
        return peopleImpacted;
    }

    public void setPeopleImpacted(int peopleImpacted) {
        this.peopleImpacted = peopleImpacted;
    }

}
