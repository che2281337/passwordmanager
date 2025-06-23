package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class PasswordListActivity extends AppCompatActivity {
    private ListView listViewPasswords;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> adapter;
    private List<PasswordEntry> passwordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        listViewPasswords = findViewById(R.id.listViewPasswords);
        Button buttonAddPassword = findViewById(R.id.buttonAddPassword);
        TextView textViewEmpty = findViewById(R.id.textViewEmpty);

        int userId = getIntent().getIntExtra("user_id", -1);
        databaseHelper = new DatabaseHelper(this);

        passwordList = databaseHelper.getPasswords(userId);
        if (passwordList.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            listViewPasswords.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            listViewPasswords.setVisibility(View.VISIBLE);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, passwordListToString());
            listViewPasswords.setAdapter(adapter);
        }

        listViewPasswords.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(PasswordListActivity.this, PasswordDetailActivity.class);
            intent.putExtra("password_id", passwordList.get(position).getId());
            startActivity(intent);
        });

        buttonAddPassword.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordListActivity.this, AddPasswordActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = getIntent().getIntExtra("user_id", -1);
        passwordList = databaseHelper.getPasswords(userId);
        if (passwordList.isEmpty()) {
            findViewById(R.id.textViewEmpty).setVisibility(View.VISIBLE);
            listViewPasswords.setVisibility(View.GONE);
        } else {
            findViewById(R.id.textViewEmpty).setVisibility(View.GONE);
            listViewPasswords.setVisibility(View.VISIBLE);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, passwordListToString());
            listViewPasswords.setAdapter(adapter);
        }
    }

    private String[] passwordListToString() {
        String[] result = new String[passwordList.size()];
        for (int i = 0; i < passwordList.size(); i++) {
            result[i] = passwordList.get(i).getServiceName() + " (" + passwordList.get(i).getLogin() + ")";
        }
        return result;
    }
}