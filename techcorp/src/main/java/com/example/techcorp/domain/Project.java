package com.example.techcorp.domain;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String name;
    private int requiredWork;
    private int progress;
    private List<Employee> team = new ArrayList<>();

    public Project(String name, int requiredWork) {
        this.name = name;
        this.requiredWork = requiredWork;
    }

    public void addEmployee(Employee e) {
        team.add(e);
    }

    public void workOneTurn() {
    int totalBoost = 0;

    for (Employee e : team) {
        progress += e.work();

        // Check for ProjectManager
        if (e instanceof ProjectManager) {
            ProjectManager pm = (ProjectManager) e;
            totalBoost += pm.boost();
        }
    }

    // Apply manager boost
    progress += totalBoost;

    if (progress > requiredWork) {
        progress = requiredWork;
    }
}

    public boolean isFinished() {
        return progress >= requiredWork;
    }

    public String getName() { return name; }
    public int getProgress() { return progress; }
    public int getRequiredWork() { return requiredWork; }
}