package library.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoanRecord {
    public Student student;
    public Book book;
    public LocalDate start;
    public LocalDate end;
    public Employee givenBy;
    public boolean returned = false;
    public LocalDate returnedAt = null;

    public LoanRecord(Student student, Book book, LocalDate start, LocalDate end, Employee givenBy) {
        this.student = student;
        this.book = book;
        this.start = start;
        this.end = end;
        this.givenBy = givenBy;
    }

    public String toString() {
        return String.format("%s | %s | %s -> %s | returned=%s %s",
                student.username, book.isbn, start, end, returned,
                (returned ? "at " + returnedAt : ""));
    }

    public boolean delayed() {
        if (!returned || returnedAt == null) return false;
        return returnedAt.isAfter(end);
    }

    public long durationDays() {
        if (!returned || returnedAt == null) return 0;
        return ChronoUnit.DAYS.between(start, returnedAt);
    }
}

