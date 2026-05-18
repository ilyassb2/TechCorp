package com.example.techcorp.web;

import com.example.techcorp.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GameController {

    private Company company;
    private Project project;

    public GameController() {

        company = new Company("TechCorp", 55000);

        Employee a = new DataEngineer("Anna", 5, 3500);
        Employee b = new MLOpsEngineer("Leo", 6, 4500);

        company.hire(a);
        company.hire(b);

        project = new Project("Cybersecurity Infrastructure", 120);

        project.addEmployee(a);
        project.addEmployee(b);

        company.startProject(project);
    }

    @GetMapping("/")
    public String home(Model model) {

        addGameData(model);

        return "index";
    }

    @PostMapping("/next-turn")
    public String nextTurn(Model model) {

        project.workOneTurn();

        company.paySalaries();

        addGameData(model);

        return "index";
    }

    private void addGameData(Model model) {

        List<String> employeeNames = new ArrayList<>();

        for (Employee employee : company.getEmployees()) {
            employeeNames.add(employee.getName());
        }

        model.addAttribute("companyName", company.getName());
        model.addAttribute("cash", company.getCash());
        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectProgress", project.getProgress());
        model.addAttribute("requiredWork", project.getRequiredWork());
        model.addAttribute("employeeNames", employeeNames);
    }
}