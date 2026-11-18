package com.example.appointmentmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private DatabaseHelper db;
    private List<Appointment> appointments;
    private TextView tvStats, tvEmpty;
    private ListView listView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        tvStats = findViewById(R.id.tvStats);
        tvEmpty = findViewById(R.id.tvEmpty);
        listView = findViewById(R.id.listAppointments);
        findViewById(R.id.btnAdd).setOnClickListener(v -> startActivity(new Intent(this, AddAppointmentActivity.class)));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, AppointmentDetailActivity.class);
            intent.putExtra("id", appointments.get(position).getId());
            startActivity(intent);
        });
    }

    @Override protected void onResume() { super.onResume(); if (db != null) refreshData(); }

    private void refreshData() {
        appointments = db.getAllAppointments();
        tvStats.setText("Tổng lịch hẹn: " + db.countAll() + "   •   Đang nhắc: " + db.countReminders());
        tvEmpty.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
        listView.setVisibility(appointments.isEmpty() ? View.GONE : View.VISIBLE);
        listView.setAdapter(new AppointmentAdapter());
    }

    private String formatDate(String value) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value);
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d);
        } catch (ParseException e) { return value; }
    }

    private class AppointmentAdapter extends BaseAdapter {
        @Override public int getCount() { return appointments.size(); }
        @Override public Object getItem(int position) { return appointments.get(position); }
        @Override public long getItemId(int position) { return appointments.get(position).getId(); }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) row = LayoutInflater.from(MainActivity.this).inflate(R.layout.row_appointment, parent, false);
            Appointment a = appointments.get(position);
            ((TextView) row.findViewById(R.id.rowTitle)).setText(a.getTitle());
            ((TextView) row.findViewById(R.id.rowLocation)).setText("📍 " + a.getLocation());
            ((TextView) row.findViewById(R.id.rowDateTime)).setText("🕒 " + formatDate(a.getDate()) + " · " + a.getTime());
            ((TextView) row.findViewById(R.id.rowReminder)).setText(a.isReminder() ? "● Có nhắc" : "○ Không nhắc");
            return row;
        }
    }

    @Override protected void onDestroy() { super.onDestroy(); if (db != null) db.close(); }
}
