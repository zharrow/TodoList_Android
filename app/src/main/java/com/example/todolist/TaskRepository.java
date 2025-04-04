package com.example.todolist;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {
    private final TaskDao taskDao;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
    }

    public long insert(Task task) {
        return new InsertTaskAsyncTask(taskDao).doInBackground(task);
    }

    public void update(Task task) {
        new UpdateTaskAsyncTask(taskDao).execute(task);
    }

    public void delete(Task task) {
        new DeleteTaskAsyncTask(taskDao).execute(task);
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
        new DeleteAllTasksByUserIdAsyncTask(taskDao).execute(userId);
    }

    private static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Long> {
        private TaskDao taskDao;

        private InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Long doInBackground(Task... tasks) {
            return taskDao.insert(tasks[0]);
        }
    }

    private static class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.update(tasks[0]);
            return null;
        }
    }

    private static class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }

    private static class DeleteAllTasksByUserIdAsyncTask extends AsyncTask<Long, Void, Void> {
        private TaskDao taskDao;

        private DeleteAllTasksByUserIdAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Long... userIds) {
            taskDao.deleteAllTasksByUserId(userIds[0]);
            return null;
        }
    }
}
