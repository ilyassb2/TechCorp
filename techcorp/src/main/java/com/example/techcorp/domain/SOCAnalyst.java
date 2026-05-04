package com.example.techcorp.domain;

public class SOCAnalyst extends Employee {

    public SOCAnalyst(String name, int skill, double salary) {
        super(name, skill, salary);
    }

    @Override
    public int work() {
        return skill;
    }

    public boolean preventIncident() {
        return Math.random() < 0.3; // 30% chance
    }
}