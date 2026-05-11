package com.example.techcorp;

import com.example.techcorp.domain.*;
import com.example.techcorp.engine.GameEngine;
import com.example.techcorp.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {

        Company company = new Company("TechCorp", 55000);

        Employee a = new DataEngineer("Anna", 5, 7000);
        Employee b = new MLOpsEngineer("Leo", 6, 8000);
        Employee c = new ProjectManager("Sara", 4, 9000);
        Employee d = new SOCAnalyst("Nick", 5, 6500);

        company.hire(a);
        company.hire(b);
        company.hire(d);

        Project p = new Project("Cybersecurity Infrastructure", 120);
        p.addEmployee(a);
        p.addEmployee(b);
        p.addEmployee(d);

        company.startProject(p);

        ConsoleUI ui = new ConsoleUI();
        GameEngine engine = new GameEngine(company, ui);

        engine.start();
    }
}