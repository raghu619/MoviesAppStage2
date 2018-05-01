package com.example.daou5____.moviesappstage1.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Reviews implements Parcelable {
    @SerializedName( "id" )
    private String id;
    @SerializedName( "author" )
    private String author;
    @SerializedName( "content" )
    private String content;
    @SerializedName( "url" )
    private String url;

    public Reviews ( String id, String author, String content, String url ) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);

    }

    //creator
    public static final Creator CREATOR = new Creator() {

        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }

    };

    public Reviews(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }
}
