package com.example.todolist;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao taskDao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
    }

    public void insert(Task task, OnTaskInsertedCallback callback) {
        executor.execute(() -> {
            long id = taskDao.insert(task);
            // Retourner au thread principal pour le callback
            mainHandler.post(() -> callback.onTaskInserted(id));
        });
    }

    public void update(Task task) {
        executor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executor.execute(() -> taskDao.delete(task));
    }

    public LiveData<Task> getTaskById(long taskId) {
        return taskDao.getTaskById(taskId);
    }

    public LiveData<List<Task>> getAllTasksByUserId(long userId) {
        return taskDao.getAllTasksByUserId(userId);
    }

    public LiveData<List<Task>> getTasksByCategoryId(long categoryId) {
        return taskDao.getTasksByCategoryId(categoryId);
    }

    public LiveData<List<Task>> getTasksByCompletionStatus(boolean isCompleted, long userId) {
        return taskDao.getTasksByCompletionStatus(isCompleted, userId);
    }

    public LiveData<List<Task>> getTasksInDateRange(String startDate, String endDate, long userId) {
        return taskDao.getTasksInDateRange(startDate, endDate, userId);
    }

    public void deleteAllTasksByUserId(long userId) {
        executor.execute(() -> taskDao.deleteAllTasksByUserId(userId));
    }

    // Interface pour le callback d'insertion
    public interface OnTaskInsertedCallback {
        void onTaskInserted(long taskId);
    }
}