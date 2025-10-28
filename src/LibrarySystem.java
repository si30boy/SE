import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

/**
 * LibrarySystem.java
 *
 * نسخهٔ کامل و قابل اجرا از سیستم مدیریت کتابخانه (درون‌حافظه‌ای) با CLI و Swing GUI.
 *
 * اجرا:
 *   javac LibrarySystem.java
 *   java LibrarySystem
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

    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        LibrarySystem sys = new LibrarySystem();
        sys.seedSampleData();
        System.out.println("کتابخانه ساده - اجرا با Java");
        System.out.println("1) اجرای CLI\n2) اجرای GUI (Swing)");
        System.out.print("انتخاب کنید (1/2): ");
        Scanner sc = new Scanner(System.in);
        String c = sc.nextLine().trim();
        if (c.equals("2")) {
            // launch Swing GUI
            SwingUtilities.invokeLater(() -> new LibraryGUI(sys));
        } else {
            sys.runCLI();
        }
    }

    private void seedSampleData() {
        // چند دانشجو و کارمند و کتاب نمونه
        students.put("sara", new Student("sara", "pass1", "Sara","sara@example.com"));
        students.put("ali", new Student("ali", "pass2","Ali","ali@example.com"));

        employees.put("emp1", new Employee("emp1","e123","Ehsan"));
        employees.put("emp2", new Employee("emp2","e123","Mina"));

        addBook(new Book("978-1","Introduction to Java", "John Doe", 2010));
        addBook(new Book("978-2","Data Structures", "Alice", 2015));
        addBook(new Book("978-3","Operating Systems", "Bob", 2008));
    }

    // --------------------- CLI ---------------------
    private void runCLI() {
        while (true) {
            System.out.println("\n--- منوی اصلی ---");
            System.out.println("1) مهمان\n2) دانشجو\n3) کارمند\n4) مدیر\n5) خروج");
            System.out.print("انتخاب: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": guestMenuCLI(); break;
                case "2": studentMenuCLI(); break;
                case "3": employeeMenuCLI(); break;
                case "4": adminMenuCLI(); break;
                case "5": System.out.println("خروج..."); return;
                default: System.out.println("ورودی نامعتبر");
            }
        }
    }

    private void guestMenuCLI() {
        System.out.println("\n--- منوی مهمان ---");
        System.out.println("1) مشاهده تعداد دانشجویان ثبت‌نام شده");
        System.out.println("2) جستجو بر اساس نام کتاب (فقط اطلاعات کتاب)");
        System.out.println("3) مشاهده آمار ساده");
        System.out.println("4) بازگشت");
        System.out.print("انتخاب: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": System.out.println("تعداد دانشجویان: " + students.size()); break;
            case "2":
                System.out.print("نام کتاب را وارد کنید: ");
                String title = scanner.nextLine();
                List<Book> res = searchBooksByTitle(title);
                if (res.isEmpty()) System.out.println("نتیجه‌ای پیدا نشد.");
                else res.forEach(b -> System.out.println(b.brief()));
                break;
            case "3": printSimpleStats(); break;
            default: break;
        }
    }

    private void studentMenuCLI() {
        System.out.println("\n--- منوی دانشجو ---");
        System.out.println("1) ثبت‌نام\n2) ورود\n3) بازگشت");
        System.out.print("انتخاب: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": studentRegisterCLI(); break;
            case "2": studentLoginCLI(); break;
            default: break;
        }
    }

    private void studentRegisterCLI() {
        System.out.print("نام کاربری: "); String user = scanner.nextLine().trim();
        if (students.containsKey(user)) { System.out.println("نام کاربری موجود است."); return; }
        System.out.print("رمز عبور: "); String pass = scanner.nextLine().trim();
        System.out.print("نام: "); String name = scanner.nextLine().trim();
        System.out.print("ایمیل: "); String email = scanner.nextLine().trim();
        Student s = new Student(user, pass, name, email);
        students.put(user, s);
        System.out.println("ثبت‌نام با موفقیت انجام شد.");
    }

    private void studentLoginCLI() {
        System.out.print("نام کاربری: "); String user = scanner.nextLine().trim();
        System.out.print("رمز عبور: "); String pass = scanner.nextLine().trim();
        Student s = students.get(user);
        if (s==null || !s.password.equals(pass)) { System.out.println("نام کاربری یا رمز عبور نادرست است."); return; }
        System.out.println("ورود موفق. خوش آمدی " + s.name);
        while (true) {
            System.out.println("\n--- منوی دانشجو ---");
            System.out.println("1) جستجوی کتاب (عنوان/سال/نویسنده)");
            System.out.println("2) مشاهده وضعیت امانت‌های من");
            System.out.println("3) ثبت درخواست امانت");
            System.out.println("4) خروج");
            System.out.print("انتخاب: "); String c = scanner.nextLine().trim();
            switch (c) {
                case "1":
                    System.out.print("جستجو (عنوان یا سال یا نویسنده): ");
                    String q = scanner.nextLine();
                    List<Book> res = searchBooks(q);
                    if (res.isEmpty()) System.out.println("نتیجه‌ای نیست.");
                    else res.forEach(b -> System.out.println(b.full()));
                    break;
                case "2":
                    printStudentLoansCLI(s);
                    break;
                case "3":
                    requestLoanCLI(s);
                    break;
                case "4": return;
                default: System.out.println("نامعتبر");
            }
        }
    }

    private void printStudentLoansCLI(Student s) {
        System.out.println("تاریخچه امانات:");
        loanRecords.stream().filter(r -> r.student.username.equals(s.username)).forEach(r -> System.out.println(r.toString()));
    }

    private void requestLoanCLI(Student s) {
        if (!s.active) { System.out.println("حساب شما غیر فعال است؛ امکان امانت وجود ندارد."); return; }
        System.out.print("شناسه کتاب (ISBN) را وارد کنید: "); String isbn = scanner.nextLine().trim();
        Book b = books.get(isbn);
        if (b==null) { System.out.println("کتاب یافت نشد."); return; }
        System.out.print("تاریخ شروع (YYYY-MM-DD): "); String ds = scanner.nextLine().trim();
        System.out.print("تاریخ پایان (YYYY-MM-DD): "); String de = scanner.nextLine().trim();
        try {
            LocalDate start = LocalDate.parse(ds);
            LocalDate end = LocalDate.parse(de);
            LoanRequest req = new LoanRequest(s, b, start, end);
            loanRequests.add(req);
            System.out.println("درخواست ثبت شد. منتظر تایید کارمند باشید.");
        } catch (Exception e) { System.out.println("فرمت تاریخ نادرست است."); }
    }

    private void employeeMenuCLI() {
        System.out.println("\n--- ورود کارمند ---");
        System.out.print("نام کاربری: "); String user = scanner.nextLine().trim();
        System.out.print("رمز عبور: "); String pass = scanner.nextLine().trim();
        Employee emp = employees.get(user);
        if (emp==null || !emp.password.equals(pass)) { System.out.println("اطلاعات ورود نامعتبر"); return; }
        System.out.println("ورود موفق: " + emp.name);
        while (true) {
            System.out.println("\n--- منوی کارمند ---");
            System.out.println("1) تغییر رمز\n2) ثبت کتاب\n3) جستجو و ویرایش کتاب\n4) بررسی و تایید درخواست‌ها\n5) مشاهده تاریخچه امانات دانشجو\n6) فعال/غیرفعال کردن دانشجو\n7) دریافت کتاب و ثبت زمان دریافت\n8) خروج");
            System.out.print("انتخاب: "); String c = scanner.nextLine().trim();
            switch (c) {
                case "1":
                    System.out.print("رمز جدید: "); String np = scanner.nextLine().trim(); emp.password = np; System.out.println("تغییر رمز انجام شد."); break;
                case "2": registerBookCLI(emp); break;
                case "3": editBookCLI(emp); break;
                case "4": approveRequestsCLI(emp); break;
                case "5": studentHistoryCLI(); break;
                case "6": toggleStudentActiveCLI(); break;
                case "7": receiveBookCLI(emp); break;
                case "8": return;
                default: System.out.println("نامعتبر");
            }
        }
    }

    private void registerBookCLI(Employee emp) {
        System.out.print("ISBN: "); String isbn = scanner.nextLine().trim();
        if (books.containsKey(isbn)) { System.out.println("کتاب با این ISBN موجود است."); return; }
        System.out.print("عنوان: "); String title = scanner.nextLine();
        System.out.print("نویسنده: "); String author = scanner.nextLine();
        System.out.print("سال نشر: "); int year = Integer.parseInt(scanner.nextLine().trim());
        Book b = new Book(isbn, title, author, year);
        addBook(b);
        emp.registeredBooks++;
        System.out.println("کتاب ثبت شد.");
    }

    private void editBookCLI(Employee emp) {
        System.out.print("ISBN یا بخشی از عنوان: "); String q = scanner.nextLine();
        List<Book> res = searchBooks(q);
        if (res.isEmpty()) { System.out.println("پیدا نشد"); return; }
        for (int i=0;i<res.size();i++) System.out.println((i+1)+") " + res.get(i).brief());
        System.out.print("شماره مورد نظر را وارد کنید: "); int idx = Integer.parseInt(scanner.nextLine().trim())-1;
        if (idx<0 || idx>=res.size()) { System.out.println("نامعتبر"); return; }
        Book b = res.get(idx);
        System.out.print("عنوان جدید (خالی برای بدون تغییر): "); String nt = scanner.nextLine(); if (!nt.isEmpty()) b.title = nt;
        System.out.print("نویسنده جدید (خالی برای بدون تغییر): "); String na = scanner.nextLine(); if (!na.isEmpty()) b.author = na;
        System.out.print("سال نشر جدید (خالی برای بدون تغییر): "); String ny = scanner.nextLine(); if (!ny.isEmpty()) b.year = Integer.parseInt(ny);
        System.out.println("ویرایش انجام شد.");
    }

    private void approveRequestsCLI(Employee emp) {
        System.out.println("درخواست‌هایی که تاریخ شروع آنها امروز یا دیروز است:");
        LocalDate today = LocalDate.now();
        List<LoanRequest> candidates = new ArrayList<>();
        for (LoanRequest r: loanRequests) {
            if (!r.approved && ( !r.start.isAfter(today) && !r.start.isBefore(today.minusDays(1)) )) {
                candidates.add(r);
            }
        }
        if (candidates.isEmpty()) { System.out.println("درخواست جدیدی برای امروز/دیروز وجود ندارد."); return; }
        for (int i=0;i<candidates.size();i++) System.out.println((i+1)+") " + candidates.get(i).brief());
        System.out.print(" شماره درخواست برای تایید (یا 0 برای لغو): "); int sel = Integer.parseInt(scanner.nextLine().trim());
        if (sel<=0 || sel>candidates.size()) return;
        LoanRequest chosen = candidates.get(sel-1);
        chosen.approved = true;
        chosen.approvedBy = emp;
        emp.loansGiven++;
        LoanRecord record = new LoanRecord(chosen.student, chosen.book, chosen.start, chosen.end, emp);
        loanRecords.add(record);
        System.out.println("درخواست تایید شد. دانشجو می‌تواند کتاب را تحویل بگیرد.");
    }

    private void studentHistoryCLI() {
        System.out.print("نام کاربری دانشجو: "); String u = scanner.nextLine().trim();
        Student s = students.get(u);
        if (s==null) { System.out.println("دانشجو یافت نشد."); return; }
        long total = loanRecords.stream().filter(r->r.student.username.equals(u)).count();
        long notReturned = loanRecords.stream().filter(r->r.student.username.equals(u) && !r.returned).count();
        long delayed = loanRecords.stream().filter(r->r.student.username.equals(u) && r.returned && r.delayed()).count();
        System.out.println("تعداد کل امانات: " + total);
        System.out.println("تعداد کتاب‌های تحویل داده نشده: " + notReturned);
        System.out.println("تعداد امانت‌های با تاخیر تحویل داده شده: " + delayed);
        loanRecords.stream().filter(r->r.student.username.equals(u)).forEach(r->System.out.println(r.toString()));
    }

    private void toggleStudentActiveCLI() {
        System.out.print("نام کاربری دانشجو: "); String u = scanner.nextLine().trim();
        Student s = students.get(u);
        if (s==null) { System.out.println("نیست"); return; }
        s.active = !s.active;
        System.out.println("وضعیت فعلی: " + (s.active?"فعال":"غیرفعال"));
    }

    private void receiveBookCLI(Employee emp) {
        System.out.print("ISBN کتاب دریافتی: "); String isbn = scanner.nextLine().trim();
        Optional<LoanRecord> opt = loanRecords.stream().filter(r->r.book.isbn.equals(isbn) && !r.returned).findFirst();
        if (!opt.isPresent()) { System.out.println("هیچ امانت باز با این ISBN پیدا نشد."); return; }
        LoanRecord rec = opt.get();
        rec.returned = true;
        rec.returnedAt = LocalDate.now();
        emp.booksReceived++;
        System.out.println("دریافت ثبت شد. تاریخ دریافت: " + rec.returnedAt);
    }

    private void adminMenuCLI() {
        System.out.println("\n--- منوی مدیر ---");
        System.out.print("نام کاربری: "); String u = scanner.nextLine().trim();
        System.out.print("رمز عبور: "); String p = scanner.nextLine().trim();
        if (!u.equals(adminUser) || !p.equals(adminPass)) { System.out.println("ورود ناموفق"); return; }
        while (true) {
            System.out.println("\n--- منوی مدیر ---");
            System.out.println("1) تعریف کارمند\n2) مشاهده عملکرد کارمند\n3) آمار امانات\n4) آمار دانشجویان\n5) خروج");
            System.out.print("انتخاب: "); String c = scanner.nextLine().trim();
            switch (c) {
                case "1": createEmployeeCLI(); break;
                case "2": viewEmployeePerformanceCLI(); break;
                case "3": loansStatsCLI(); break;
                case "4": studentsStatsCLI(); break;
                case "5": return;
                default: break;
            }
        }
    }

    private void createEmployeeCLI() {
        System.out.print("نام کاربری کارمند: "); String u = scanner.nextLine().trim();
        System.out.print("رمز عبور: "); String p = scanner.nextLine().trim();
        System.out.print("نام: "); String n = scanner.nextLine().trim();
        employees.put(u, new Employee(u,p,n));
        System.out.println("کارمند اضافه شد.");
    }

    private void viewEmployeePerformanceCLI() {
        System.out.print("نام کاربری کارمند: "); String u = scanner.nextLine().trim();
        Employee e = employees.get(u);
        if (e==null) { System.out.println("نیست"); return; }
        System.out.println("تعداد کتاب‌های ثبت شده: " + e.registeredBooks);
        System.out.println("تعداد کل کتاب‌هایی که امانت داده شده: " + e.loansGiven);
        System.out.println("تعداد کل کتاب‌هایی که تحویل گرفته: " + e.booksReceived);
    }

    private void loansStatsCLI() {
        System.out.println("تعداد درخواست‌های ثبت شده: " + loanRequests.size());
        System.out.println("تعداد کل امانت داده شده: " + loanRecords.size());
        double avgDays = loanRecords.stream().filter(r->r.returned).mapToLong(LoanRecord::durationDays).average().orElse(0.0);
        System.out.println("میانگین تعداد روزهای امانت (برای بازگشت شده‌ها): " + String.format("%.2f", avgDays));
    }

    private void studentsStatsCLI() {
        System.out.println("آمار برای همه دانشجویان:");
        for (Student s: students.values()) {
            long total = loanRecords.stream().filter(r->r.student.username.equals(s.username)).count();
            long notReturned = loanRecords.stream().filter(r->r.student.username.equals(s.username) && !r.returned).count();
            long delayed = loanRecords.stream().filter(r->r.student.username.equals(s.username) && r.returned && r.delayed()).count();
            System.out.println(String.format("%s: total=%d, notReturned=%d, delayed=%d", s.username, total, notReturned, delayed));
        }
        System.out.println("10 دانشجوی با بیشترین تاخیر:");
        Map<String, Long> delayedCount = new HashMap<>();
        for (LoanRecord r: loanRecords) if (r.returned && r.delayed()) delayedCount.put(r.student.username, delayedCount.getOrDefault(r.student.username,0L)+1);
        delayedCount.entrySet().stream().sorted((a,b)->Long.compare(b.getValue(), a.getValue())).limit(10).forEach(e-> System.out.println(e.getKey()+" -> " + e.getValue()));
    }

    // --------------------- Helpers & Model ops ---------------------
    // این متدها public هستند تا GUI نیز بتواند از آنها استفاده کند.
    public void addBook(Book b) { books.put(b.isbn, b); }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> res = new ArrayList<>();
        for (Book b: books.values()) if (b.title.toLowerCase().contains(title.toLowerCase())) res.add(b);
        return res;
    }

    public List<Book> searchBooks(String q) {
        List<Book> res = new ArrayList<>();
        for (Book b: books.values()) {
            if (b.title.toLowerCase().contains(q.toLowerCase()) || b.author.toLowerCase().contains(q.toLowerCase())) res.add(b);
            else {
                try { if (Integer.toString(b.year).equals(q)) res.add(b); } catch (Exception ignored) {}
            }
        }
        return res;
    }

    private void printSimpleStats() {
        long currentlyLoaned = loanRecords.stream().filter(r->!r.returned).count();
        System.out.println("تعداد کل دانشجویان: " + students.size());
        System.out.println("تعداد کل کتاب‌ها: " + books.size());
        System.out.println("تعداد کل امانت‌ها: " + loanRecords.size());
        System.out.println("تعداد کتاب‌هایی که هم‌اکنون امانت هستند: " + currentlyLoaned);
    }

    // --------------------- Models ---------------------
    static class Student {
        String username;
        String password;
        String name;
        String email;
        boolean active = true;
        public Student(String username, String password, String name, String email) { this.username = username; this.password = password; this.name = name; this.email = email; }
    }

    static class Employee {
        String username;
        String password;
        String name;
        int registeredBooks = 0;
        int loansGiven = 0;
        int booksReceived = 0;
        public Employee(String username, String password, String name) { this.username = username; this.password = password; this.name = name; }
    }

    static class Book {
        String isbn;
        String title;
        String author;
        int year;
        public Book(String isbn, String title, String author, int year) { this.isbn = isbn; this.title = title; this.author = author; this.year = year; }
        public String brief() { return String.format("%s - %s (%d)", isbn, title, year); }
        public String full() { return String.format("%s | %s | %s | %d", isbn, title, author, year); }
    }

    static class LoanRequest {
        Student student;
        Book book;
        LocalDate start;
        LocalDate end;
        boolean approved = false;
        Employee approvedBy = null;
        public LoanRequest(Student student, Book book, LocalDate start, LocalDate end) { this.student = student; this.book = book; this.start = start; this.end = end; }
        public String brief() { return String.format("%s requested %s from %s to %s (approved=%s)", student.username, book.isbn, start, end, approved); }
    }

    static class LoanRecord {
        Student student;
        Book book;
        LocalDate start;
        LocalDate end;
        Employee givenBy;
        boolean returned = false;
        LocalDate returnedAt = null;
        public LoanRecord(Student s, Book b, LocalDate start, LocalDate end, Employee givenBy) { this.student = s; this.book = b; this.start = start; this.end = end; this.givenBy = givenBy; }
        public String toString() {
            return String.format("%s | %s | %s -> %s | returned=%s %s", student.username, book.isbn, start, end, returned, (returned? "at "+returnedAt : ""));
        }
        public boolean delayed() {
            if (!returned || returnedAt==null) return false;
            return returnedAt.isAfter(end);
        }
        public long durationDays() { if (!returned || returnedAt==null) return 0; return ChronoUnit.DAYS.between(start, returnedAt); }
    }
}

// --------------------- Simple Swing UI class ---------------------
class LibraryGUI extends JFrame {
    private final LibrarySystem sys;
    public LibraryGUI(LibrarySystem sys) {
        super("Library System - GUI");
        this.sys = sys;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700,450);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("سیستم مدیریت کتابخانه - رابط کاربری ساده", SwingConstants.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(2,2,10,10));
        JButton guestBtn = new JButton("مهمان");
        JButton studentBtn = new JButton("دانشجو");
        JButton empBtn = new JButton("کارمند");
        JButton adminBtn = new JButton("مدیر");
        panel.add(guestBtn); panel.add(studentBtn); panel.add(empBtn); panel.add(adminBtn);
        add(panel, BorderLayout.CENTER);

        guestBtn.addActionListener(e -> guestDialog());
        studentBtn.addActionListener(e -> studentDialog());
        empBtn.addActionListener(e -> employeeLoginDialog());
        adminBtn.addActionListener(e -> adminLoginDialog());

        setVisible(true);
    }

    private void guestDialog() {
        String[] options = {"تعداد دانشجویان","جستجوی نام کتاب","آمار ساده","بستن"};
        int sel = JOptionPane.showOptionDialog(this, "منوی مهمان", "مهمان", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        switch (sel) {
            case 0: JOptionPane.showMessageDialog(this, "تعداد دانشجویان: " + sys.students.size()); break;
            case 1: {
                String q = JOptionPane.showInputDialog(this, "نام کتاب را وارد کنید:");
                if (q!=null) {
                    java.util.List<LibrarySystem.Book> res = sys.searchBooksByTitle(q);
                    StringBuilder sb = new StringBuilder();
                    for (LibrarySystem.Book b: res) sb.append(b.full()).append("\n");
                    JOptionPane.showMessageDialog(this, sb.length()==0?"نتیجه‌ای نیست":sb.toString());
                }
            } break;
            case 2: {
                StringBuilder sb = new StringBuilder();
                long currLoaned = sys.loanRecords.stream().filter(r->!r.returned).count();
                sb.append("تعداد کل دانشجویان: ").append(sys.students.size()).append("\n");
                sb.append("تعداد کل کتاب‌ها: ").append(sys.books.size()).append("\n");
                sb.append("تعداد کل امانت‌ها: ").append(sys.loanRecords.size()).append("\n");
                sb.append("تعداد امانت‌های فعال: ").append(currLoaned).append("\n");
                JOptionPane.showMessageDialog(this, sb.toString());
            } break;
        }
    }

    private void studentDialog() {
        String[] options = {"ثبت‌نام","ورود","بستن"};
        int sel = JOptionPane.showOptionDialog(this, "منوی دانشجو", "دانشجو", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (sel==0) {
            String user = JOptionPane.showInputDialog(this, "نام کاربری:"); if (user==null) return;
            if (sys.students.containsKey(user)) { JOptionPane.showMessageDialog(this, "نام کاربری موجود است"); return; }
            String pass = JOptionPane.showInputDialog(this, "رمز عبور:"); if (pass==null) return;
            String name = JOptionPane.showInputDialog(this, "نام:"); if (name==null) return;
            String email = JOptionPane.showInputDialog(this, "ایمیل:"); if (email==null) return;
            sys.students.put(user, new LibrarySystem.Student(user, pass, name, email));
            JOptionPane.showMessageDialog(this, "ثبت‌نام انجام شد");
        } else if (sel==1) {
            String user = JOptionPane.showInputDialog(this, "نام کاربری:"); if (user==null) return;
            String pass = JOptionPane.showInputDialog(this, "رمز عبور:"); if (pass==null) return;
            LibrarySystem.Student s = sys.students.get(user);
            if (s==null || !s.password.equals(pass)) { JOptionPane.showMessageDialog(this, "نام کاربری یا رمز اشتباه"); return; }
            // after login: allow search and request
            String[] ops = {"جستجو","درخواست امانت","مشاهده تاریخچه","خروج"};
            while (true) {
                int o = JOptionPane.showOptionDialog(this, "خوش آمدی " + s.name, "دانشجو", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
                if (o==0) {
                    String q = JOptionPane.showInputDialog(this, "جستجو (عنوان/نویسنده/سال):"); if (q==null) continue;
                    java.util.List<LibrarySystem.Book> res = sys.searchBooks(q);
                    StringBuilder sb = new StringBuilder(); for (LibrarySystem.Book b: res) sb.append(b.full()).append("\n");
                    JOptionPane.showMessageDialog(this, sb.length()==0?"نتیجه‌ای نیست":sb.toString());
                } else if (o==1) {
                    String isbn = JOptionPane.showInputDialog(this, "ISBN کتاب:"); if (isbn==null) continue;
                    LibrarySystem.Book b = sys.books.get(isbn);
                    if (b==null) { JOptionPane.showMessageDialog(this, "پیدا نشد"); continue; }
                    String ds = JOptionPane.showInputDialog(this, "تاریخ شروع (YYYY-MM-DD):"); if (ds==null) continue;
                    String de = JOptionPane.showInputDialog(this, "تاریخ پایان (YYYY-MM-DD):"); if (de==null) continue;
                    try {
                        java.time.LocalDate start = java.time.LocalDate.parse(ds);
                        java.time.LocalDate end = java.time.LocalDate.parse(de);
                        LibrarySystem.LoanRequest lr = new LibrarySystem.LoanRequest(s, b, start, end);
                        sys.loanRequests.add(lr);
                        JOptionPane.showMessageDialog(this, "درخواست ثبت شد");
                    } catch (Exception ex) { JOptionPane.showMessageDialog(this, "فرمت تاریخ نادرست"); }
                } else if (o==2) {
                    StringBuilder sb = new StringBuilder();
                    sys.loanRecords.stream().filter(r->r.student.username.equals(s.username)).forEach(r->sb.append(r.toString()).append("\n"));
                    JOptionPane.showMessageDialog(this, sb.length()==0?"تاریخچه‌ای نیست":sb.toString());
                } else break;
            }
        }
    }

    private void employeeLoginDialog() {
        String user = JOptionPane.showInputDialog(this, "نام کاربری کارمند:"); if (user==null) return;
        String pass = JOptionPane.showInputDialog(this, "رمز عبور:"); if (pass==null) return;
        LibrarySystem.Employee emp = sys.employees.get(user);
        if (emp==null || !emp.password.equals(pass)) { JOptionPane.showMessageDialog(this, "نام کاربری یا رمز اشتباه"); return; }
        String[] ops = {"ثبت کتاب","تایید درخواست‌ها","ثبت دریافت کتاب","فعال/غیرفعال دانشجو","بازگشت"};
        while (true) {
            int o = JOptionPane.showOptionDialog(this, "خوش آمدی " + emp.name, "کارمند", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
            if (o==0) {
                String isbn = JOptionPane.showInputDialog(this, "ISBN:"); if (isbn==null) continue;
                if (sys.books.containsKey(isbn)) { JOptionPane.showMessageDialog(this, "موجود است"); continue; }
                String title = JOptionPane.showInputDialog(this, "عنوان:"); if (title==null) continue;
                String author = JOptionPane.showInputDialog(this, "نویسنده:"); if (author==null) continue;
                String y = JOptionPane.showInputDialog(this, "سال:"); if (y==null) continue;
                try { int year = Integer.parseInt(y); sys.addBook(new LibrarySystem.Book(isbn,title,author,year)); emp.registeredBooks++; JOptionPane.showMessageDialog(this, "ثبت شد"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this, "سال نامعتبر"); }
            } else if (o==1) {
                java.util.List<LibrarySystem.LoanRequest> candidates = new ArrayList<>();
                java.time.LocalDate today = java.time.LocalDate.now();
                for (LibrarySystem.LoanRequest r: sys.loanRequests) if (!r.approved && (!r.start.isAfter(today) && !r.start.isBefore(today.minusDays(1)))) candidates.add(r);
                if (candidates.isEmpty()) { JOptionPane.showMessageDialog(this, "هیچ درخواستی برای امروز/دیروز نیست"); continue; }
                StringBuilder sb = new StringBuilder(); for (int i=0;i<candidates.size();i++) sb.append(i+1).append(") ").append(candidates.get(i).brief()).append("\n");
                String sel = JOptionPane.showInputDialog(this, sb.toString()+"\nشماره برای تایید:"); if (sel==null) continue;
                try { int s = Integer.parseInt(sel)-1; if (s<0||s>=candidates.size()) continue; LibrarySystem.LoanRequest chosen = candidates.get(s); chosen.approved=true; chosen.approvedBy=emp; emp.loansGiven++; sys.loanRecords.add(new LibrarySystem.LoanRecord(chosen.student, chosen.book, chosen.start, chosen.end, emp)); JOptionPane.showMessageDialog(this, "تایید شد"); }
                catch (Exception ex) { }
            } else if (o==2) {
                String isbn = JOptionPane.showInputDialog(this, "ISBN دریافتی:"); if (isbn==null) continue;
                Optional<LibrarySystem.LoanRecord> opt = sys.loanRecords.stream().filter(r->r.book.isbn.equals(isbn) && !r.returned).findFirst();
                if (!opt.isPresent()) { JOptionPane.showMessageDialog(this, "هیچ امانت باز یافت نشد"); continue; }
                LibrarySystem.LoanRecord rec = opt.get(); rec.returned=true; rec.returnedAt=java.time.LocalDate.now(); emp.booksReceived++; JOptionPane.showMessageDialog(this, "ثبت شد: " + rec.returnedAt);
            } else if (o==3) {
                String u = JOptionPane.showInputDialog(this, "نام کاربری دانشجو برای تغییر وضعیت:"); if (u==null) continue; LibrarySystem.Student st = sys.students.get(u); if (st==null) { JOptionPane.showMessageDialog(this, "نیست"); continue; } st.active=!st.active; JOptionPane.showMessageDialog(this, "حالت جدید: " + (st.active?"فعال":"غیرفعال"));
            } else break;
        }
    }

    private void adminLoginDialog() {
        String u = JOptionPane.showInputDialog(this, "نام کاربری مدیر:"); if (u==null) return;
        String p = JOptionPane.showInputDialog(this, "رمز عبور:"); if (p==null) return;
        if (!u.equals(sys.adminUser) || !p.equals(sys.adminPass)) { JOptionPane.showMessageDialog(this, "نام کاربری یا رمز اشتباه"); return; }
        String[] ops = {"تعریف کارمند","عملکرد کارمند","آمار امانات","آمار دانشجویان","بستن"};
        while (true) {
            int o = JOptionPane.showOptionDialog(this, "مدیر", "مدیر", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
            if (o==0) {
                String user = JOptionPane.showInputDialog(this, "نام کاربری کارمند:"); if (user==null) continue; String pass = JOptionPane.showInputDialog(this, "رمز عبور:"); if (pass==null) continue; String name = JOptionPane.showInputDialog(this, "نام:"); if (name==null) continue; sys.employees.put(user, new LibrarySystem.Employee(user,pass,name)); JOptionPane.showMessageDialog(this, "اضافه شد");
            } else if (o==1) {
                String user = JOptionPane.showInputDialog(this, "نام کاربری کارمند:"); if (user==null) continue; LibrarySystem.Employee e = sys.employees.get(user); if (e==null) { JOptionPane.showMessageDialog(this, "نیست"); continue; } JOptionPane.showMessageDialog(this, String.format("ثبت‌شده: %d\nوام‌های صادرشده: %d\nواپس‌گیری‌ها: %d", e.registeredBooks, e.loansGiven, e.booksReceived));
            } else if (o==2) {
                StringBuilder sb = new StringBuilder(); sb.append("درخواست‌ها: ").append(sys.loanRequests.size()).append("\n"); sb.append("کل امانت‌ها: ").append(sys.loanRecords.size()).append("\n"); double avg = sys.loanRecords.stream().filter(r->r.returned).mapToLong(LibrarySystem.LoanRecord::durationDays).average().orElse(0.0); sb.append("میانگین روزها: ").append(String.format("%.2f", avg)); JOptionPane.showMessageDialog(this, sb.toString());
            } else if (o==3) {
                StringBuilder sb = new StringBuilder();
                Map<String, Long> delayedCount = new HashMap<>();
                for (LibrarySystem.LoanRecord r: sys.loanRecords) if (r.returned && r.delayed()) delayedCount.put(r.student.username, delayedCount.getOrDefault(r.student.username,0L)+1);
                delayedCount.entrySet().stream().sorted((a,b)->Long.compare(b.getValue(), a.getValue())).limit(10).forEach(e->sb.append(e.getKey()).append(" -> ").append(e.getValue()).append("\n"));
                JOptionPane.showMessageDialog(this, sb.length()==0?"بدون مورد":sb.toString());
            } else break;
        }
    }
}
