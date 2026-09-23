package com.example.apptuhorasalud.utils;

import android.content.Context;
import androidx.room.Room;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;

public class DatabaseHelper {
    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "usuarios-db")
                .fallbackToDestructiveMigration()
                .build();
    }
}
