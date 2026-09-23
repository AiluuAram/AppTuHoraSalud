package com.example.apptuhorasalud.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

public class FormUtils {
    public static void showError(Context context, String message, EditText field) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        field.requestFocus();
    }
    public static void showSuccess(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
