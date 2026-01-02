# Brainventory - Study Session Tracker

A modern Java Swing application for tracking study sessions with Pomodoro timer, subject management, productivity metrics, and comprehensive reporting.

## ✨ Features Implemented

### 🕒 Study Session Logging
- **Pomodoro Timer**: 25-minute focus sessions, 5-minute breaks
- **Subject Assignment**: Assign each session to a subject/course
- **Productivity Level**: Rate 1-5 scale after each session
- **Energy Level**: Rate 1-5 scale after each session
- **Automatic Timestamping**: Start and end times recorded with date
- **Duration Tracking**: Exact seconds logged per session

**Files**:
- `PomodoroStudy.java` - Modern Pomodoro UI with session logging dialog
- `Session.java` - Data model (startTime, endTime, durationSeconds, course)

### 📘 Subject Management
- **Add Subjects**: Create new subjects/courses during study sessions
- **View Subjects**: Browse all registered subjects
- **Subject Persistence**: All subjects saved to `brainventory/subjects.json`
- **Automatic Synchronization**: New subjects available immediately in dropdown

**Files**:
- `Subject.java` - Subject model
- `SubjectManager.java` - Load/save subjects with Gson, add/remove/query operations

### 📊 Report Generation
- **Daily Report**: JSON summary of sessions for a selected date
  - Total sessions, total time, breakdown by subject
  - Average productivity and energy per subject
- **Weekly Report**: Summary for Monday-Sunday week
  - Week start/end dates
  - Aggregated metrics by subject
- **Overall Report**: Lifetime statistics
  - All sessions across all time
  - Subject summaries with totals
- **Subject Summary**: Quick view of hours per subject
  - Tab-separated format for easy reading

**Files**:
- `ReportGenerator.java` - Generates daily, weekly, overall reports with subject breakdowns
- `SessionsPanel.java` - UI with date picker and report buttons

### 💾 Data Persistence
All user data saved as JSON using Google Gson library:

1. **Sessions** → `brainventory/data.json`
   - List of all completed focus sessions
   - Each session: startTime, endTime, durationSeconds, course

2. **Metadata** → `brainventory/study_metadata.json`
   - Productivity and energy levels per session
   - Keyed by `startTime|endTime` for lookup

3. **Subjects** → `brainventory/subjects.json`
   - All user-created subjects
   - Each subject: name, description

**Automatic**: All data saved immediately after each action (save on session completion, new subject added, etc.)

## 📁 Project Structure

```
Brainventory/
├── brainventorypackage/
│   ├── Main.java                    # App entry point (uses PomodoroModern)
│   ├── MainStudy.java               # Alt entry with PomodoroStudy
│   ├── Pomodoro.java                # Basic Pomodoro timer
│   ├── PomodoroModern.java          # Modern Pomodoro UI
│   ├── PomodoroStudy.java           # Pomodoro with session logging (RECOMMENDED)
│   ├── Session.java                 # Session data model
│   ├── SessionManager.java          # Sessions persistence + query helpers
│   ├── Subject.java                 # Subject data model
│   ├── SubjectManager.java          # Subjects persistence
│   ├── StudyMetadataManager.java    # Productivity/energy persistence
│   ├── ReportGenerator.java         # Report generation engine
│   ├── SessionsPanel.java           # Reports UI panel
│   ├── Dashboard.java               # Dashboard placeholder
│   ├── MyButton.java                # Custom styled button
│   ├── RoundedPanel.java            # Rounded corner panel
│   ├── NavButton.java               # Navigation button
│   ├── gson-2.10.1.jar              # JSON library
│   └── ...other files
├── brainventory/                    # Data directory
│   ├── data.json                    # Sessions
│   ├── study_metadata.json          # Productivity/energy
│   └── subjects.json                # Subjects
└── README_COMPLETE.md               # This file
```

## 🚀 Quick Start

### Prerequisites
- Java 11+
- WSL (Windows Subsystem for Linux) or Linux/Mac
- Display server for GUI (X11 on WSL or native on Linux/Mac)

### Compile
```bash
javac -cp "brainventorypackage/gson-2.10.1.jar" brainventorypackage/*.java
```
### Run (Alternative: Main version)
```bash
java -cp ".:brainventorypackage/gson-2.10.1.jar" brainventorypackage.Main
```

## 📱 Usage

### 1. Start a Focus Session
1. Navigate to "Focus" tab
2. Click "Start" button to begin 25-minute Pomodoro
3. When timer reaches 0:00, a dialog appears

### 2. Log Session Data
When a focus session completes:
- **Subject**: Select existing subject or create new one
- **Productivity**: Rate 1-5 (default 3)
- **Energy**: Rate 1-5 (default 3)
- Click "OK" to save

Session is automatically saved with:
- Start time (when timer started)
- End time (now)
- Duration (in seconds)
- Subject (course name)
- Productivity & Energy levels

### 3. View Reports
Navigate to "Focus" tab → Scroll to "Sessions Panel"

**Daily Report**:
- Set date with date picker
- Click "Daily Report"
- View JSON with sessions for that day

**Weekly Report**:
- Set any date in the week
- Click "Weekly Report"
- View JSON with week's summary

**Overall Report**:
- Click "Overall Report"
- View JSON with all-time statistics

**Subjects Summary**:
- Click "Subjects Summary"
- View table: Subject | Total Hours

## 📊 Data Format Examples

### Session (data.json)
```json
[
  {
    "startTime": "2025-12-18 13:41:09",
    "endTime": "2025-12-18 14:06:09",
    "durationSeconds": 1500,
    "course": "CMSC 22"
  }
]
```

### Metadata (study_metadata.json)
```json
{
  "2025-12-18 13:41:09|2025-12-18 14:06:09": {
    "productivity": 4,
    "energy": 3
  }
}
```

### Subjects (subjects.json)
```json
[
  {
    "name": "CMSC 22",
    "description": "Data Structures"
  }
]
```

### Daily Report Output
```json
{
  "date": "2025-12-18",
  "totalSessions": 2,
  "totalSeconds": 3000,
  "subjects": [
    {
      "subject": "CMSC 22",
      "totalSeconds": 1500,
      "avgProductivity": 3.5,
      "avgEnergy": 3.0
    },
    {
      "subject": "CMSC 123",
      "totalSeconds": 1500,
      "avgProductivity": 4.0,
      "avgEnergy": 4.0
    }
  ]
}
```

## 🔧 Core Components

### SessionManager
- Loads/saves sessions from `brainventory/data.json`
- Query helpers:
  - `getSessionsBetween(LocalDateTime start, LocalDateTime end)`
  - `getSessionsForDate(LocalDate date)`
  - `getSessionsBySubject(String subject)`
  - `totalSecondsBySubject()`

### ReportGenerator
- Generates reports from sessions + metadata
- Methods:
  - `dailyReport(LocalDate date)` → JSON string
  - `weeklyReport(LocalDate anyDateInWeek)` → JSON string
  - `overallReport()` → JSON string
  - `summarizeBySubject(Collection<Session>)` → Map

### PomodoroStudy
- 25-minute focus timer (1500 seconds)
- Prompts for subject, productivity, energy on completion
- Creates Session and saves via SessionManager
- Stores metadata via StudyMetadataManager

## 📝 Files Modified/Created

### Created:
- `Subject.java` - Subject model
- `SubjectManager.java` - Subject persistence
- `StudyMetadataManager.java` - Productivity/energy persistence
- `ReportGenerator.java` - Report generation
- `PomodoroStudy.java` - Pomodoro with session logging
- `MainStudy.java` - Entry point for study version

### Modified:
- `Session.java` - No changes (uses existing `course` field)
- `SessionManager.java` - Added query helpers
- `SessionsPanel.java` - Replaced with Reports UI
- `Main.java` - Integrated managers and PomodoroStudy

## ✅ Requirements Fulfillment

| Requirement | Status | Implementation |
|---|---|---|
| 🕒 Study Session Logging | ✅ Complete | PomodoroStudy captures subject, duration, date, productivity, energy |
| 📘 Subject Management | ✅ Complete | SubjectManager add/remove/query; dropdown in session dialog |
| 📊 Report Generation | ✅ Complete | ReportGenerator provides daily, weekly, overall; SessionsPanel UI |
| 💾 Data Persistence | ✅ Complete | Gson JSON storage for sessions, metadata, subjects |

## 🐛 Troubleshooting

**"Error saving session"**
- Ensure `/home/jemarco/repos/Brainventory/brainventory/` directory exists
- Check write permissions: `ls -la brainventory/`

**"No data in reports"**
- Complete at least one focus session first
- Check data files: `cat brainventory/data.json`

**"Subject not appearing in dropdown"**
- Ensure subject is created by selecting "< New Subject >" and entering name
- Check `brainventory/subjects.json` for saved subjects

**Compilation errors**
- Verify Gson JAR present: `ls brainventorypackage/gson-2.10.1.jar`
- Compile with: `javac -cp "brainventorypackage/gson-2.10.1.jar" brainventorypackage/*.java`

## 📚 Technologies

- **Language**: Java 11+
- **GUI**: Swing
- **JSON**: Gson 2.10.1
- **Build**: javac (command-line)
- **Data Storage**: JSON files on disk

## 🎯 Future Enhancements

- [ ] Database backend (SQLite/MySQL)
- [ ] Charts and graphs for productivity trends
- [ ] Goal setting and progress tracking
- [ ] Customizable Pomodoro durations
- [ ] Dark/light theme toggle
- [ ] Export reports to CSV/PDF
- [ ] Notification on session completion
- [ ] Multi-user support

---

**Version**: 1.0.0  
**Author**: Jemarco Briz
