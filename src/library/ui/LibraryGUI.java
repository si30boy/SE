package library.ui;

import library.LibrarySystem;
import library.models.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class LibraryGUI extends JFrame {
    private final LibrarySystem sys;

    public LibraryGUI(LibrarySystem sys) {
        super("Library System - GUI");
        this.sys = sys;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("سیستم مدیریت کتابخانه - رابط کاربری ساده", SwingConstants.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton guestBtn = new JButton("مهمان");
        JButton studentBtn = new JButton("دانشجو");
        JButton empBtn = new JButton("کارمند");
        JButton adminBtn = new JButton("مدیر");
        panel.add(guestBtn);
        panel.add(studentBtn);
        panel.add(empBtn);
        panel.add(adminBtn);
        add(panel, BorderLayout.CENTER);

        guestBtn.addActionListener(e -> guestDialog());
        studentBtn.addActionListener(e -> studentDialog());
        empBtn.addActionListener(e -> employeeLoginDialog());
        adminBtn.addActionListener(e -> adminLoginDialog());

        setVisible(true);
    }

    private void guestDialog() {
        String[] options = {"تعداد دانشجویان", "جستجوی نام کتاب", "آمار ساده", "بستن"};
        int sel = JOptionPane.showOptionDialog(this, "منوی مهمان", "مهمان",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        switch (sel) {
            case 0:
                JOptionPane.showMessageDialog(this, "تعداد دانشجویان: " + sys.students.size());
                break;
            case 1: {
                String q = JOptionPane.showInputDialog(this, "نام کتاب را وارد کنید:");
                if (q != null) {
                    List<Book> res = sys.searchBooksByTitle(q);
                    StringBuilder sb = new StringBuilder();
                    for (Book b : res) sb.append(b.full()).append("\n");
                    JOptionPane.showMessageDialog(this, sb.length() == 0 ? "نتیجه‌ای نیست" : sb.toString());
                }
            }
            break;
            case 2: {
                StringBuilder sb = new StringBuilder();
                long currLoaned = sys.loanRecords.stream().filter(r -> !r.returned).count();
                sb.append("تعداد کل دانشجویان: ").append(sys.students.size()).append("\n");
                sb.append("تعداد کل کتاب‌ها: ").append(sys.books.size()).append("\n");
                sb.append("تعداد کل امانت‌ها: ").append(sys.loanRecords.size()).append("\n");
                sb.append("تعداد امانت‌های فعال: ").append(currLoaned).append("\n");
                JOptionPane.showMessageDialog(this, sb.toString());
            }
            break;
        }
    }

    private void studentDialog() {
        String[] options = {"ثبت‌نام", "ورود", "بستن"};
        int sel = JOptionPane.showOptionDialog(this, "منوی دانشجو", "دانشجو",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (sel == 0) {
            String user = JOptionPane.showInputDialog(this, "نام کاربری:");
            if (user == null) return;
            if (sys.students.containsKey(user)) {
                JOptionPane.showMessageDialog(this, "نام کاربری موجود است");
                return;
            }
            String pass = JOptionPane.showInputDialog(this, "رمز عبور:");
            if (pass == null) return;
            String name = JOptionPane.showInputDialog(this, "نام:");
            if (name == null) return;
            String email = JOptionPane.showInputDialog(this, "ایمیل:");
            if (email == null) return;
            sys.students.put(user, new Student(user, pass, name, email));
            JOptionPane.showMessageDialog(this, "ثبت‌نام انجام شد");
        } else if (sel == 1) {
            String user = JOptionPane.showInputDialog(this, "نام کاربری:");
            if (user == null) return;
            String pass = JOptionPane.showInputDialog(this, "رمز عبور:");
            if (pass == null) return;
            Student s = sys.students.get(user);
            if (s == null || !s.password.equals(pass)) {
                JOptionPane.showMessageDialog(this, "نام کاربری یا رمز اشتباه");
                return;
            }
            String[] ops = {"جستجو", "درخواست امانت", "مشاهده تاریخچه", "خروج"};
            while (true) {
                int o = JOptionPane.showOptionDialog(this, "خوش آمدی " + s.name, "دانشجو",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
                if (o == 0) {
                    String q = JOptionPane.showInputDialog(this, "جستجو (عنوان/نویسنده/سال):");
                    if (q == null) continue;
                    List<Book> res = sys.searchBooks(q);
                    StringBuilder sb = new StringBuilder();
                    for (Book b : res) sb.append(b.full()).append("\n");
                    JOptionPane.showMessageDialog(this, sb.length() == 0 ? "نتیجه‌ای نیست" : sb.toString());
                } else if (o == 1) {
                    String isbn = JOptionPane.showInputDialog(this, "ISBN کتاب:");
                    if (isbn == null) continue;
                    Book b = sys.books.get(isbn);
                    if (b == null) {
                        JOptionPane.showMessageDialog(this, "پیدا نشد");
                        continue;
                    }
                    String ds = JOptionPane.showInputDialog(this, "تاریخ شروع (YYYY-MM-DD):");
                    if (ds == null) continue;
                    String de = JOptionPane.showInputDialog(this, "تاریخ پایان (YYYY-MM-DD):");
                    if (de == null) continue;
                    try {
                        LocalDate start = LocalDate.parse(ds);
                        LocalDate end = LocalDate.parse(de);
                        LoanRequest lr = new LoanRequest(s, b, start, end);
                        sys.loanRequests.add(lr);
                        JOptionPane.showMessageDialog(this, "درخواست ثبت شد");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "فرمت تاریخ نادرست");
                    }
                } else if (o == 2) {
                    StringBuilder sb = new StringBuilder();
                    sys.loanRecords.stream()
                            .filter(r -> r.student.username.equals(s.username))
                            .forEach(r -> sb.append(r.toString()).append("\n"));
                    JOptionPane.showMessageDialog(this, sb.length() == 0 ? "تاریخچه‌ای نیست" : sb.toString());
                } else break;
            }
        }
    }

    private void employeeLoginDialog() {
        String user = JOptionPane.showInputDialog(this, "نام کاربری کارمند:");
        if (user == null) return;
        String pass = JOptionPane.showInputDialog(this, "رمز عبور:");
        if (pass == null) return;
        Employee emp = sys.employees.get(user);
        if (emp == null || !emp.password.equals(pass)) {
            JOptionPane.showMessageDialog(this, "نام کاربری یا رمز اشتباه");
            return;
        }
        String[] ops = {"ثبت کتاب", "تایید درخواست‌ها", "ثبت دریافت کتاب", "فعال/غیرفعال دانشجو", "بازگشت"};
        while (true) {
            int o = JOptionPane.showOptionDialog(this, "خوش آمدی " + emp.name, "کارمند",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
            if (o == 0) {
                String isbn = JOptionPane.showInputDialog(this, "ISBN:");
                if (isbn == null) continue;
                if (sys.books.containsKey(isbn)) {
                    JOptionPane.showMessageDialog(this, "موجود است");
                    continue;
                }
                String title = JOptionPane.showInputDialog(this, "عنوان:");
                if (title == null) continue;
                String author = JOptionPane.showInputDialog(this, "نویسنده:");
                if (author == null) continue;
                String y = JOptionPane.showInputDialog(this, "سال:");
                if (y == null) continue;
                try {
                    int year = Integer.parseInt(y);
                    sys.addBook(new Book(isbn, title, author, year));
                    emp.registeredBooks++;
                    JOptionPane.showMessageDialog(this, "ثبت شد");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "سال نامعتبر");
                }
            } else if (o == 1) {
                List<LoanRequest> candidates = new ArrayList<>();
                LocalDate today = LocalDate.now();
                for (LoanRequest r : sys.loanRequests) {
                    if (!r.approved && (!r.start.isAfter(today) && !r.start.isBefore(today.minusDays(1)))) {
                        candidates.add(r);
                    }
                }
                if (candidates.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "هیچ درخواستی برای امروز/دیروز نیست");
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < candidates.size(); i++) {
                    sb.append(i + 1).append(") ").append(candidates.get(i).brief()).append("\n");
                }
                String sel = JOptionPane.showInputDialog(this, sb.toString() + "\nشماره برای تایید:");
                if (sel == null) continue;
                try {
                    int s = Integer.parseInt(sel) - 1;
                    if (s < 0 || s >= candidates.size()) continue;
                    LoanRequest chosen = candidates.get(s);
                    chosen.approved = true;
                    chosen.approvedBy = emp;
                    emp.loansGiven++;
                    sys.loanRecords.add(new LoanRecord(chosen.student, chosen.book, chosen.start, chosen.end, emp));
                    JOptionPane.showMessageDialog(this, "تایید شد");
                } catch (Exception ex) {
                }
            } else if (o == 2) {
                String isbn = JOptionPane.showInputDialog(this, "ISBN دریافتی:");
                if (isbn == null) continue;
                Optional<LoanRecord> opt = sys.loanRecords.stream()
                        .filter(r -> r.book.isbn.equals(isbn) && !r.returned)
                        .findFirst();
                if (!opt.isPresent()) {
                    JOptionPane.showMessageDialog(this, "هیچ امانت باز یافت نشد");
                    continue;
                }
                LoanRecord rec = opt.get();
                rec.returned = true;
                rec.returnedAt = LocalDate.now();
                emp.booksReceived++;
                JOptionPane.showMessageDialog(this, "ثبت شد: " + rec.returnedAt);
            } else if (o == 3) {
                String u = JOptionPane.showInputDialog(this, "نام کاربری دانشجو برای تغییر وضعیت:");
                if (u == null) continue;
                Student st = sys.students.get(u);
                if (st == null) {
                    JOptionPane.showMessageDialog(this, "نیست");
                    continue;
                }
                st.active = !st.active;
                JOptionPane.showMessageDialog(this, "حالت جدید: " + (st.active ? "فعال" : "غیرفعال"));
            } else break;
        }
    }

    private void adminLoginDialog() {
        String u = JOptionPane.showInputDialog(this, "نام کاربری مدیر:");
        if (u == null) return;
        String p = JOptionPane.showInputDialog(this, "رمز عبور:");
        if (p == null) return;
        if (!u.equals(sys.adminUser) || !p.equals(sys.adminPass)) {
            JOptionPane.showMessageDialog(this, "نام کاربری یا رمز اشتباه");
            return;
        }
        String[] ops = {"تعریف کارمند", "عملکرد کارمند", "آمار امانات", "آمار دانشجویان", "بستن"};
        while (true) {
            int o = JOptionPane.showOptionDialog(this, "مدیر", "مدیر",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
            if (o == 0) {
                String user = JOptionPane.showInputDialog(this, "نام کاربری کارمند:");
                if (user == null) continue;
                String pass = JOptionPane.showInputDialog(this, "رمز عبور:");
                if (pass == null) continue;
                String name = JOptionPane.showInputDialog(this, "نام:");
                if (name == null) continue;
                sys.employees.put(user, new Employee(user, pass, name));
                JOptionPane.showMessageDialog(this, "اضافه شد");
            } else if (o == 1) {
                String user = JOptionPane.showInputDialog(this, "نام کاربری کارمند:");
                if (user == null) continue;
                Employee e = sys.employees.get(user);
                if (e == null) {
                    JOptionPane.showMessageDialog(this, "نیست");
                    continue;
                }
                JOptionPane.showMessageDialog(this, String.format("ثبت‌شده: %d\nوام‌های صادرشده: %d\nواپس‌گیری‌ها: %d",
                        e.registeredBooks, e.loansGiven, e.booksReceived));
            } else if (o == 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("درخواست‌ها: ").append(sys.loanRequests.size()).append("\n");
                sb.append("کل امانت‌ها: ").append(sys.loanRecords.size()).append("\n");
                double avg = sys.loanRecords.stream()
                        .filter(r -> r.returned)
                        .mapToLong(LoanRecord::durationDays)
                        .average()
                        .orElse(0.0);
                sb.append("میانگین روزها: ").append(String.format("%.2f", avg));
                JOptionPane.showMessageDialog(this, sb.toString());
            } else if (o == 3) {
                StringBuilder sb = new StringBuilder();
                Map<String, Long> delayedCount = new HashMap<>();
                for (LoanRecord r : sys.loanRecords) {
                    if (r.returned && r.delayed()) {
                        delayedCount.put(r.student.username, delayedCount.getOrDefault(r.student.username, 0L) + 1);
                    }
                }
                delayedCount.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .limit(10)
                        .forEach(e -> sb.append(e.getKey()).append(" -> ").append(e.getValue()).append("\n"));
                JOptionPane.showMessageDialog(this, sb.length() == 0 ? "بدون مورد" : sb.toString());
            } else break;
        }
    }
}

