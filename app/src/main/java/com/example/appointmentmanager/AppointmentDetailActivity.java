package com.example.appointmentmanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentDetailActivity extends Activity {
    private DatabaseHelper db;
    private int appointmentId;
    private Switch swReminder;
    private TextView tvTitle, tvLocation, tvDateTime, tvReminderStatus;
    private boolean loading = false;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        db = new DatabaseHelper(this);
        appointmentId = getIntent().getIntExtra("id", -1);
        tvTitle = findViewById(R.id.tvTitle);
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvReminderStatus = findViewById(R.id.tvReminderStatus);
        swReminder = findViewById(R.id.swReminder);
        swReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (loading) return;
            db.updateReminder(appointmentId, isChecked);
            updateStatus(isChecked);
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Xóa lịch hẹn")
                    .setMessage("Bạn có chắc chắn muốn xóa lịch hẹn này?")
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        db.deleteAppointment(appointmentId);
                        Toast.makeText(this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show();
                        finish();
                    }).show();
        });
        loadAppointment();
    }

    private void loadAppointment() {
        Appointment a = db.getAppointmentById(appointmentId);
        if (a == null) { Toast.makeText(this, "Không tìm thấy lịch hẹn", Toast.LENGTH_SHORT).show(); finish(); return; }
        tvTitle.setText(a.getTitle());
        tvLocation.setText("Địa điểm: " + a.getLocation());
        tvDateTime.setText("Thời gian: " + formatDate(a.getDate()) + " · " + a.getTime());
        loading = true;
        swReminder.setChecked(a.isReminder());
        updateStatus(a.isReminder());
        loading = false;
    }

    private void updateStatus(boolean on) { tvReminderStatus.setText(on ? "Đang bật" : "Đang tắt"); }

    private String formatDate(String value) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value);
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d);
        } catch (ParseException e) { return value; }
    }

    @Override protected void onDestroy() { super.onDestroy(); if (db != null) db.close(); }
}
