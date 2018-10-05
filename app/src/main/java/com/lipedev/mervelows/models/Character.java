package com.lipedev.mervelows.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Character implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ForeignKey(entity = Comic.class, parentColumns = "uid", childColumns = "comicUid")
    private int comicUid;

    public Character(String name) {
        this.name = name;
    }

    public int getComicUid() {
        return comicUid;
    }

    public void setComicUid(int comicUid) {
        this.comicUid = comicUid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Character{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                '}';
    }
}
