package library;

import library.models.*;
import library.cli.LibraryCLI;
import library.ui.LibraryGUI;
import javax.swing.*;
import java.util.*;

/**
 * LibrarySystem.java
 *
 * نسخهٔ کامل و قابل اجرا از سیستم مدیریت کتابخانه (درون‌حافظه‌ای) با CLI و Swing GUI.
 *
 * اجرا:
 *   javac -d . -sourcepath src src/library/LibrarySystem.java
 *   java library.LibrarySystem
 *
 * توضیح: داده‌ها در حافظه ذخیره می‌شوند (بدون پایگاه‌داده یا فایل).
 */
public class LibrarySystem {
    // داده‌ها (الان public تا GUI بتواند به راحتی از آنها استفاده کند)
    public final Map<String, Student> students = new HashMap<>();
    public final Map<String, Employee> employees = new HashMap<>();
    public final Map<String, Book> books = new HashMap<>();
    public final List<LoanRequest> loanRequests = new ArrayList<>();
    public final List<LoanRecord> loanRecords = new ArrayList<>();

    // اعتبارسنجی مدیر (قابل دسترسی برای GUI)
    public final String adminUser = "admin";
    public final String adminPass = "admin";

    public static void main(String[] args) {
        LibrarySystem sys = new LibrarySystem();
        sys.seedSampleData();
        System.out.println("کتابخانه ساده - اجرا با Java");
        System.out.println("1) اجرای CLI\n2) اجرای GUI (Swing)");
        System.out.print("انتخاب کنید (1/2): ");
        try (Scanner sc = new Scanner(System.in)) {
            String c = sc.nextLine().trim();
            if (c.equals("2")) {
                SwingUtilities.invokeLater(() -> new LibraryGUI(sys));
            } else {
                sys.runCLI();
            }
        }
    }

    private void seedSampleData() {
        // چند دانشجو و کارمند و کتاب نمونه
        students.put("sara", new Student("sara", "pass1", "Sara", "sara@example.com"));
        students.put("ali", new Student("ali", "pass2", "Ali", "ali@example.com"));

        employees.put("emp1", new Employee("emp1", "e123", "Ehsan"));
        employees.put("emp2", new Employee("emp2", "e123", "Mina"));

        addBook(new Book("978-1", "Introduction to Java", "John Doe", 2010));
        addBook(new Book("978-2", "Data Structures", "Alice", 2015));
        addBook(new Book("978-3", "Operating Systems", "Bob", 2008));
    }

    // --------------------- CLI ---------------------
    private void runCLI() {
        new LibraryCLI(this).start();
    }

    // --------------------- Helpers & Model ops ---------------------
    // این متدها public هستند تا GUI نیز بتواند از آنها استفاده کند.
    public void addBook(Book b) {
        books.put(b.isbn, b);
    }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> res = new ArrayList<>();
        for (Book b : books.values()) {
            if (b.title.toLowerCase().contains(title.toLowerCase())) {
                res.add(b);
            }
        }
        return res;
    }

    public List<Book> searchBooks(String q) {
        List<Book> res = new ArrayList<>();
        for (Book b : books.values()) {
            if (b.title.toLowerCase().contains(q.toLowerCase()) || b.author.toLowerCase().contains(q.toLowerCase())) {
                res.add(b);
            } else {
                try {
                    if (Integer.toString(b.year).equals(q)) {
                        res.add(b);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return res;
    }

    public void printSimpleStats() {
        long currentlyLoaned = loanRecords.stream().filter(r -> !r.returned).count();
        System.out.println("تعداد کل دانشجویان: " + students.size());
        System.out.println("تعداد کل کتاب‌ها: " + books.size());
        System.out.println("تعداد کل امانت‌ها: " + loanRecords.size());
        System.out.println("تعداد کتاب‌هایی که هم‌اکنون امانت هستند: " + currentlyLoaned);
    }
}

