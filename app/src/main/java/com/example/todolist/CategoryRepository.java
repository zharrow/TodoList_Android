package com.example.todolist;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryRepository {
    private final CategoryDao categoryDao;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
    }

    public long insert(Category category) {
        return new InsertCategoryAsyncTask(categoryDao).doInBackground(category);
    }

    public void update(Category category) {
        new UpdateCategoryAsyncTask(categoryDao).execute(category);
    }

    public void delete(Category category) {
        new DeleteCategoryAsyncTask(categoryDao).execute(category);
    }

    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }

    public LiveData<List<Category>> getAllCategoriesByUserId(long userId) {
        return categoryDao.getAllCategoriesByUserId(userId);
    }

    public void deleteAllCategoriesByUserId(long userId) {
        new DeleteAllCategoriesByUserIdAsyncTask(categoryDao).execute(userId);
    }

    private static class InsertCategoryAsyncTask extends AsyncTask<Category, Void, Long> {
        private CategoryDao categoryDao;

        private InsertCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Long doInBackground(Category... categories) {
            return categoryDao.insert(categories[0]);
        }
    }

    private static class UpdateCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private UpdateCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.update(categories[0]);
            return null;
        }
    }

    private static class DeleteCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private DeleteCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.delete(categories[0]);
            return null;
        }
    }

    private static class DeleteAllCategoriesByUserIdAsyncTask extends AsyncTask<Long, Void, Void> {
        private CategoryDao categoryDao;

        private DeleteAllCategoriesByUserIdAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Long... userIds) {
            categoryDao.deleteAllCategoriesByUserId(userIds[0]);
            return null;
        }
    }
}
