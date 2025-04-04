package com.example.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> taskSaved = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> taskDeleted = new MutableLiveData<>(false);
    private final long currentUserId;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        currentUserId = SharedPreferencesManager.getUserId(application);
    }

    public LiveData<List<Task>> getAllTasks() {
        return taskRepository.getAllTasksByUserId(currentUserId);
    }

    public LiveData<List<Task>> getTasksByCategoryId(long categoryId) {
        return taskRepository.getTasksByCategoryId(categoryId);
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return taskRepository.getTasksByCompletionStatus(true, currentUserId);
    }

    public LiveData<List<Task>> getPendingTasks() {
        return taskRepository.getTasksByCompletionStatus(false, currentUserId);
    }

    public LiveData<List<Task>> getTasksInDateRange(String startDate, String endDate) {
        return taskRepository.getTasksInDateRange(startDate, endDate, currentUserId);
    }

    public LiveData<Task> getTaskById(long taskId) {
        return taskRepository.getTaskById(taskId);
    }

    public void saveTask(String title, String description, String dueDate, Priority priority, long categoryId) {
        isLoading.setValue(true);

        if (title.isEmpty()) {
            errorMessage.setValue("Le titre est obligatoire");
            isLoading.setValue(false);
            return;
        }

        Task task = new Task(title, description, dueDate, priority, categoryId, currentUserId);
        long taskId = taskRepository.insert(task);

        if (taskId > 0) {
            taskSaved.setValue(true);
        } else {
            errorMessage.setValue("Erreur lors de la sauvegarde de la tâche");
        }

        isLoading.setValue(false);
    }

    public void updateTask(Task task) {
        isLoading.setValue(true);

        if (task.getTitle().isEmpty()) {
            errorMessage.setValue("Le titre est obligatoire");
            isLoading.setValue(false);
            return;
        }

        taskRepository.update(task);
        taskSaved.setValue(true);
        isLoading.setValue(false);
    }

    public void deleteTask(Task task) {
        isLoading.setValue(true);
        taskRepository.delete(task);
        taskDeleted.setValue(true);
        isLoading.setValue(false);
    }

    public void toggleTaskCompletion(Task task) {
        task.setCompleted(!task.isCompleted());
        taskRepository.update(task);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getTaskSaved() {
        return taskSaved;
    }

    public LiveData<Boolean> getTaskDeleted() {
        return taskDeleted;
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void resetTaskSaved() {
        taskSaved.setValue(false);
    }

    public void resetTaskDeleted() {
        taskDeleted.setValue(false);
    }
}

