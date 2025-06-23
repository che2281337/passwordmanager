package com.example.passwordmanager;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordDetailActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_detail);

        TextView textViewService = findViewById(R.id.textViewService);
        TextView textViewLogin = findViewById(R.id.textViewLogin);
        TextView textViewPassword = findViewById(R.id.textViewPassword);
        TextView textViewNotes = findViewById(R.id.textViewNotes);
        CheckBox checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);

        databaseHelper = new DatabaseHelper(this);
        int passwordId = getIntent().getIntExtra("password_id", -1);
        PasswordEntry entry = databaseHelper.getPasswordById(passwordId);

        textViewService.setText(entry.getServiceName());
        textViewLogin.setText(entry.getLogin());
        textViewPassword.setText(entry.getPassword());
        textViewNotes.setText(entry.getNotes());

        checkBoxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textViewPassword.setTransformationMethod(null);
            } else {
                textViewPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
    }
}