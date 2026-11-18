package com.example.appointmentmanager;

public class Appointment {
    private int id;
    private String title, location, date, time;
    private boolean reminder;

    public Appointment(int id, String title, String location, String date, String time, boolean reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public Appointment(String title, String location, String date, String time, boolean reminder) {
        this(0, title, location, date, time, reminder);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public boolean isReminder() { return reminder; }
}
