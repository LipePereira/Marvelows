package com.lipedev.mervelows.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
public class Comic implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "marvel_id")
    private long marvelId;

    @ColumnInfo(name = "launch_date")
    private Date launchDate;

    @ColumnInfo(name = "price")
    private double price;

    @Ignore
    private List<Creator> creators;

    @Ignore
    private List<Character> characters;

    private String imagePath;
    private String imageExtension;

    private boolean isFavorite;

    public Comic() {
    }

    public Comic(String title, String description, long marvelId, Date launchDate, double price, List<Creator> creators, List<Character> characters, String imagePath, String imageExtension, boolean isFavorite) {
        this.title = title;
        this.description = description;
        this.marvelId = marvelId;
        this.launchDate = launchDate;
        this.price = price;
        this.creators = creators;
        this.characters = characters;
        this.imagePath = imagePath;
        this.imageExtension = imageExtension;
        this.isFavorite = isFavorite;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getMarvelId() {
        return marvelId;
    }

    public void setMarvelId(long marvelId) {
        this.marvelId = marvelId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public void setImageExtension(String imageExtension) {
        this.imageExtension = imageExtension;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Creator> getCreators() {
        return creators;
    }

    public void setCreators(List<Creator> creators) {
        this.creators = creators;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comic comic = (Comic) o;
        return marvelId == comic.marvelId &&
                title.equals(comic.title) &&
                description.equals(comic.description) &&
                launchDate.getTime() == comic.launchDate.getTime() &&
                imagePath.equals(comic.imagePath) &&
                imageExtension.equals(comic.imageExtension);
    }

    @Override
    public String toString() {
        return "Comic{" +
                "uid=" + uid +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", marvelId=" + marvelId +
                ", launchDate=" + launchDate +
                ", price=" + price +
                ", creators=" + creators +
                ", characters=" + characters +
                ", imagePath='" + imagePath + '\'' +
                ", imageExtension='" + imageExtension + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
