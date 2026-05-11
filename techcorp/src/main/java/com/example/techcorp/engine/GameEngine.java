package com.example.techcorp.engine;

import com.example.techcorp.domain.*;
import com.example.techcorp.ui.ConsoleUI;
import com.example.techcorp.events.*;
import com.example.techcorp.exceptions.*;

import java.util.Random;

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
        }
    }

    private void handleChoice(int choice) {

        switch (choice) {

            case 1:

                ui.showCompanyStatus(company);

                break;

            case 2:

                workOnProjects();

                company.paySalaries();

                triggerRandomEvent();

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

    // Validate role BEFORE asking for name
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

    // Automatically add employee to first project
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

        if (company.getCash() < 0) {

            ui.showMessage(
                "You went bankrupt! Game over."
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

        // 30% chance
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

    // Remove from company
    company.getEmployees().remove(employee);

    // Remove from all projects
    for (Project p : company.getProjects()) {

        p.getTeam().remove(employee);
    }

    ui.showMessage(
        employee.getName() +
        " was fired."
    );
}
}