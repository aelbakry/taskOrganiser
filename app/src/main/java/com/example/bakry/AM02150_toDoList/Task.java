package com.example.bakry.AM02150_toDoList;

import java.util.ArrayList;
import java.util.Calendar;

public class Task {

    private String id = null;
    private String overview = null;
    private Calendar dueDate = null;
    private int numberOfSteps = 0;
    private boolean isComplete = false;
    private ArrayList<Step> steps = null;
    private double lat = 0;

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    private double lng = 0;

    public void setId(String id) {
        this.id = id;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }


    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }
}
