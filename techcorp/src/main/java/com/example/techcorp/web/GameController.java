package com.example.techcorp.web;

import com.example.techcorp.engine.GameSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameController {

    private GameSession gameSession = new GameSession();

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

        gameSession.startGame(selectedDifficulty);

        addGameData(model);

        return "index";
    }

    @PostMapping("/next-turn")
    public String nextTurn(Model model) {

        gameSession.nextTurn();

        addGameData(model);

        return "index";
    }

    @PostMapping("/hire")
    public String hireEmployeeFromForm(
            @RequestParam String name,
            @RequestParam String role,
            Model model
    ) {

        gameSession.hireEmployeeFromForm(
            name,
            role
        );

        addGameData(model);

        return "index";
    }

    @PostMapping("/fire")
    public String fireEmployeeFromForm(
            @RequestParam int employeeIndex,
            Model model
    ) {

        gameSession.fireEmployeeFromForm(
            employeeIndex
        );

        addGameData(model);

        return "index";
    }

    @PostMapping("/restart")
    public String restart(Model model) {

        gameSession.restart();

        addGameData(model);

        return "index";
    }

    private void addGameData(Model model) {

        model.addAttribute(
            "gameStarted",
            gameSession.isGameStarted()
        );

        model.addAttribute(
            "gameOver",
            gameSession.isGameOver()
        );

        model.addAttribute(
            "difficulty",
            gameSession.getDifficulty()
        );

        model.addAttribute(
            "message",
            gameSession.getMessage()
        );

        model.addAttribute(
            "gameResult",
            gameSession.getGameResult()
        );

        model.addAttribute(
            "eventLog",
            gameSession.getEventLog()
        );

        if (gameSession.isGameStarted()) {

            model.addAttribute(
                "companyName",
                gameSession.getCompany().getName()
            );

            model.addAttribute(
                "cash",
                gameSession.getCompany().getCash()
            );

            model.addAttribute(
                "projectName",
                gameSession.getProject().getName()
            );

            model.addAttribute(
                "projectProgress",
                gameSession.getProject().getProgress()
            );

            model.addAttribute(
                "requiredWork",
                gameSession.getProject().getRequiredWork()
            );

            model.addAttribute(
                "employees",
                gameSession.getCompany().getEmployees()
            );

            model.addAttribute(
                "opponentName",
                gameSession.getOpponentCompany().getName()
            );

            model.addAttribute(
                "opponentCash",
                gameSession.getOpponentCompany().getCash()
            );

            model.addAttribute(
                "opponentProjectName",
                gameSession.getOpponentProject().getName()
            );

            model.addAttribute(
                "opponentProjectProgress",
                gameSession.getOpponentProject().getProgress()
            );

            model.addAttribute(
                "opponentRequiredWork",
                gameSession.getOpponentProject().getRequiredWork()
            );

            model.addAttribute(
                "opponentEmployees",
                gameSession.getOpponentCompany().getEmployees()
            );
        }
    }
}