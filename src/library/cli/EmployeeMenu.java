package library.cli;

import library.LibrarySystem;
import library.models.Book;
import library.models.Employee;
import library.models.LoanRequest;
import library.models.LoanRecord;
import library.models.Student;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

class EmployeeMenu {
    private final LibrarySystem system;
    private final Scanner scanner;

    EmployeeMenu(LibrarySystem system, Scanner scanner) {
        this.system = system;
        this.scanner = scanner;
    }

    void showMenu() {
        System.out.println("\n--- ورود کارمند ---");
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine().trim();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine().trim();
        Employee employee = system.employees.get(username);
        if (employee == null || !employee.password.equals(password)) {
            System.out.println("اطلاعات ورود نامعتبر");
            return;
        }
        System.out.println("ورود موفق: " + employee.name);
        handleEmployeeSession(employee);
    }

    private void handleEmployeeSession(Employee employee) {
        while (true) {
            System.out.println("\n--- منوی کارمند ---");
            System.out.println("1) تغییر رمز\n2) ثبت کتاب\n3) جستجو و ویرایش کتاب\n4) بررسی و تایید درخواست‌ها\n5) مشاهده تاریخچه امانات دانشجو\n6) فعال/غیرفعال کردن دانشجو\n7) دریافت کتاب و ثبت زمان دریافت\n8) خروج");
            System.out.print("انتخاب: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    changePassword(employee);
                    break;
                case "2":
                    registerBook(employee);
                    break;
                case "3":
                    editBook();
                    break;
                case "4":
                    approveRequests(employee);
                    break;
                case "5":
                    showStudentHistory();
                    break;
                case "6":
                    toggleStudentActive();
                    break;
                case "7":
                    receiveBook(employee);
                    break;
                case "8":
                    return;
                default:
                    System.out.println("نامعتبر");
            }
        }
    }

    private void changePassword(Employee employee) {
        System.out.print("رمز جدید: ");
        employee.password = scanner.nextLine().trim();
        System.out.println("تغییر رمز انجام شد.");
    }

    private void registerBook(Employee employee) {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        if (system.books.containsKey(isbn)) {
            System.out.println("کتاب با این ISBN موجود است.");
            return;
        }
        System.out.print("عنوان: ");
        String title = scanner.nextLine();
        System.out.print("نویسنده: ");
        String author = scanner.nextLine();
        System.out.print("سال نشر: ");
        int year = Integer.parseInt(scanner.nextLine().trim());
        Book book = new Book(isbn, title, author, year);
        system.addBook(book);
        employee.registeredBooks++;
        System.out.println("کتاب ثبت شد.");
    }

    private void editBook() {
        System.out.print("ISBN یا بخشی از عنوان: ");
        String query = scanner.nextLine();
        List<Book> result = system.searchBooks(query);
        if (result.isEmpty()) {
            System.out.println("پیدا نشد");
            return;
        }
        for (int i = 0; i < result.size(); i++) {
            System.out.println((i + 1) + ") " + result.get(i).brief());
        }
        System.out.print("شماره مورد نظر را وارد کنید: ");
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index < 0 || index >= result.size()) {
            System.out.println("نامعتبر");
            return;
        }
        Book book = result.get(index);
        System.out.print("عنوان جدید (خالی برای بدون تغییر): ");
        String newTitle = scanner.nextLine();
        if (!newTitle.isEmpty()) book.title = newTitle;
        System.out.print("نویسنده جدید (خالی برای بدون تغییر): ");
        String newAuthor = scanner.nextLine();
        if (!newAuthor.isEmpty()) book.author = newAuthor;
        System.out.print("سال نشر جدید (خالی برای بدون تغییر): ");
        String newYear = scanner.nextLine();
        if (!newYear.isEmpty()) book.year = Integer.parseInt(newYear);
        System.out.println("ویرایش انجام شد.");
    }

    private void approveRequests(Employee employee) {
        System.out.println("درخواست‌هایی که تاریخ شروع آنها امروز یا دیروز است:");
        LocalDate today = LocalDate.now();
        List<LoanRequest> candidates = new ArrayList<>();
        for (LoanRequest request : system.loanRequests) {
            boolean inRange = !request.start.isAfter(today) && !request.start.isBefore(today.minusDays(1));
            if (!request.approved && inRange) {
                candidates.add(request);
            }
        }
        if (candidates.isEmpty()) {
            System.out.println("درخواست جدیدی برای امروز/دیروز وجود ندارد.");
            return;
        }
        for (int i = 0; i < candidates.size(); i++) {
            System.out.println((i + 1) + ") " + candidates.get(i).brief());
        }
        System.out.print(" شماره درخواست برای تایید (یا 0 برای لغو): ");
        int selection = Integer.parseInt(scanner.nextLine().trim());
        if (selection <= 0 || selection > candidates.size()) {
            return;
        }
        LoanRequest chosen = candidates.get(selection - 1);
        chosen.approved = true;
        chosen.approvedBy = employee;
        employee.loansGiven++;
        LoanRecord record = new LoanRecord(chosen.student, chosen.book, chosen.start, chosen.end, employee);
        system.loanRecords.add(record);
        System.out.println("درخواست تایید شد. دانشجو می‌تواند کتاب را تحویل بگیرد.");
    }

    private void showStudentHistory() {
        System.out.print("نام کاربری دانشجو: ");
        String username = scanner.nextLine().trim();
        Student student = system.students.get(username);
        if (student == null) {
            System.out.println("دانشجو یافت نشد.");
            return;
        }
        long total = system.loanRecords.stream()
                .filter(record -> record.student.username.equals(username))
                .count();
        long notReturned = system.loanRecords.stream()
                .filter(record -> record.student.username.equals(username) && !record.returned)
                .count();
        long delayed = system.loanRecords.stream()
                .filter(record -> record.student.username.equals(username) && record.returned && record.delayed())
                .count();
        System.out.println("تعداد کل امانات: " + total);
        System.out.println("تعداد کتاب‌های تحویل داده نشده: " + notReturned);
        System.out.println("تعداد امانت‌های با تاخیر تحویل داده شده: " + delayed);
        system.loanRecords.stream()
                .filter(record -> record.student.username.equals(username))
                .forEach(record -> System.out.println(record.toString()));
    }

    private void toggleStudentActive() {
        System.out.print("نام کاربری دانشجو: ");
        String username = scanner.nextLine().trim();
        Student student = system.students.get(username);
        if (student == null) {
            System.out.println("نیست");
            return;
        }
        student.active = !student.active;
        System.out.println("وضعیت فعلی: " + (student.active ? "فعال" : "غیرفعال"));
    }

    private void receiveBook(Employee employee) {
        System.out.print("ISBN کتاب دریافتی: ");
        String isbn = scanner.nextLine().trim();
        Optional<LoanRecord> openLoan = system.loanRecords.stream()
                .filter(record -> record.book.isbn.equals(isbn) && !record.returned)
                .findFirst();
        if (!openLoan.isPresent()) {
            System.out.println("هیچ امانت باز با این ISBN پیدا نشد.");
            return;
        }
        LoanRecord record = openLoan.get();
        record.returned = true;
        record.returnedAt = LocalDate.now();
        employee.booksReceived++;
        System.out.println("دریافت ثبت شد. تاریخ دریافت: " + record.returnedAt);
    }
}

