package com.example.todolist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    LiveData<Category> getCategoryById(long categoryId);

    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    LiveData<List<Category>> getAllCategoriesByUserId(long userId);

    @Query("DELETE FROM categories WHERE userId = :userId")
    void deleteAllCategoriesByUserId(long userId);
}
