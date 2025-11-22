package library.cli;

import library.LibrarySystem;
import library.models.Employee;
import library.models.LoanRecord;
import library.models.Student;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class AdminMenu {
    private final LibrarySystem system;
    private final Scanner scanner;

    AdminMenu(LibrarySystem system, Scanner scanner) {
        this.system = system;
        this.scanner = scanner;
    }

    void showMenu() {
        System.out.println("\n--- منوی مدیر ---");
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine().trim();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine().trim();
        if (!username.equals(system.adminUser) || !password.equals(system.adminPass)) {
            System.out.println("ورود ناموفق");
            return;
        }
        handleAdminSession();
    }

    private void handleAdminSession() {
        while (true) {
            System.out.println("\n--- منوی مدیر ---");
            System.out.println("1) تعریف کارمند\n2) مشاهده عملکرد کارمند\n3) آمار امانات\n4) آمار دانشجویان\n5) خروج");
            System.out.print("انتخاب: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    createEmployee();
                    break;
                case "2":
                    viewEmployeePerformance();
                    break;
                case "3":
                    printLoansStats();
                    break;
                case "4":
                    printStudentsStats();
                    break;
                case "5":
                    return;
                default:
                    break;
            }
        }
    }

    private void createEmployee() {
        System.out.print("نام کاربری کارمند: ");
        String username = scanner.nextLine().trim();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine().trim();
        System.out.print("نام: ");
        String name = scanner.nextLine().trim();
        system.employees.put(username, new Employee(username, password, name));
        System.out.println("کارمند اضافه شد.");
    }

    private void viewEmployeePerformance() {
        System.out.print("نام کاربری کارمند: ");
        String username = scanner.nextLine().trim();
        Employee employee = system.employees.get(username);
        if (employee == null) {
            System.out.println("نیست");
            return;
        }
        System.out.println("تعداد کتاب‌های ثبت شده: " + employee.registeredBooks);
        System.out.println("تعداد کل کتاب‌هایی که امانت داده شده: " + employee.loansGiven);
        System.out.println("تعداد کل کتاب‌هایی که تحویل گرفته: " + employee.booksReceived);
    }

    private void printLoansStats() {
        System.out.println("تعداد درخواست‌های ثبت شده: " + system.loanRequests.size());
        System.out.println("تعداد کل امانت داده شده: " + system.loanRecords.size());
        double averageDays = system.loanRecords.stream()
                .filter(record -> record.returned)
                .mapToLong(LoanRecord::durationDays)
                .average()
                .orElse(0.0);
        System.out.println("میانگین تعداد روزهای امانت (برای بازگشت شده‌ها): " + String.format("%.2f", averageDays));
    }

    private void printStudentsStats() {
        System.out.println("آمار برای همه دانشجویان:");
        for (Student student : system.students.values()) {
            long total = system.loanRecords.stream()
                    .filter(record -> record.student.username.equals(student.username))
                    .count();
            long notReturned = system.loanRecords.stream()
                    .filter(record -> record.student.username.equals(student.username) && !record.returned)
                    .count();
            long delayed = system.loanRecords.stream()
                    .filter(record -> record.student.username.equals(student.username) && record.returned && record.delayed())
                    .count();
            System.out.println(String.format("%s: total=%d, notReturned=%d, delayed=%d",
                    student.username, total, notReturned, delayed));
        }
        System.out.println("10 دانشجوی با بیشترین تاخیر:");
        Map<String, Long> delayedCount = new HashMap<>();
        for (LoanRecord record : system.loanRecords) {
            if (record.returned && record.delayed()) {
                delayedCount.put(record.student.username,
                        delayedCount.getOrDefault(record.student.username, 0L) + 1);
            }
        }
        delayedCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }
}

