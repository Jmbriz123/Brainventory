package brainventorypackage;

// ReportGenerator: builds JSON reports (daily, weekly, overall)
// from sessions supplied by an ISessionRepository and enriches
// summaries with metadata from StudyMetadataManager.

import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {
    private final ISessionRepository sessionRepository;
    private final StudyMetadataManager metadataManager;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Gson gson = new Gson();

    public static class SubjectSummary {
        public String subject;
        public int totalSeconds;
        public double totalHours() { return totalSeconds / 3600.0; }
        public double avgProductivity;
        public double avgEnergy;
    }

    public ReportGenerator(ISessionRepository sessionRepository, StudyMetadataManager metadataManager) {
        this.sessionRepository = sessionRepository;
        this.metadataManager = metadataManager;
    }

    private LocalDateTime parse(String ts) {
        return LocalDateTime.parse(ts, fmt);
    }

    // Parse timestamp strings stored in Session objects to LocalDateTime

    public List<Session> getSessionsBetween(LocalDateTime start, LocalDateTime end) {
        return sessionRepository.getSessions().stream().filter(s -> {
            try {
                LocalDateTime st = parse(s.startTime);
                return (st.isEqual(start) || st.isAfter(start)) && st.isBefore(end.plusSeconds(1));
            } catch (Exception ex) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    public List<Session> getSessionsForDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusSeconds(1);
        return getSessionsBetween(start, end);
    }

    public Map<String, SubjectSummary> summarizeBySubject(Collection<Session> sessions) {
        Map<String, SubjectSummary> map = new HashMap<>();

        for (Session s : sessions) {
            String subject = (s.course != null && !s.course.isEmpty()) ? s.course : "<Unassigned>";
            SubjectSummary summ = map.computeIfAbsent(subject, k -> {
                SubjectSummary ss = new SubjectSummary();
                ss.subject = k; ss.totalSeconds = 0; ss.avgProductivity = 0; ss.avgEnergy = 0; return ss; });
            summ.totalSeconds += s.durationSeconds;

            String key = s.startTime + "|" + s.endTime;
            StudyMetadataManager.Metadata m = metadataManager.getMetadata(key);
            if (m != null) {
                // compute running average
                double prevCount = summ.avgProductivity == 0 ? 0 : 1; // we will average properly below
                summ.avgProductivity += m.productivity;
                summ.avgEnergy += m.energy;
            }
        }

        // convert sums to averages
        // We need counts per subject for metadata — compute separately
        Map<String, Integer> counts = new HashMap<>();
        for (Session s : sessions) {
            String subject = (s.course != null && !s.course.isEmpty()) ? s.course : "<Unassigned>";
            String key = s.startTime + "|" + s.endTime;
            StudyMetadataManager.Metadata m = metadataManager.getMetadata(key);
            if (m != null) {
                counts.put(subject, counts.getOrDefault(subject, 0) + 1);
            }
        }

        for (Map.Entry<String, SubjectSummary> e : map.entrySet()) {
            int cnt = counts.getOrDefault(e.getKey(), 0);
            if (cnt > 0) {
                e.getValue().avgProductivity = e.getValue().avgProductivity / cnt;
                e.getValue().avgEnergy = e.getValue().avgEnergy / cnt;
            } else {
                e.getValue().avgProductivity = 0;
                e.getValue().avgEnergy = 0;
            }
        }

        return map;
    }

    public String dailyReport(LocalDate date) {
        List<Session> sessions = getSessionsForDate(date);
        Map<String, SubjectSummary> map = summarizeBySubject(sessions);
        Map<String, Object> out = new HashMap<>();
        out.put("date", date.toString());
        out.put("totalSessions", sessions.size());
        out.put("totalSeconds", sessions.stream().mapToInt(s -> s.durationSeconds).sum());
        out.put("subjects", map.values());
        return gson.toJson(out);
    }

    public String weeklyReport(LocalDate anyDateInWeek) {
        LocalDate start = anyDateInWeek.with(java.time.DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);
        LocalDateTime sdt = start.atStartOfDay();
        LocalDateTime edt = end.plusDays(1).atStartOfDay().minusSeconds(1);
        List<Session> sessions = getSessionsBetween(sdt, edt);
        Map<String, SubjectSummary> map = summarizeBySubject(sessions);
        Map<String, Object> out = new HashMap<>();
        out.put("weekStart", start.toString());
        out.put("weekEnd", end.toString());
        out.put("totalSessions", sessions.size());
        out.put("totalSeconds", sessions.stream().mapToInt(s -> s.durationSeconds).sum());
        out.put("subjects", map.values());
        return gson.toJson(out);
    }

    public String overallReport() {
        List<Session> sessions = sessionRepository.getSessions();
        Map<String, SubjectSummary> map = summarizeBySubject(sessions);
        Map<String, Object> out = new HashMap<>();
        out.put("totalSessions", sessions.size());
        out.put("totalSeconds", sessions.stream().mapToInt(s -> s.durationSeconds).sum());
        out.put("subjects", map.values());
        return gson.toJson(out);
    }
}
