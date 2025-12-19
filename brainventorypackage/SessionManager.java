package brainventorypackage;

// SessionManager: provides an in-memory repository of study sessions
// and persists them to disk at `brainventory/data.json`.
// It offers simple query helpers used by UI components and report
// generators. This class implements `ISessionRepository` so callers
// can depend on the abstraction instead of the concrete type.

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SessionManager implements ISessionRepository {

    private static final String FILE_PATH = "brainventory/data.json";
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final List<Session> sessions = new ArrayList<>();

    public SessionManager() {
        loadSessions();
    }

    // Load persisted sessions into memory on construction so callers
    // can read and aggregate sessions without repeatedly hitting disk.

    private void ensureDataDir() {
        File f = new File(FILE_PATH);
        File p = f.getParentFile();
        if (p != null && !p.exists()) p.mkdirs();
    }

    // ===== LOAD =====
    private void loadSessions() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Session>>() {}.getType();
            List<Session> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                sessions.clear();
                sessions.addAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("[SessionManager] Error loading sessions: " + e.getMessage());
        }
    }

    // Reload sessions from disk (useful when external processes modify data)
    public synchronized void reloadSessions() {
        loadSessions();
    }

    // ===== SAVE =====
    public synchronized void saveSession(Session session) {
        sessions.add(session);
        ensureDataDir();
        File file = new File(FILE_PATH);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(sessions, writer);
            writer.flush();
            System.err.println("[SessionManager] Saved session to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("[SessionManager] Error saving sessions: " + e.getMessage());
        }
    }

    public List<Session> getSessions() {
        return new ArrayList<>(sessions);
    }

    public List<Session> getSessionsBySubject(String subject) {
        List<Session> out = new ArrayList<>();
        for (Session s : sessions) {
            if (subject == null) continue;
            if (subject.equals(s.course)) out.add(s);
        }
        return out;
    }

    public Map<String, Integer> totalSecondsBySubject() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Session s : sessions) {
            String subj = (s.course == null || s.course.isEmpty()) ? "<Unassigned>" : s.course;
            map.put(subj, map.getOrDefault(subj, 0) + s.durationSeconds);
        }
        return map;
    }

    // optional helper: parse start time
    public LocalDateTime parseStart(Session s) {
        try {
            return LocalDateTime.parse(s.startTime, fmt);
        } catch (Exception ex) {
            return null;
        }
    }
}
