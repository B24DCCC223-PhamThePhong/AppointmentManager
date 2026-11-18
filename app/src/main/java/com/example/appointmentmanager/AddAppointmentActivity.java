package com.example.appointmentmanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends Activity {
    private DatabaseHelper db;
    private EditText etTitle, etLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Switch swReminder;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        db = new DatabaseHelper(this);
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        swReminder = findViewById(R.id.swReminder);
        timePicker.setIs24HourView(true);
        Calendar now = Calendar.getInstance();
        datePicker.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), null);
        timePicker.setHour(now.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute((now.get(Calendar.MINUTE) + 5) % 60);
        findViewById(R.id.btnSave).setOnClickListener(v -> saveAppointment());
    }

    private void saveAppointment() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        if (title.isEmpty()) { etTitle.setError("Không được để trống tên lịch hẹn"); etTitle.requestFocus(); return; }
        if (location.isEmpty()) { etLocation.setError("Không được để trống địa điểm"); etLocation.requestFocus(); return; }
        Calendar chosen = Calendar.getInstance();
        chosen.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getHour(), timePicker.getMinute(), 0);
        chosen.set(Calendar.MILLISECOND, 0);
        if (!chosen.after(Calendar.getInstance())) {
            Toast.makeText(this, "Thời điểm hẹn phải sau thời điểm hiện tại", Toast.LENGTH_LONG).show();
            return;
        }
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(chosen.getTime());
        String time = new SimpleDateFormat("HH:mm", Locale.US).format(chosen.getTime());
        long id = db.insertAppointment(new Appointment(title, location, date, time, swReminder.isChecked()));
        if (id != -1) {
            Toast.makeText(this, "Đã lưu lịch hẹn", Toast.LENGTH_SHORT).show();
            finish();
        } else Toast.makeText(this, "Không thể lưu lịch hẹn", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onDestroy() { super.onDestroy(); if (db != null) db.close(); }
}
