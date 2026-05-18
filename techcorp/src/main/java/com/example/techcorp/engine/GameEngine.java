package com.example.techcorp.engine;

import com.example.techcorp.domain.*;
import com.example.techcorp.ui.ConsoleUI;
import com.example.techcorp.events.*;
import com.example.techcorp.exceptions.*;

import java.util.Random;

public class GameEngine {

    private Difficulty difficulty = Difficulty.MEDIUM;

    private Company company;

    private Company opponentCompany;

    private ConsoleUI ui;

    private boolean running = true;

    private int turn = 1;

    private Random random = new Random();

    public GameEngine(
            Company company,
            Company opponentCompany,
            ConsoleUI ui
    ) {

        this.company = company;

        this.opponentCompany = opponentCompany;

        this.ui = ui;
    }

    public void setDifficulty(Difficulty difficulty) {

        this.difficulty = difficulty;
    }

    public void start() {

        ui.showMessage("Opponent difficulty: " + difficulty);

        while (running) {

            ui.showTurnHeader(turn);

            ui.showCompanyStatus(company);

            ui.showOpponentStatus(opponentCompany);

            ui.showMainMenu();

            int choice = ui.readMenuChoice();

            handleChoice(choice);
        }
    }

    private void handleChoice(int choice) {

        switch (choice) {

            case 1:

                ui.showCompanyStatus(company);

                ui.showOpponentStatus(opponentCompany);

                break;

            case 2:

                workOnProjects();

                opponentTurn();

                company.paySalaries();

                triggerRandomEvent();

                triggerOpponentRandomEvent();

                ui.showTurnSummary(company, opponentCompany);

                checkGameOver();

                if (running) {
                    turn++;
                }

                break;

            case 3:

                try {

                    hireEmployee();

                } catch (InsufficientFundsException e) {

                    ui.showMessage(
                        "ERROR: " + e.getMessage()
                    );

                } catch (InvalidRoleException e) {

                    ui.showMessage(
                        "ERROR: " + e.getMessage()
                    );
                }

                break;

            case 4:

                try {

                    fireEmployee();

                } catch (EmployeeNotFoundException e) {

                    ui.showMessage(
                        "ERROR: " + e.getMessage()
                    );
                }

                break;

            case 5:

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

    private void opponentTurn() {

        for (Project p : opponentCompany.getProjects()) {

            p.workOneTurn();
        }

        opponentCompany.paySalaries();

        opponentAutoHire();

        ui.showMessage(
            opponentCompany.getName() +
            " completed one business turn."
        );
    }

    private void opponentAutoHire() {

        if (opponentCompany.getProjects().isEmpty()) {
            return;
        }

        Project mainProject = opponentCompany.getProjects().get(0);

        double progressRatio =
            (double) mainProject.getProgress() /
            mainProject.getRequiredWork();

        double cashThreshold;
        int maxTeamSize;

        switch (difficulty) {

            case EASY:
                cashThreshold = 45000;
                maxTeamSize = 2;
                break;

            case HARD:
                cashThreshold = 15000;
                maxTeamSize = 6;
                break;

            default:
                cashThreshold = 25000;
                maxTeamSize = 5;
                break;
        }

        boolean hasEnoughCash = opponentCompany.getCash() > cashThreshold;
        boolean projectIsBehind = progressRatio < 0.60;
        int teamSize = mainProject.getTeam().size();

        if (hasEnoughCash && projectIsBehind && teamSize < maxTeamSize) {

            Employee employee;

            if (progressRatio < 0.35) {

                employee = new MLOpsEngineer(
                    "Opponent MLOps Engineer",
                    6,
                    4500
                );

            } else if (progressRatio < 0.55) {

                employee = new ProjectManager(
                    "Opponent Project Manager",
                    4,
                    5000
                );

            } else {

                employee = new DataEngineer(
                    "Opponent Data Engineer",
                    5,
                    3500
                );
            }

            opponentCompany.hire(employee);

            mainProject.addEmployee(employee);

            ui.showMessage(
                opponentCompany.getName() +
                " strategically hired a new " +
                employee.getClass().getSimpleName()
            );
        }
    }

    private void hireEmployee()
            throws InsufficientFundsException,
                   InvalidRoleException {

        double minimumCashRequired = 10000;

        if (company.getCash() < minimumCashRequired) {

            throw new InsufficientFundsException(
                "Not enough cash to hire new employee!"
            );
        }

        ui.showHireMenu();

        int roleChoice = ui.readMenuChoice();

        if (roleChoice < 1 || roleChoice > 4) {

            throw new InvalidRoleException(
                "Invalid employee role selected!"
            );
        }

        String name = ui.readString(
            "Enter employee name: "
        );

        Employee employee = null;

        switch (roleChoice) {

            case 1:

                employee = new DataEngineer(
                    name,
                    5,
                    3500
                );

                break;

            case 2:

                employee = new MLOpsEngineer(
                    name,
                    6,
                    4500
                );

                break;

            case 3:

                employee = new SOCAnalyst(
                    name,
                    5,
                    3000
                );

                break;

            case 4:

                employee = new ProjectManager(
                    name,
                    4,
                    5000
                );

                break;
        }

        company.hire(employee);

        if (!company.getProjects().isEmpty()) {

            company.getProjects()
                    .get(0)
                    .addEmployee(employee);
        }

        ui.showMessage(
            employee.getClass().getSimpleName() +
            " hired successfully!"
        );
    }

    private void fireEmployee()
            throws EmployeeNotFoundException {

        if (company.getEmployees().isEmpty()) {

            throw new EmployeeNotFoundException(
                "No employees to fire!"
            );
        }

        ui.showEmployeeList(company);

        int employeeIndex = ui.readMenuChoice();

        if (employeeIndex < 1 ||
                employeeIndex > company.getEmployees().size()) {

            throw new EmployeeNotFoundException(
                "Invalid employee selected!"
            );
        }

        Employee employee =
                company.getEmployees().get(employeeIndex - 1);

        company.getEmployees().remove(employee);

        for (Project p : company.getProjects()) {

            p.getTeam().remove(employee);
        }

        ui.showMessage(
            employee.getName() +
            " was fired."
        );
    }

    private boolean allProjectsFinished() {

        for (Project p : company.getProjects()) {

            if (!p.isFinished()) {
                return false;
            }
        }

        return true;
    }

    private boolean opponentFinished() {

        for (Project p : opponentCompany.getProjects()) {

            if (!p.isFinished()) {
                return false;
            }
        }

        return true;
    }

    private void checkGameOver() {

        if (company.getCash() < 0) {

            ui.showMessage(
                "You went bankrupt! Game over."
            );

            running = false;

            return;
        }

        if (opponentCompany.getCash() < 0) {

            ui.showMessage(
                opponentCompany.getName() +
                " went bankrupt. You win!"
            );

            running = false;

            return;
        }

        if (opponentFinished()) {

            ui.showMessage(
                opponentCompany.getName() +
                " finished first. You lose!"
            );

            running = false;

            return;
        }

        if (allProjectsFinished()) {

            ui.showMessage(
                "All projects completed! You win!"
            );

            running = false;
        }
    }

    private void triggerRandomEvent() {

        int chance = random.nextInt(100);

        if (chance < 30) {

            GameEvent event;

            if (random.nextBoolean()) {

                event = new CrisisEvent();

            } else {

                event = new BonusEvent();
            }

            ui.showMessage(
                "EVENT: " + event.getName()
            );

            if (event instanceof CrisisEvent
                    && crisisPrevented()) {

                ui.showMessage(
                    "SOC Analyst prevented the crisis!"
                );

            } else {

                event.apply(company);
            }
        }
    }

    private void triggerOpponentRandomEvent() {

        int eventChance;

        switch (difficulty) {

            case EASY:
                eventChance = 5;
                break;

            case HARD:
                eventChance = 35;
                break;

            default:
                eventChance = 25;
                break;
        }

        int chance = random.nextInt(100);

        if (chance < eventChance) {

            GameEvent event;

            if (random.nextBoolean()) {

                event = new CrisisEvent();

            } else {

                event = new BonusEvent();
            }

            ui.showMessage(
                "OPPONENT EVENT: " + event.getName()
            );

            event.apply(opponentCompany);
        }
    }

    private boolean crisisPrevented() {

        for (Employee e : company.getEmployees()) {

            if (e instanceof SOCAnalyst) {

                SOCAnalyst soc =
                        (SOCAnalyst) e;

                if (soc.preventIncident()) {
                    return true;
                }
            }
        }

        return false;
    }
}