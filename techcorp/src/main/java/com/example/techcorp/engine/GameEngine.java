package com.example.techcorp.engine;

import com.example.techcorp.domain.Company;
import com.example.techcorp.domain.Project;
import com.example.techcorp.ui.ConsoleUI;

public class GameEngine {

    private Company company;
    private ConsoleUI ui;
    private boolean running = true;
    private int turn = 1;

    public GameEngine(Company company, ConsoleUI ui) {
        this.company = company;
        this.ui = ui;
    }

    public void start() {
        while (running) {
            ui.showTurnHeader(turn);
            ui.showCompanyStatus(company);
            ui.showMainMenu();

            int choice = ui.readMenuChoice();
            handleChoice(choice);

            turn++;
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> ui.showCompanyStatus(company);
            case 2 -> workOnProjects();
            case 3 -> running = false;
            default -> ui.showMessage("Invalid option");
        }
    }

    private void workOnProjects() {
        for (Project p : company.getProjects()) {
            p.workOneTurn();
        }
        ui.showMessage("Worked one turn");
    }
}