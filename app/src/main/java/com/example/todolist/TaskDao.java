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
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskById(long taskId);

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasksByUserId(long userId);

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByCategoryId(long categoryId);

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted AND userId = :userId ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByCompletionStatus(boolean isCompleted, long userId);

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate AND userId = :userId ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksInDateRange(String startDate, String endDate, long userId);

    @Query("DELETE FROM tasks WHERE userId = :userId")
    void deleteAllTasksByUserId(long userId);
}