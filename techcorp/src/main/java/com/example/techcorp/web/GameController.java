package com.example.techcorp.web;

import com.example.techcorp.domain.*;
import com.example.techcorp.events.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    private Random random = new Random();

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

        project = new Project("Cybersecurity Infrastructure", 120);

        project.addEmployee(a);
        project.addEmployee(b);

        company.startProject(project);

        opponentCompany = new Company("EVCorp", getOpponentStartingCash());

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

            boolean playerEventTriggered =
                triggerRandomEvent();

            boolean opponentEventTriggered =
                triggerOpponentRandomEvent();

            if (!playerEventTriggered &&
                    !opponentEventTriggered) {

                message =
                    message +
                    " No major events this turn.";
            }

            checkGameState();
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

            Employee employee = createEmployee(name, role);

            if (employee != null) {
                hireEmployee(employee);
            } else {
                message = "Invalid role selected.";
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

        addGameData(model);

        return "index";
    }

    private double getPlayerStartingCash() {

        switch (difficulty) {

            case EASY:
                return 90000;

            case HARD:
                return 65000;

            default:
                return 80000;
        }
    }

    private Employee createEmployee(String name, String role) {

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

        return null;
    }

    private void hireEmployee(Employee employee) {

        double minimumCash = 10000;

        if (company.getCash() < minimumCash) {
            message = "Not enough cash to hire.";
            return;
        }

        company.hire(employee);
        project.addEmployee(employee);

        message = employee.getName() + " hired successfully.";
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

            message =
                message +
                " EVCorp hired a new employee.";
        }
    }

    private boolean triggerRandomEvent() {

        int chance = random.nextInt(100);

        if (chance < 30) {

            GameEvent event;

            if (random.nextBoolean()) {
                event = new CrisisEvent();
            } else {
                event = new BonusEvent();
            }

            if (event instanceof CrisisEvent
                    && crisisPrevented()) {

                message =
                    message +
                    " EVENT: " +
                    event.getName() +
                    " was prevented by SOC Analyst.";

            } else {

                event.apply(company);

                message =
                    message +
                    " EVENT: " +
                    event.getName() +
                    " affected TechCorp.";
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

            event.apply(opponentCompany);

            message =
                message +
                " OPPONENT EVENT: " +
                event.getName() +
                " affected EVCorp.";

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

    private void checkGameState() {

        if (project.isFinished()) {

            gameOver = true;

            gameResult =
                "You completed the project before EVCorp. You win!";

            return;
        }

        if (opponentProject.isFinished()) {

            gameOver = true;

            gameResult =
                "EVCorp completed its project first. You lose.";

            return;
        }

        if (company.getCash() < 0) {

            gameOver = true;

            gameResult =
                "Company went bankrupt. Game Over.";

            return;
        }

        if (opponentCompany.getCash() < 10000) {

            opponentCompany.addCash(
                getOpponentEmergencyFunding()
            );

            message =
                message +
                " EVCorp received emergency funding.";
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
                return 145;

            case HARD:
                return 115;

            default:
                return 135;
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