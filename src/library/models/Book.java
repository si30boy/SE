package library.models;

public class Book {
    public String isbn;
    public String title;
    public String author;
    public int year;

    public Book(String isbn, String title, String author, int year) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String brief() {
        return String.format("%s - %s (%d)", isbn, title, year);
    }

    public String full() {
        return String.format("%s | %s | %s | %d", isbn, title, author, year);
    }
}

