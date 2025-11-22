package library.cli;

import library.LibrarySystem;
import java.util.Scanner;

public class LibraryCLI {
    private final Scanner scanner;
    private final GuestMenu guestMenu;
    private final StudentMenu studentMenu;
    private final EmployeeMenu employeeMenu;
    private final AdminMenu adminMenu;

    public LibraryCLI(LibrarySystem system) {
        this.scanner = new Scanner(System.in);
        this.guestMenu = new GuestMenu(system, scanner);
        this.studentMenu = new StudentMenu(system, scanner);
        this.employeeMenu = new EmployeeMenu(system, scanner);
        this.adminMenu = new AdminMenu(system, scanner);
    }

    public void start() {
        while (true) {
            System.out.println("\n--- منوی اصلی ---");
            System.out.println("1) مهمان\n2) دانشجو\n3) کارمند\n4) مدیر\n5) خروج");
            System.out.print("انتخاب: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    guestMenu.showMenu();
                    break;
                case "2":
                    studentMenu.showMenu();
                    break;
                case "3":
                    employeeMenu.showMenu();
                    break;
                case "4":
                    adminMenu.showMenu();
                    break;
                case "5":
                    System.out.println("خروج...");
                    return;
                default:
                    System.out.println("ورودی نامعتبر");
            }
        }
    }
}

