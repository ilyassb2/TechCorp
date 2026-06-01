package com.example.techcorp.web;

import com.example.techcorp.domain.*;
import com.example.techcorp.events.*;
import com.example.techcorp.exceptions.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class GameController {

    private Company company;
    private Project project;

    private Company opponentCompany;
    private Project opponentProject;

    private Difficulty difficulty = Difficulty.MEDIUM;

    private boolean gameStarted = false;
    private boolean gameOver = false;

    private String message = "";
    private String gameResult = "";

    private List<String> eventLog = new ArrayList<>();

    private Random random = new Random();

    private int opponentFundingUsed = 0;

    public GameController() {
    }

    @GetMapping("/")
    public String home(Model model) {
        addGameData(model);
        return "index";
    }

    @PostMapping("/start-game")
    public String startGame(
            @RequestParam String selectedDifficulty,
            Model model
    ) {
        difficulty = Difficulty.valueOf(selectedDifficulty);

        setupGame();

        gameStarted = true;
        gameOver = false;
        message = "Game started on " + difficulty + " difficulty.";
        gameResult = "";
        opponentFundingUsed = 0;

        eventLog.clear();
        addEvent("Game started on " + difficulty + " difficulty.");

        addGameData(model);
        return "index";
    }

    private void setupGame() {

        company = new Company(
            "TechCorp",
            getPlayerStartingCash()
        );

        Employee a = new DataEngineer("Anna", 5, 3500);
        Employee b = new MLOpsEngineer("Leo", 6, 4500);

        company.hire(a);
        company.hire(b);

        project = new Project(
            "Cybersecurity Infrastructure",
            getPlayerRequiredWork()
        );

        project.addEmployee(a);
        project.addEmployee(b);

        company.startProject(project);

        opponentCompany = new Company(
            "EVCorp",
            getOpponentStartingCash()
        );

        Employee rivalA = new DataEngineer("Rival Data Engineer", 5, 3500);
        Employee rivalB = new MLOpsEngineer("Rival MLOps Engineer", 5, 4000);

        opponentCompany.hire(rivalA);
        opponentCompany.hire(rivalB);

        opponentProject = new Project(
            "Cloud AI Platform",
            getOpponentRequiredWork()
        );

        opponentProject.addEmployee(rivalA);
        opponentProject.addEmployee(rivalB);

        opponentCompany.startProject(opponentProject);
    }

    @PostMapping("/next-turn")
    public String nextTurn(Model model) {

        if (gameStarted && !gameOver) {

            project.workOneTurn();
            company.paySalaries();

            opponentProject.workOneTurn();
            opponentCompany.paySalaries();

            opponentAutoHire();

            message = "Turn completed.";
            addEvent("Turn completed.");

            boolean playerEventTriggered = triggerRandomEvent();

            boolean opponentEventTriggered = triggerOpponentRandomEvent();

            handleOpponentEmergencyFunding();

            checkGameState();

            if (!playerEventTriggered &&
                    !opponentEventTriggered &&
                    message.equals("Turn completed.")) {

                message = message + " No major events this turn.";
                addEvent("No major events this turn.");
            }
        }

        addGameData(model);
        return "index";
    }

    @PostMapping("/hire")
    public String hireEmployeeFromForm(
            @RequestParam String name,
            @RequestParam String role,
            Model model
    ) {

        if (gameStarted && !gameOver) {

            try {

                Employee employee = createEmployee(name, role);

                hireEmployee(employee);

            } catch (InsufficientFundsException e) {

                message = "ERROR: " + e.getMessage();
                addEvent(message);

            } catch (InvalidRoleException e) {

                message = "ERROR: " + e.getMessage();
                addEvent(message);
            }
        }

        addGameData(model);
        return "index";
    }

    @PostMapping("/fire")
    public String fireEmployeeFromForm(
            @RequestParam int employeeIndex,
            Model model
    ) {

        if (gameStarted && !gameOver) {

            try {

                fireEmployee(employeeIndex);

            } catch (EmployeeNotFoundException e) {

                message = "ERROR: " + e.getMessage();
                addEvent(message);
            }
        }

        addGameData(model);
        return "index";
    }

    @PostMapping("/restart")
    public String restart(Model model) {

        gameStarted = false;
        gameOver = false;
        message = "";
        gameResult = "";
        opponentFundingUsed = 0;
        eventLog.clear();

        addGameData(model);
        return "index";
    }

    private double getPlayerStartingCash() {

        switch (difficulty) {

            case EASY:
                return 90000;

            case HARD:
                return 55000;

            default:
                return 80000;
        }
    }

    private int getPlayerRequiredWork() {

        switch (difficulty) {

            case EASY:
                return 150;

            case HARD:
                return 200;

            default:
                return 175;
        }
    }

    private Employee createEmployee(String name, String role)
            throws InvalidRoleException {

        if (role.equals("DataEngineer")) {
            return new DataEngineer(name, 5, 3500);
        }

        if (role.equals("MLOpsEngineer")) {
            return new MLOpsEngineer(name, 6, 4500);
        }

        if (role.equals("SOCAnalyst")) {
            return new SOCAnalyst(name, 5, 3000);
        }

        if (role.equals("ProjectManager")) {
            return new ProjectManager(name, 4, 5000);
        }

        throw new InvalidRoleException(
            "Invalid employee role selected!"
        );
    }

    private void hireEmployee(Employee employee)
            throws InsufficientFundsException {

        double minimumCash = 10000;

        if (company.getCash() < minimumCash) {

            throw new InsufficientFundsException(
                "Not enough cash to hire new employee!"
            );
        }

        company.hire(employee);
        project.addEmployee(employee);

        message = employee.getName() + " hired successfully.";
        addEvent(message);
    }

    private void fireEmployee(int employeeIndex)
            throws EmployeeNotFoundException {

        if (company.getEmployees().isEmpty()) {

            throw new EmployeeNotFoundException(
                "No employees to fire!"
            );
        }

        if (employeeIndex < 0 ||
                employeeIndex >= company.getEmployees().size()) {

            throw new EmployeeNotFoundException(
                "Invalid employee selected!"
            );
        }

        Employee employee =
            company.getEmployees().get(employeeIndex);

        company.getEmployees().remove(employee);

        for (Project p : company.getProjects()) {
            p.getTeam().remove(employee);
        }

        message = employee.getName() + " was fired.";
        addEvent(message);
    }

    private void opponentAutoHire() {

        if (opponentCompany.getCash() < getOpponentHiringCashThreshold()) {
            return;
        }

        if (opponentProject.getTeam().size() >= getOpponentMaxTeamSize()) {
            return;
        }

        double progressRatio =
            (double) opponentProject.getProgress()
            / opponentProject.getRequiredWork();

        if (progressRatio < getOpponentHiringProgressLimit()) {

            Employee employee;

            if (difficulty == Difficulty.HARD) {

                employee = new MLOpsEngineer(
                    "EVCorp MLOps Engineer",
                    6,
                    4500
                );

            } else {

                employee = new DataEngineer(
                    "EVCorp Data Engineer",
                    5,
                    3500
                );
            }

            opponentCompany.hire(employee);
            opponentProject.addEmployee(employee);

            String eventMessage =
                "EVCorp hired a new employee.";

            message = message + " " + eventMessage;
            addEvent(eventMessage);
        }
    }

    private int getPlayerEventChance() {

        int eventChance = 15;

        if (company.getCash() < 20000) {
            eventChance += 10;
        }

        if (company.getEmployees().size() > 4) {
            eventChance += 10;
        }

        return eventChance;
    }

    private boolean triggerRandomEvent() {

        int chance = random.nextInt(100);

        if (chance < getPlayerEventChance()) {

            GameEvent event;

            if (company.getCash() < 20000) {

                event = new BonusEvent();

            } else if (company.getEmployees().size() > 4) {

                event = new CrisisEvent();

            } else if (random.nextBoolean()) {

                event = new CrisisEvent();

            } else {

                event = new BonusEvent();
            }

            if (event instanceof CrisisEvent
                    && crisisPrevented()) {

                String eventMessage =
                    "Player Event: " +
                    event.getName() +
                    " was prevented by SOC Analyst.";

                message = message + " " + eventMessage;
                addEvent(eventMessage);

            } else {

                event.apply(company);

                String eventMessage =
                    "Player Event: " +
                    event.getName() +
                    " affected TechCorp.";

                message = message + " " + eventMessage;
                addEvent(eventMessage);
            }

            return true;
        }

        return false;
    }

    private boolean triggerOpponentRandomEvent() {

        int eventChance;

        switch (difficulty) {

            case EASY:
                eventChance = 5;
                break;

            case HARD:
                eventChance = 25;
                break;

            default:
                eventChance = 20;
                break;
        }

        int chance = random.nextInt(100);

        if (chance < eventChance) {

            GameEvent event;

            if (opponentCompany.getCash() < 15000) {

                event = new BonusEvent();

            } else if (opponentCompany.getEmployees().size() > 4) {

                event = new CrisisEvent();

            } else if (random.nextBoolean()) {

                event = new CrisisEvent();

            } else {

                event = new BonusEvent();
            }

            event.apply(opponentCompany);

            String eventMessage =
                "Opponent Event: " +
                event.getName() +
                " affected EVCorp.";

            message = message + " " + eventMessage;
            addEvent(eventMessage);

            return true;
        }

        return false;
    }

    private boolean crisisPrevented() {

        for (Employee employee : company.getEmployees()) {

            if (employee instanceof SOCAnalyst) {

                SOCAnalyst socAnalyst =
                    (SOCAnalyst) employee;

                if (socAnalyst.preventIncident()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void handleOpponentEmergencyFunding() {

        if (opponentCompany.getCash() < 10000 &&
                opponentFundingUsed < 1) {

            opponentCompany.addCash(
                getOpponentEmergencyFunding()
            );

            opponentFundingUsed++;

            String eventMessage =
                "EVCorp received emergency funding.";

            message = message + " " + eventMessage;
            addEvent(eventMessage);
        }
    }

    private void checkGameState() {

        if (company.getCash() < 0 &&
                opponentCompany.getCash() < 0) {

            gameOver = true;

            gameResult =
                "Both companies went bankrupt. No winner.";

            addEvent(gameResult);

            return;
        }

        if (company.getCash() < 0) {

            gameOver = true;

            gameResult =
                "TechCorp went bankrupt. EVCorp wins.";

            addEvent(gameResult);

            return;
        }

        if (opponentCompany.getCash() < 0) {

            gameOver = true;

            gameResult =
                "EVCorp went bankrupt. TechCorp wins.";

            addEvent(gameResult);

            return;
        }

        if (project.isFinished()) {

            gameOver = true;

            gameResult =
                "TechCorp completed the project first. TechCorp wins!";

            addEvent(gameResult);

            return;
        }

        if (opponentProject.isFinished()) {

            gameOver = true;

            gameResult =
                "EVCorp completed its project first. EVCorp wins.";

            addEvent(gameResult);
        }
    }

    private void addEvent(String event) {

        eventLog.add(0, event);

        if (eventLog.size() > 8) {
            eventLog.remove(eventLog.size() - 1);
        }
    }

    private double getOpponentStartingCash() {

        switch (difficulty) {

            case EASY:
                return 45000;

            case HARD:
                return 80000;

            default:
                return 60000;
        }
    }

    private int getOpponentRequiredWork() {

        switch (difficulty) {

            case EASY:
                return 170;

            case HARD:
                return 180;

            default:
                return 175;
        }
    }

    private double getOpponentHiringCashThreshold() {

        switch (difficulty) {

            case EASY:
                return 40000;

            case HARD:
                return 15000;

            default:
                return 25000;
        }
    }

    private int getOpponentMaxTeamSize() {

        switch (difficulty) {

            case EASY:
                return 3;

            case HARD:
                return 6;

            default:
                return 4;
        }
    }

    private double getOpponentHiringProgressLimit() {

        switch (difficulty) {

            case EASY:
                return 0.40;

            case HARD:
                return 0.85;

            default:
                return 0.65;
        }
    }

    private double getOpponentEmergencyFunding() {

        switch (difficulty) {

            case EASY:
                return 5000;

            case HARD:
                return 30000;

            default:
                return 15000;
        }
    }

    private void addGameData(Model model) {

        model.addAttribute("gameStarted", gameStarted);
        model.addAttribute("gameOver", gameOver);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("message", message);
        model.addAttribute("gameResult", gameResult);
        model.addAttribute("eventLog", eventLog);

        if (gameStarted) {

            model.addAttribute("companyName", company.getName());
            model.addAttribute("cash", company.getCash());
            model.addAttribute("projectName", project.getName());
            model.addAttribute("projectProgress", project.getProgress());
            model.addAttribute("requiredWork", project.getRequiredWork());
            model.addAttribute("employees", company.getEmployees());

            model.addAttribute("opponentName", opponentCompany.getName());
            model.addAttribute("opponentCash", opponentCompany.getCash());
            model.addAttribute("opponentProjectName", opponentProject.getName());
            model.addAttribute("opponentProjectProgress", opponentProject.getProgress());
            model.addAttribute("opponentRequiredWork", opponentProject.getRequiredWork());
            model.addAttribute("opponentEmployees", opponentCompany.getEmployees());
        }
    }
}