package com.lipedev.mervelows.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Creator implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "role")
    private String role;

    @ForeignKey(entity = Comic.class, parentColumns = "uid", childColumns = "comicUid")
    private int comicUid;

    public Creator() {
    }

    @Ignore
    public Creator(String name, String role) {
        this.name = name;
        this.role = role;
    }

    @Ignore
    public Creator(String name, String role, int comicUid) {
        this.name = name;
        this.role = role;
        this.comicUid = comicUid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getComicUid() {
        return comicUid;
    }

    public void setComicUid(int comicUid) {
        this.comicUid = comicUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Creator{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", comicUid=" + comicUid +
                '}';
    }
}
