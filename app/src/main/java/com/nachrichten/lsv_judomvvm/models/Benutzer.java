package com.nachrichten.lsv_judomvvm.models;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

public class Benutzer implements Parcelable{

    private int ID;
    private String Name;
    private String APIKey;

    public Benutzer(int pID,String pName,String pAPIKey){
        ID = pID;
        Name = pName;
        APIKey = pAPIKey;
    }

    protected Benutzer(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
        APIKey = in.readString();
    }

    public static final Creator<Benutzer> CREATOR = new Creator<Benutzer>() {
        @Override
        public Benutzer createFromParcel(Parcel in) {
            return new Benutzer(in);
        }

        @Override
        public Benutzer[] newArray(int size) {
            return new Benutzer[size];
        }
    };

    public static Benutzer BenutzerFromString(String pDaten){
        int arraySize = 4;
        String[] results = new String[arraySize];
        String substr;
        for(int i = 0; i < arraySize; i++){
            if(i == arraySize-1){
                substr = pDaten;
            }else
                substr = pDaten.substring(0, pDaten.indexOf(":"));
            if(!substr.equals("")){
                results[i] = substr;
            }
            pDaten = pDaten.substring(pDaten.indexOf(":")+1);
        }
        if(results[0] != null && results[1] != null&& results[2]!= null && results[3] != null){
            return new Benutzer(Integer.parseInt(results[0]),results[1],results[3]);
        }
        return null;
    }

    public static String BenutzerToString(Benutzer pBenutzer){
        return "<tr>"+pBenutzer.getID()
                +"/"+pBenutzer.getName()
                +"/"+pBenutzer.getAPIKey()+"<tr/>";
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAPIKey(){
        return APIKey;
    }

    public void setAPIKey(String pAPIKey){
        APIKey = pAPIKey;
    }

    public int getID() {
        return ID;
    }

    public void setID(int pID){
        ID = pID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(Name);
        parcel.writeString(APIKey);
    }

}
