package com.example.purduecircle307;

public class Messages {
    public String date, from, message, time, type;

    public Messages() { } // empty constructor

    public Messages(String date, String from, String message, String time, String type) {
        this.date = date;
        this.from = from;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }
}
