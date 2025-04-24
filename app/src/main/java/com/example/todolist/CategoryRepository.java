package com.example.todolist;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
    }
    public Category getOrCreateDefaultCategory(long userId) {
        // First, check if a default category exists
        Category existingCategory = categoryDao.findDefaultCategorySync(userId);

        if (existingCategory != null) {
            return existingCategory;
        }

        // If no default category exists, create one
        Category defaultCategory = new Category(
                "Sans Catégorie",
                "#9C27B0", // Default category color
                "ic_category", // Default category icon
                userId
        );

        // Insert synchronously
        long categoryId = categoryDao.insert(defaultCategory);
        defaultCategory.setId(categoryId);

        return defaultCategory;
    }


    public void insert(Category category, OnCategoryInsertedCallback callback) {
        executor.execute(() -> {
            long id = categoryDao.insert(category);
            // Retourner au thread principal pour le callback
            mainHandler.post(() -> callback.onCategoryInserted(id));
        });
    }

    public LiveData<Category> ensureDefaultCategory(long userId) {
        MutableLiveData<Category> defaultCategoryLiveData = new MutableLiveData<>();

        executor.execute(() -> {
            // Check if default category exists
            Category existingCategory = categoryDao.findDefaultCategorySync(userId);

            if (existingCategory == null) {
                // Create a new default category
                Category defaultCategory = new Category(
                        "Sans Catégorie",
                        "#9C27B0", // Default category color
                        "ic_category", // Default category icon
                        userId
                );

                // Insert and get the ID
                long categoryId = categoryDao.insert(defaultCategory);
                defaultCategory.setId(categoryId);

                // Post to main thread
                mainHandler.post(() -> defaultCategoryLiveData.setValue(defaultCategory));
            } else {
                // Post existing category to main thread
                mainHandler.post(() -> defaultCategoryLiveData.setValue(existingCategory));
            }
        });

        return defaultCategoryLiveData;
    }

    public void update(Category category) {
        executor.execute(() -> categoryDao.update(category));
    }

    public void delete(Category category) {
        executor.execute(() -> categoryDao.delete(category));
    }

    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }

    public LiveData<List<Category>> getAllCategoriesByUserId(long userId) {
        return categoryDao.getAllCategoriesByUserId(userId);
    }

    public void deleteAllCategoriesByUserId(long userId) {
        executor.execute(() -> categoryDao.deleteAllCategoriesByUserId(userId));
    }

    // Interface pour le callback d'insertion
    public interface OnCategoryInsertedCallback {
        void onCategoryInserted(long categoryId);
    }
}