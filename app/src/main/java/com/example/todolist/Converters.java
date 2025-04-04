package com.example.todolist;

import androidx.room.TypeConverter;

import com.example.todolist.Priority;

public class Converters {
    @TypeConverter
    public static String fromPriority(Priority priority) {
        return priority == null ? null : priority.name();
    }

    @TypeConverter
    public static Priority toPriority(String value) {
        return value == null ? null : Priority.valueOf(value);
    }
}
