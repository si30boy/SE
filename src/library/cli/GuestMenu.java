package library.cli;

import library.LibrarySystem;
import library.models.Book;
import java.util.List;
import java.util.Scanner;

class GuestMenu {
    private final LibrarySystem system;
    private final Scanner scanner;

    GuestMenu(LibrarySystem system, Scanner scanner) {
        this.system = system;
        this.scanner = scanner;
    }

    void showMenu() {
        System.out.println("\n--- منوی مهمان ---");
        System.out.println("1) مشاهده تعداد دانشجویان ثبت‌نام شده");
        System.out.println("2) جستجو بر اساس نام کتاب (فقط اطلاعات کتاب)");
        System.out.println("3) مشاهده آمار ساده");
        System.out.println("4) بازگشت");
        System.out.print("انتخاب: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.println("تعداد دانشجویان: " + system.students.size());
                break;
            case "2":
                handleBookSearch();
                break;
            case "3":
                system.printSimpleStats();
                break;
            default:
                break;
        }
    }

    private void handleBookSearch() {
        System.out.print("نام کتاب را وارد کنید: ");
        String title = scanner.nextLine();
        List<Book> result = system.searchBooksByTitle(title);
        if (result.isEmpty()) {
            System.out.println("نتیجه‌ای پیدا نشد.");
            return;
        }
        result.forEach(book -> System.out.println(book.brief()));
    }
}

