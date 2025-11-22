package library.models;

import java.time.LocalDate;

public class LoanRequest {
    public Student student;
    public Book book;
    public LocalDate start;
    public LocalDate end;
    public boolean approved = false;
    public Employee approvedBy = null;

    public LoanRequest(Student student, Book book, LocalDate start, LocalDate end) {
        this.student = student;
        this.book = book;
        this.start = start;
        this.end = end;
    }

    public String brief() {
        return String.format("%s requested %s from %s to %s (approved=%s)",
                student.username, book.isbn, start, end, approved);
    }
}

