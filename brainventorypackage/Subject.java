package brainventorypackage;

// Subject: simple data holder for a course (name + optional description)

public class Subject {
    String name;
    String description;

    public Subject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
