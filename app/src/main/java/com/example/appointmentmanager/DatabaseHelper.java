package com.example.appointmentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "appointments.db";
    public DatabaseHelper(Context context) { super(context, DB_NAME, null, 1); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
        db.execSQL("CREATE TABLE appointments (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, location TEXT NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, reminder INTEGER NOT NULL)");
        ContentValues user = new ContentValues();
        user.put("username", "admin"); user.put("password", "123456");
        db.insert("users", null, user);

        addSample(db, "Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", 1);
        addSample(db, "Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", 1);
        addSample(db, "Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", 0);
    }

    private void addSample(SQLiteDatabase db, String title, String location, String date, String time, int reminder) {
        ContentValues v = new ContentValues();
        v.put("title", title); v.put("location", location); v.put("date", date);
        v.put("time", time); v.put("reminder", reminder);
        db.insert("appointments", null, v);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM users WHERE username = ? AND password = ?", new String[]{username, password});
        boolean ok = c.moveToFirst(); c.close(); return ok;
    }

    public long insertUser(String username, String password) {
        ContentValues v = new ContentValues(); v.put("username", username); v.put("password", password);
        return getWritableDatabase().insert("users", null, v);
    }

    public long insertAppointment(Appointment a) {
        ContentValues v = new ContentValues();
        v.put("title", a.getTitle()); v.put("location", a.getLocation());
        v.put("date", a.getDate()); v.put("time", a.getTime()); v.put("reminder", a.isReminder() ? 1 : 0);
        return getWritableDatabase().insert("appointments", null, v);
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> result = new ArrayList<>();
        Cursor c = getReadableDatabase().query("appointments", null, null, null, null, null, "date ASC, time ASC");
        try {
            while (c.moveToNext()) result.add(fromCursor(c));
        } finally { c.close(); }
        return result;
    }

    public Appointment getAppointmentById(int id) {
        Cursor c = getReadableDatabase().query("appointments", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        try { return c.moveToFirst() ? fromCursor(c) : null; } finally { c.close(); }
    }

    private Appointment fromCursor(Cursor c) {
        return new Appointment(c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("title")),
                c.getString(c.getColumnIndexOrThrow("location")),
                c.getString(c.getColumnIndexOrThrow("date")),
                c.getString(c.getColumnIndexOrThrow("time")),
                c.getInt(c.getColumnIndexOrThrow("reminder")) == 1);
    }

    public int updateReminder(int id, boolean on) {
        ContentValues v = new ContentValues(); v.put("reminder", on ? 1 : 0);
        return getWritableDatabase().update("appointments", v, "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteAppointment(int id) {
        return getWritableDatabase().delete("appointments", "id = ?", new String[]{String.valueOf(id)});
    }

    public int countAll() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM appointments", null);
        try { c.moveToFirst(); return c.getInt(0); } finally { c.close(); }
    }

    public int countReminders() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM appointments WHERE reminder = 1", null);
        try { c.moveToFirst(); return c.getInt(0); } finally { c.close(); }
    }
}
