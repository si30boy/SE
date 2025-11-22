package library.models;

public class Student {
    public String username;
    public String password;
    public String name;
    public String email;
    public boolean active = true;

    public Student(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }
}

