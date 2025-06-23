package com.example.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "password_manager.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SECRET_KEY = "dGhpc2lzYXNlY3VyZWtleTEyMzQ1Njc4OTAxMjM0NQ==";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE passwords (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, service_name TEXT, login TEXT, password TEXT, notes TEXT, FOREIGN KEY(user_id) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS passwords");
        onCreate(db);
    }

    public boolean addUser(String login, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("login", login);
        values.put("password", hashPassword(password));
        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE login = ?", new String[]{login});
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(0);
            cursor.close();
            db.close();
            return storedPassword.equals(hashPassword(password));
        }
        cursor.close();
        db.close();
        return false;
    }

    public boolean checkUserExists(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT login FROM users WHERE login = ?", new String[]{login});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public int getUserId(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE login = ?", new String[]{login});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            db.close();
            return id;
        }
        cursor.close();
        db.close();
        return -1;
    }

    public boolean addPassword(int userId, String serviceName, String login, String password, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("service_name", serviceName);
        values.put("login", login);
        values.put("password", encryptPassword(password));
        values.put("notes", notes);
        long result = db.insert("passwords", null, values);
        db.close();
        return result != -1;
    }

    public List<PasswordEntry> getPasswords(int userId) {
        List<PasswordEntry> passwords = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, service_name, login, password, notes FROM passwords WHERE user_id = ?", new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String serviceName = cursor.getString(1);
            String login = cursor.getString(2);
            String password = decryptPassword(cursor.getString(3));
            String notes = cursor.getString(4);
            passwords.add(new PasswordEntry(id, serviceName, login, password, notes));
        }
        cursor.close();
        db.close();
        return passwords;
    }

    public PasswordEntry getPasswordById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, service_name, login, password, notes FROM passwords WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String serviceName = cursor.getString(1);
            String login = cursor.getString(2);
            String password = decryptPassword(cursor.getString(3));
            String notes = cursor.getString(4);
            cursor.close();
            db.close();
            return new PasswordEntry(id, serviceName, login, password, notes);
        }
        cursor.close();
        db.close();
        return null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    private String encryptPassword(String password) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(password.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    private String decryptPassword(String encryptedPassword) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.decode(encryptedPassword, Base64.DEFAULT);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return encryptedPassword;
        }
    }
}