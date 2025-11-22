package library.models;

public class Employee {
    public String username;
    public String password;
    public String name;
    public int registeredBooks = 0;
    public int loansGiven = 0;
    public int booksReceived = 0;

    public Employee(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
}

