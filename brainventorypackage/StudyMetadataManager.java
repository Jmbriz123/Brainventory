package brainventorypackage;

// StudyMetadataManager: stores simple metadata (productivity/energy)
// keyed by a session identifier (start|end). This metadata is used by
// ReportGenerator to compute average productivity/energy per subject.

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class StudyMetadataManager {
    private static final String FILE_PATH = "brainventory/study_metadata.json";
    private final Gson gson = new Gson();

    // key -> metadata where key is sessionKey (start|end)
    private Map<String, Metadata> map = new HashMap<>();

    public static class Metadata {
        public int productivity; // 1-5
        public int energy; // 1-5

        public Metadata() {}

        public Metadata(int productivity, int energy) {
            this.productivity = productivity;
            this.energy = energy;
        }
    }

    public StudyMetadataManager() {
        load();
    }

    // Constructor initializes metadata map from disk so callers can query

    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            map = new HashMap<>();
            return;
        }

        try (Reader r = new FileReader(file)) {
            Type t = new TypeToken<HashMap<String, Metadata>>(){}.getType();
            map = gson.fromJson(r, t);
            if (map == null) map = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            map = new HashMap<>();
        }
    }

    private void save() {
        try (Writer w = new FileWriter(FILE_PATH)) {
            gson.toJson(map, w);
            System.out.println("[StudyMetadataManager] Saved metadata to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("[StudyMetadataManager] Error saving metadata to " + FILE_PATH);
            e.printStackTrace();
        }
    }

    public void putMetadata(String sessionKey, Metadata m) {
        map.put(sessionKey, m);
        save();
    }

    public Metadata getMetadata(String sessionKey) {
        return map.get(sessionKey);
    }

    public Map<String, Metadata> getAll() {
        return map;
    }

    
}
