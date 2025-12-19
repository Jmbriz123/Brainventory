package brainventorypackage;

// SubjectManager: simple in-memory manager for subjects (courses).
// Provides add/remove/list operations persisted to `subjects.json`.

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;

public class SubjectManager {

    private static final String FILE_PATH = "brainventory/subjects.json";
    private final Gson gson = new Gson();
    private ArrayList<Subject> subjects;

    public SubjectManager() {
        loadSubjects();
    }

    private void loadSubjects() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            subjects = new ArrayList<>();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Subject>>() {}.getType();
            subjects = gson.fromJson(reader, listType);
            if (subjects == null) subjects = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            subjects = new ArrayList<>();
        }
    }

    private void saveAll() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(subjects, writer);
            System.out.println("[SubjectManager] Saved subjects to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("[SubjectManager] Error saving subjects to " + FILE_PATH);
            e.printStackTrace();
        }
    }

    public void addSubject(Subject s) {
        subjects.add(s);
        saveAll();
    }

    public void removeSubject(String name) {
        subjects.removeIf(s -> s.name.equalsIgnoreCase(name));
        saveAll();
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public Optional<Subject> findByName(String name) {
        return subjects.stream().filter(s -> s.name.equalsIgnoreCase(name)).findFirst();
    }
}
