package library.cli;

import library.LibrarySystem;
import library.models.Book;
import library.models.Student;
import library.models.LoanRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

class StudentMenu {
    private final LibrarySystem system;
    private final Scanner scanner;

    StudentMenu(LibrarySystem system, Scanner scanner) {
        this.system = system;
        this.scanner = scanner;
    }

    void showMenu() {
        System.out.println("\n--- منوی دانشجو ---");
        System.out.println("1) ثبت‌نام\n2) ورود\n3) بازگشت");
        System.out.print("انتخاب: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                registerStudent();
                break;
            case "2":
                loginStudent();
                break;
            default:
                break;
        }
    }

    private void registerStudent() {
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine().trim();
        if (system.students.containsKey(username)) {
            System.out.println("نام کاربری موجود است.");
            return;
        }
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine().trim();
        System.out.print("نام: ");
        String name = scanner.nextLine().trim();
        System.out.print("ایمیل: ");
        String email = scanner.nextLine().trim();
        Student student = new Student(username, password, name, email);
        system.students.put(username, student);
        System.out.println("ثبت‌نام با موفقیت انجام شد.");
    }

    private void loginStudent() {
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine().trim();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine().trim();
        Student student = system.students.get(username);
        if (student == null || !student.password.equals(password)) {
            System.out.println("نام کاربری یا رمز عبور نادرست است.");
            return;
        }
        System.out.println("ورود موفق. خوش آمدی " + student.name);
        handleStudentSession(student);
    }

    private void handleStudentSession(Student student) {
        while (true) {
            System.out.println("\n--- منوی دانشجو ---");
            System.out.println("1) جستجوی کتاب (عنوان/سال/نویسنده)");
            System.out.println("2) مشاهده وضعیت امانت‌های من");
            System.out.println("3) ثبت درخواست امانت");
            System.out.println("4) خروج");
            System.out.print("انتخاب: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleBookSearch();
                    break;
                case "2":
                    printStudentLoans(student);
                    break;
                case "3":
                    requestLoan(student);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("نامعتبر");
            }
        }
    }

    private void handleBookSearch() {
        System.out.print("جستجو (عنوان یا سال یا نویسنده): ");
        String query = scanner.nextLine();
        List<Book> result = system.searchBooks(query);
        if (result.isEmpty()) {
            System.out.println("نتیجه‌ای نیست.");
            return;
        }
        result.forEach(book -> System.out.println(book.full()));
    }

    private void printStudentLoans(Student student) {
        System.out.println("تاریخچه امانات:");
        system.loanRecords.stream()
                .filter(record -> record.student.username.equals(student.username))
                .forEach(record -> System.out.println(record.toString()));
    }

    private void requestLoan(Student student) {
        if (!student.active) {
            System.out.println("حساب شما غیر فعال است؛ امکان امانت وجود ندارد.");
            return;
        }
        System.out.print("شناسه کتاب (ISBN) را وارد کنید: ");
        String isbn = scanner.nextLine().trim();
        Book book = system.books.get(isbn);
        if (book == null) {
            System.out.println("کتاب یافت نشد.");
            return;
        }
        System.out.print("تاریخ شروع (YYYY-MM-DD): ");
        String startString = scanner.nextLine().trim();
        System.out.print("تاریخ پایان (YYYY-MM-DD): ");
        String endString = scanner.nextLine().trim();
        try {
            LocalDate start = LocalDate.parse(startString);
            LocalDate end = LocalDate.parse(endString);
            LoanRequest request = new LoanRequest(student, book, start, end);
            system.loanRequests.add(request);
            System.out.println("درخواست ثبت شد. منتظر تایید کارمند باشید.");
        } catch (Exception e) {
            System.out.println("فرمت تاریخ نادرست است.");
        }
    }
}

