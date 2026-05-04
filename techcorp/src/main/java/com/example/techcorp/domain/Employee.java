package com.example.techcorp.domain;

public class Employee {
    private String name;
    private int skill;

    public Employee(String name, int skill) {
        this.name = name;
        this.skill = skill;
    }

    public int work() {
        return skill;
    }

    public String getName() {
        return name;
    }
}