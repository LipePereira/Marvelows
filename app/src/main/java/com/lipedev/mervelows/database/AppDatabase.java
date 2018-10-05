package com.lipedev.mervelows.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.lipedev.mervelows.models.Character;
import com.lipedev.mervelows.models.Comic;
import com.lipedev.mervelows.models.Creator;

@Database(entities = {Comic.class, Character.class, Creator.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase DBInstance;

    public abstract ComicDao comicDao();

    public static AppDatabase getInstance(Context context) {
        if (DBInstance == null) {
            DBInstance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "marvelows-database")
                    .allowMainThreadQueries()
                    .build();
        }
        return DBInstance;
    }
}
