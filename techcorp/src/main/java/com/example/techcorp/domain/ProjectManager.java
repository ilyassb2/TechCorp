package com.example.techcorp.domain;

public class ProjectManager extends Employee {

    public ProjectManager(String name, int skill, double salary) {
        super(name, skill, salary);
    }

    @Override
    public int work() {
        return skill;
    }

    public int boost() {
        return 2;
    }
}