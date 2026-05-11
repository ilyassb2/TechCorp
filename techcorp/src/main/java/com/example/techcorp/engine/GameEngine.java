package com.example.techcorp.engine;

import com.example.techcorp.domain.Company;
import com.example.techcorp.domain.Project;
import com.example.techcorp.ui.ConsoleUI;
import com.example.techcorp.events.*;
import java.util.Random;
import com.example.techcorp.domain.*;

public class GameEngine {

    private Company company;
    private ConsoleUI ui;
    private boolean running = true;
    private int turn = 1;
    private Random random = new Random();

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
            company.paySalaries();
            triggerRandomEvent();
            checkGameOver();

            turn++;
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
    case 1:
        ui.showCompanyStatus(company);
        break;
    case 2:
        workOnProjects();
        break;
    case 3:
        running = false;
        break;
    default:
        ui.showMessage("Invalid option");
}
    }

    private void workOnProjects() {
        for (Project p : company.getProjects()) {
            p.workOneTurn();
        }
        ui.showMessage("Worked one turn");
    }
    private boolean allProjectsFinished() {
    if (company.getProjects().isEmpty()) {
        return false;
    }

    for (Project p : company.getProjects()) {
        if (!p.isFinished()) {
            return false;
        }
    }

    return true;
}
private void checkGameOver() {

    // Lose condition
    if (company.getCash() < 0) {
        ui.showMessage("You went bankrupt! Game over.");
        running = false;
        return;
    }

    // Win condition
    if (allProjectsFinished()) {
        ui.showMessage("All projects completed! You win!");
        running = false;
    }
}
private void triggerRandomEvent() {

    int chance = random.nextInt(100);

    // 30% chance of event
    if (chance < 30) {

        GameEvent event;

        if (random.nextBoolean()) {
            event = new CrisisEvent();
        } else {
            event = new BonusEvent();
        }

        ui.showMessage("EVENT: " + event.getName());

        if (event instanceof CrisisEvent && crisisPrevented()) {

    ui.showMessage("SOC Analyst prevented the crisis!");

} else {

    event.apply(company);
}
    }
}
private boolean crisisPrevented() {

    for (Employee e : company.getEmployees()) {

        if (e instanceof SOCAnalyst) {

            SOCAnalyst soc = (SOCAnalyst) e;

            if (soc.preventIncident()) {
                return true;
            }
        }
    }

    return false;
}
}