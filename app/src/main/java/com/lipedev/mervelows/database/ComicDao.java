package com.lipedev.mervelows.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.lipedev.mervelows.models.Character;
import com.lipedev.mervelows.models.Comic;
import com.lipedev.mervelows.models.Creator;

import java.util.List;

@Dao
public interface ComicDao {

    @Query("SELECT * FROM comic")
    List<Comic> getAll();

    @Query("SELECT * FROM comic WHERE isFavorite=:isFavorite AND title LIKE '%' || :search || '%' ORDER BY title")
    List<Comic> getFavorites(boolean isFavorite, String search);

    @Query("SELECT * FROM comic WHERE isFavorite=:isFavorite AND title LIKE '%' || :search || '%' ORDER BY launch_date")
    List<Comic> getFavoritesByDate(boolean isFavorite, String search);

    @Query("SELECT * FROM comic WHERE marvel_id=:id")
    Comic findByMarvelId(long id);

    @Query("SELECT * FROM creator WHERE comicUid=:comicUid")
    List<Creator> getCreators(int comicUid);

    @Query("SELECT * FROM character WHERE comicUid=:comicUid")
    List<Character> getCharacters(int comicUid);

    @Update
    void update(Comic comics);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Comic comic);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCreator(Creator creator);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCharacter(Character character);
}
