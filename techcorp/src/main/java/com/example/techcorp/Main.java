package com.example.techcorp;

import com.example.techcorp.domain.*;
import com.example.techcorp.engine.GameEngine;
import com.example.techcorp.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {

        Company company = new Company("TechCorp");

        Employee a = new Employee("Anna", 5);
        Employee b = new Employee("Piotr", 4);

        company.hire(a);
        company.hire(b);

        Project p = new Project("App", 20);
        p.addEmployee(a);
        p.addEmployee(b);

        company.startProject(p);

        ConsoleUI ui = new ConsoleUI();
        GameEngine engine = new GameEngine(company, ui);

        engine.start();
    }
}