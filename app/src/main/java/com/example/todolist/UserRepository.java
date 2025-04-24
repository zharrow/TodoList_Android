package com.example.todolist;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public void insert(User user, OnUserInsertedCallback callback) {
        executor.execute(() -> {
            long id = userDao.insert(user);
            mainHandler.post(() -> callback.onUserInserted(id));
        });
    }

    public void update(User user) {
        executor.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        executor.execute(() -> userDao.delete(user));
    }

    public LiveData<User> getUserById(long userId) {
        return userDao.getUserById(userId);
    }

    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public LiveData<User> login(String email, String password) {
        return userDao.login(email, password);
    }

    // Interface pour le callback d'insertion
    public interface OnUserInsertedCallback {
        void onUserInserted(long userId);
    }
}