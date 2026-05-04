package com.example.techcorp.ui;

import com.example.techcorp.domain.Company;
import com.example.techcorp.domain.Project;

import java.util.Scanner;

public class ConsoleUI {

    private Scanner scanner = new Scanner(System.in);

    public void showTurnHeader(int turn) {
        System.out.println("\n=== TURN " + turn + " ===");
    }

    public void showMainMenu() {
        System.out.println("1. Show status");
        System.out.println("2. Work on projects");
        System.out.println("3. Exit");
    }

    public int readMenuChoice() {
        System.out.print("Choice: ");
        if (!scanner.hasNextInt()) {
            scanner.nextLine();
            return -1;
        }
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public void showCompanyStatus(Company company) {
        System.out.println("Company: " + company.getName());
        for (Project p : company.getProjects()) {
            System.out.println(
                p.getName() + " " +
                p.getProgress() + "/" + p.getRequiredWork()
            );
        }
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }
}