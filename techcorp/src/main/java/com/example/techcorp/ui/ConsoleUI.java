package com.example.techcorp.ui;

import com.example.techcorp.domain.Company;
import com.example.techcorp.domain.Project;
import com.example.techcorp.domain.Employee;
import com.example.techcorp.domain.Employee;

import java.util.Scanner;

public class ConsoleUI {

    private Scanner scanner = new Scanner(System.in);

    public void showTurnHeader(int turn) {
        System.out.println("\n=== TURN " + turn + " ===");
    }

    public void showMainMenu() {

        System.out.println("\n===== MENU =====");
        System.out.println("1. Show company status");
        System.out.println("2. Advance one work turn");
        System.out.println("3. Hire employee");
        System.out.println("4. Fire employee");
        System.out.println("5. Exit game");
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

        System.out.println("\n===== COMPANY STATUS =====");

        System.out.println("Company: " + company.getName());
        System.out.println("Cash: $" + company.getCash());

        System.out.println("\nProjects:");

        for (Project p : company.getProjects()) {

            System.out.println(
                "- " +
                p.getName() +
                " | Progress: " +
                p.getProgress() +
                "/" +
                p.getRequiredWork()
            );
        }

        System.out.println("\nEmployees:");

        for (Employee e : company.getEmployees()) {

            System.out.println(
                "- " +
                e.getClass().getSimpleName() +
                " : " +
                e.getName() +
                " | Salary: $" +
                e.getSalary()
            );
        }
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }
    public String readString(String message) {

    System.out.print(message);

    return scanner.nextLine();
    }
    public void showHireMenu() {

    System.out.println("\nChoose employee role:");
    System.out.println("1. Data Engineer");
    System.out.println("2. MLOps Engineer");
    System.out.println("3. SOC Analyst");
    System.out.println("4. Project Manager");
}
   public void showEmployeeList(Company company) {

    System.out.println("\nEmployees:");

    int index = 1;

    for (Employee e : company.getEmployees()) {

        System.out.println(
            index +
            ". " +
            e.getName() +
            " (" +
            e.getClass().getSimpleName() +
            ")"
        );

        index++;
    }
}

}