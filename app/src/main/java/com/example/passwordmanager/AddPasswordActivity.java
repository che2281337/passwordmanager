package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddPasswordActivity extends AppCompatActivity {
    private EditText editTextService, editTextLogin, editTextPassword, editTextNotes;
    private CheckBox checkBoxShowPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        editTextService = findViewById(R.id.editTextService);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNotes = findViewById(R.id.editTextNotes);
        checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);
        Button buttonSave = findViewById(R.id.buttonSave);

        databaseHelper = new DatabaseHelper(this);

        checkBoxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextPassword.setTransformationMethod(null);
            } else {
                editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
            editTextPassword.setSelection(editTextPassword.getText().length());
        });

        buttonSave.setOnClickListener(v -> {
            String service = editTextService.getText().toString().trim();
            String login = editTextLogin.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String notes = editTextNotes.getText().toString().trim();
            int userId = getIntent().getIntExtra("user_id", -1);

            if (service.isEmpty() || login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.addPassword(userId, service, login, password, notes)) {
                Toast.makeText(this, "Пароль сохранён", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddPasswordActivity.this, PasswordListActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show();
            }
        });
    }
}