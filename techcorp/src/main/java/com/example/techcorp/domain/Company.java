package com.example.techcorp.domain;

import java.util.ArrayList;
import java.util.List;

public class Company {
    private String name;
    private List<Project> projects = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();

    public Company(String name) {
        this.name = name;
    }

    public void hire(Employee e) {
        employees.add(e);
    }

    public void startProject(Project p) {
        projects.add(p);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public String getName() {
        return name;
    }
}