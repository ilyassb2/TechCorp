package com.example.techcorp.domain;

import java.util.ArrayList;
import java.util.List;

public class Company {
    private String name;
    private double cash;
    private List<Project> projects = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();

    public Company(String name) {
        this.name = name;
    }
    public Company(String name, double cash) {
    this.name = name;
    this.cash = cash;
}

    public void hire(Employee e) {
        employees.add(e);
    }

    public void startProject(Project p) {
        projects.add(p);
    }
    public void paySalaries() {
    double total = 0;

    for (Employee e : employees) {
        total += e.getSalary();
    }

    cash -= total;

    System.out.println("Paid salaries: -" + total);
    System.out.println("Remaining cash: " + cash);
}
    public List<Project> getProjects() {
        return projects;
    }
    public double getCash() {
    return cash;
}

    public List<Employee> getEmployees() {
        return employees;
    }

    public String getName() {
        return name;
    }
}
