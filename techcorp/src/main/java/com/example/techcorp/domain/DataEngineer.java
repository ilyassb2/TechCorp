package com.example.techcorp.domain;

public class DataEngineer extends Employee {

    public DataEngineer(String name, int skill, double salary) {
        super(name, skill, salary);
    }

    @Override
    public int work() {
        return skill + 2;
    }
}