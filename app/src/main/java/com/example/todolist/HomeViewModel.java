package com.example.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;
    private final long currentUserId;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        currentUserId = SharedPreferencesManager.getUserId(application);
    }

    public LiveData<List<Task>> getTodayTasks() {
        String today = DateUtils.getTodayDateString();
        return taskRepository.getTasksInDateRange(today, today, currentUserId);
    }

    public LiveData<List<Task>> getUpcomingTasks() {
        String tomorrow = DateUtils.getTomorrowDateString();
        String nextWeek = DateUtils.getNextWeekDateString();
        return taskRepository.getTasksInDateRange(tomorrow, nextWeek, currentUserId);
    }

    public LiveData<List<Task>> getOverdueTasks() {
        String yesterday = DateUtils.getYesterdayDateString();
        String pastMonth = DateUtils.getPastMonthDateString();
        return Transformations.switchMap(
                taskRepository.getTasksInDateRange(pastMonth, yesterday, currentUserId),
                tasks -> {
                    MutableLiveData<List<Task>> pendingOverdueTasks = new MutableLiveData<>();
                    // Filter to get only uncompleted tasks
                    List<Task> filteredTasks = tasks.stream()
                            .filter(task -> !task.isCompleted())
                            .toList();
                    pendingOverdueTasks.setValue(filteredTasks);
                    return pendingOverdueTasks;
                }
        );
    }

    public LiveData<Integer> getTotalTasksCount() {
        MutableLiveData<Integer> totalTasksCount = new MutableLiveData<>(0);
        taskRepository.getAllTasksByUserId(currentUserId).observeForever(tasks -> {
            if (tasks != null) {
                totalTasksCount.setValue(tasks.size());
            }
        });
        return totalTasksCount;
    }

    public LiveData<Integer> getCompletedTasksCount() {
        MutableLiveData<Integer> completedTasksCount = new MutableLiveData<>(0);
        taskRepository.getTasksByCompletionStatus(true, currentUserId).observeForever(tasks -> {
            if (tasks != null) {
                completedTasksCount.setValue(tasks.size());
            }
        });
        return completedTasksCount;
    }

    public LiveData<Integer> getPendingTasksCount() {
        MutableLiveData<Integer> pendingTasksCount = new MutableLiveData<>(0);
        taskRepository.getTasksByCompletionStatus(false, currentUserId).observeForever(tasks -> {
            if (tasks != null) {
                pendingTasksCount.setValue(tasks.size());
            }
        });
        return pendingTasksCount;
    }
}
