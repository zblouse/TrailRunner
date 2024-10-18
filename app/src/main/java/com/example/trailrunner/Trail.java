package com.example.trailrunner;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Trail implements Parcelable, Serializable {

    private int id;
    private String trailName;
    private double trailDistance;
    private String trailDistanceUnit;
    private String uid;
    private double userTrailDistance;

    public Trail(int id, String trailName, double trailDistance, String trailDistanceUnit, String uid, double userTrailDistance){
        this.id = id;
        this.trailName = trailName;
        this.trailDistance = trailDistance;
        this.trailDistanceUnit = trailDistanceUnit;
        this.uid = uid;
        this.userTrailDistance = userTrailDistance;
    }

    public Trail(String trailName, double trailDistance, String trailDistanceUnit, String uid, double userTrailDistance){
        this.trailName = trailName;
        this.trailDistance = trailDistance;
        this.trailDistanceUnit = trailDistanceUnit;
        this.uid = uid;
        this.userTrailDistance = userTrailDistance;
    }

    protected Trail(Parcel in) {
        this.id = in.readInt();
        this.trailName = in.readString();
        this.trailDistance = in.readDouble();
        this.trailDistanceUnit = in.readString();
        this.uid = in.readString();
        this.userTrailDistance = in.readDouble();
    }

    public static final Creator<Trail> CREATOR = new Creator<Trail>() {
        @Override
        public Trail createFromParcel(Parcel in) {
            return new Trail(in);
        }

        @Override
        public Trail[] newArray(int size) {
            return new Trail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.trailName);
        parcel.writeDouble(this.trailDistance);
        parcel.writeString(this.trailDistanceUnit);
        parcel.writeString(this.uid);
        parcel.writeDouble(this.userTrailDistance);
    }

    public int getId(){
        return this.id;
    }

    public String getTrailName(){
        return this.trailName;
    }

    public void setTrailName(String trailName){
        this.trailName = trailName;
    }

    public double getTrailDistance(){
        return this.trailDistance;
    }

    public void setTrailDistance(double trailDistance){
        this.trailDistance = trailDistance;
    }

    public String getTrailDistanceUnit(){
        return this.trailDistanceUnit;
    }

    public void setTrailDistanceUnit(String trailDistanceUnit){
        this.trailDistanceUnit = trailDistanceUnit;
    }

    public String getUid(){
        return this.uid;
    }

    public double getUserTrailDistance(){
        return this.userTrailDistance;
    }

    public void setUserTrailDistance(double userTrailDistance){
        this.userTrailDistance = userTrailDistance;
    }
}
