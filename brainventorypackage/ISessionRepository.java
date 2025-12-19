package brainventorypackage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// ISessionRepository: abstraction over session persistence used to
// decouple consumers from the concrete SessionManager implementation.
public interface ISessionRepository {
    List<Session> getSessions();
    List<Session> getSessionsBySubject(String subject);
    Map<String, Integer> totalSecondsBySubject();
    void saveSession(Session session);
    void reloadSessions();
    LocalDateTime parseStart(Session s);
}
