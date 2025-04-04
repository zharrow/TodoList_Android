package com.example.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository categoryRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> categorySaved = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> categoryDeleted = new MutableLiveData<>(false);
    private long currentUserId;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        currentUserId = SharedPreferencesManager.getUserId(application);
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategoriesByUserId(currentUserId);
    }

    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryRepository.getCategoryById(categoryId);
    }

    public void saveCategory(String name, String color, String icon) {
        isLoading.setValue(true);

        if (name.isEmpty()) {
            errorMessage.setValue("Le nom est obligatoire");
            isLoading.setValue(false);
            return;
        }

        Category category = new Category(name, color, icon, currentUserId);
        long categoryId = categoryRepository.insert(category);

        if (categoryId > 0) {
            categorySaved.setValue(true);
        } else {
            errorMessage.setValue("Erreur lors de la sauvegarde de la catégorie");
        }

        isLoading.setValue(false);
    }

    public void updateCategory(Category category) {
        isLoading.setValue(true);

        if (category.getName().isEmpty()) {
            errorMessage.setValue("Le nom est obligatoire");
            isLoading.setValue(false);
            return;
        }

        categoryRepository.update(category);
        categorySaved.setValue(true);
        isLoading.setValue(false);
    }

    public void deleteCategory(Category category) {
        isLoading.setValue(true);
        categoryRepository.delete(category);
        categoryDeleted.setValue(true);
        isLoading.setValue(false);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getCategorySaved() {
        return categorySaved;
    }

    public LiveData<Boolean> getCategoryDeleted() {
        return categoryDeleted;
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void resetCategorySaved() {
        categorySaved.setValue(false);
    }

    public void resetCategoryDeleted() {
        categoryDeleted.setValue(false);
    }
}
