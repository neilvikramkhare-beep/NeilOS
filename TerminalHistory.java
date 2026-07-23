package com.neilos.terminal;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TerminalHistory - Manages terminal command history for NeilOS
 * Provides persistent storage, search, filtering, and management
 * of terminal command history.
 * 
 * Features:
 * - Command history storage with timestamps
 * - Persistent storage to file
 * - Search and filtering
 * - Command statistics
 * - History navigation (up/down arrows)
 * - Export/Import history
 * - History size management
 * - Duplicate command detection
 * - Command frequency tracking
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class TerminalHistory {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default maximum history size */
    public static final int DEFAULT_MAX_SIZE = 1000;
    
    /** Default history file name */
    public static final String DEFAULT_HISTORY_FILE = ".terminal_history";
    
    /** History file format version */
    public static final int HISTORY_VERSION = 1;
    
    /** Timestamp format for history entries */
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /** History entry separator */
    private static final String ENTRY_SEPARATOR = "|||";
    
    /** Command history separator */
    private static final String COMMAND_SEPARATOR = ":::";
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * Represents a single command history entry
     */
    public static class HistoryEntry {
        private String command;
        private LocalDateTime timestamp;
        private String workingDirectory;
        private int exitCode;
        private String user;
        private long executionTime;
        private String output;
        private boolean isSuccess;
        private String sessionId;
        private List<String> tags;
        private Map<String, String> metadata;
        
        public HistoryEntry(String command) {
            this.command = command;
            this.timestamp = LocalDateTime.now();
            this.exitCode = 0;
            this.isSuccess = true;
            this.executionTime = 0;
            this.tags = new ArrayList<>();
            this.metadata = new HashMap<>();
            this.workingDirectory = System.getProperty("user.dir");
            this.user = System.getProperty("user.name");
            this.sessionId = UUID.randomUUID().toString();
        }
        
        public HistoryEntry(String command, LocalDateTime timestamp) {
            this(command);
            this.timestamp = timestamp;
        }
        
        public String getCommand() { return command; }
        public void setCommand(String command) { this.command = command; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getWorkingDirectory() { return workingDirectory; }
        public void setWorkingDirectory(String workingDirectory) { 
            this.workingDirectory = workingDirectory; 
        }
        
        public int getExitCode() { return exitCode; }
        public void setExitCode(int exitCode) { 
            this.exitCode = exitCode;
            this.isSuccess = exitCode == 0;
        }
        
        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }
        
        public boolean isSuccess() { return isSuccess; }
        public void setSuccess(boolean success) { isSuccess = success; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }
        public void addTag(String tag) { 
            if (tag != null && !tag.isEmpty()) {
                tags.add(tag);
            }
        }
        public void removeTag(String tag) { tags.remove(tag); }
        public boolean hasTag(String tag) { return tags.contains(tag); }
        
        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { 
            this.metadata = metadata != null ? metadata : new HashMap<>(); 
        }
        public void putMetadata(String key, String value) { metadata.put(key, value); }
        public String getMetadata(String key) { return metadata.get(key); }
        
        @Override
        public String toString() {
            return String.format("[%s] %s", 
                timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
                command);
        }
        
        public String toDetailedString() {
            StringBuilder sb = new StringBuilder();
            sb.append("═".repeat(60)).append("\n");
            sb.append("📝 HISTORY ENTRY\n");
            sb.append("═".repeat(60)).append("\n");
            sb.append("Command:      ").append(command).append("\n");
            sb.append("Timestamp:    ").append(timestamp.format(TIMESTAMP_FORMAT)).append("\n");
            sb.append("User:         ").append(user).append("\n");
            sb.append("Directory:    ").append(workingDirectory).append("\n");
            sb.append("Exit Code:    ").append(exitCode).append("\n");
            sb.append("Status:       ").append(isSuccess ? "✅ Success" : "❌ Failed").append("\n");
            sb.append("Execution:    ").append(executionTime).append("ms\n");
            sb.append("Session:      ").append(sessionId).append("\n");
            if (!tags.isEmpty()) {
                sb.append("Tags:         ").append(String.join(", ", tags)).append("\n");
            }
            if (output != null && !output.isEmpty()) {
                sb.append("Output:\n");
                sb.append("─".repeat(40)).append("\n");
                sb.append(output).append("\n");
                sb.append("─".repeat(40)).append("\n");
            }
            sb.append("═".repeat(60)).append("\n");
            return sb.toString();
        }
        
        public String toSerialized() {
            StringBuilder sb = new StringBuilder();
            sb.append(timestamp.format(TIMESTAMP_FORMAT)).append(ENTRY_SEPARATOR);
            sb.append(command).append(ENTRY_SEPARATOR);
            sb.append(workingDirectory).append(ENTRY_SEPARATOR);
            sb.append(exitCode).append(ENTRY_SEPARATOR);
            sb.append(user).append(ENTRY_SEPARATOR);
            sb.append(executionTime).append(ENTRY_SEPARATOR);
            sb.append(isSuccess).append(ENTRY_SEPARATOR);
            sb.append(sessionId).append(ENTRY_SEPARATOR);
            sb.append(String.join(COMMAND_SEPARATOR, tags)).append(ENTRY_SEPARATOR);
            sb.append(output != null ? output : "");
            return sb.toString();
        }
        
        public static HistoryEntry fromSerialized(String serialized) {
            String[] parts = serialized.split("\\" + ENTRY_SEPARATOR);
            if (parts.length < 9) {
                throw new IllegalArgumentException("Invalid serialized entry");
            }
            
            LocalDateTime timestamp = LocalDateTime.parse(parts[0], TIMESTAMP_FORMAT);
            String command = parts[1];
            String workingDir = parts[2];
            int exitCode = Integer.parseInt(parts[3]);
            String user = parts[4];
            long executionTime = Long.parseLong(parts[5]);
            boolean isSuccess = Boolean.parseBoolean(parts[6]);
            String sessionId = parts[7];
            String tagsStr = parts[8];
            String output = parts.length > 9 ? parts[9] : null;
            
            HistoryEntry entry = new HistoryEntry(command, timestamp);
            entry.setWorkingDirectory(workingDir);
            entry.setExitCode(exitCode);
            entry.setUser(user);
            entry.setExecutionTime(executionTime);
            entry.setSuccess(isSuccess);
            entry.setSessionId(sessionId);
            entry.setOutput(output);
            
            if (!tagsStr.isEmpty()) {
                entry.setTags(Arrays.asList(tagsStr.split(COMMAND_SEPARATOR)));
            }
            
            return entry;
        }
    }
    
    /**
     * Command statistics
     */
    public static class CommandStats {
        private String command;
        private int count;
        private long totalExecutionTime;
        private double avgExecutionTime;
        private long lastUsed;
        private int successCount;
        private int failureCount;
        private Set<String> sessions;
        
        public CommandStats(String command) {
            this.command = command;
            this.count = 0;
            this.totalExecutionTime = 0;
            this.successCount = 0;
            this.failureCount = 0;
            this.sessions = new HashSet<>();
        }
        
        public String getCommand() { return command; }
        public int getCount() { return count; }
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public double getAvgExecutionTime() { return avgExecutionTime; }
        public long getLastUsed() { return lastUsed; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public Set<String> getSessions() { return sessions; }
        
        public void addUsage(long executionTime, boolean success, String sessionId) {
            count++;
            totalExecutionTime += executionTime;
            avgExecutionTime = (double) totalExecutionTime / count;
            lastUsed = System.currentTimeMillis();
            if (success) {
                successCount++;
            } else {
                failureCount++;
            }
            sessions.add(sessionId);
        }
        
        public double getSuccessRate() {
            int total = successCount + failureCount;
            return total > 0 ? (double) successCount / total * 100 : 0;
        }
        
        @Override
        public String toString() {
            return String.format("%s: %d times, avg: %.2fms, success: %.1f%%", 
                command, count, avgExecutionTime, getSuccessRate());
        }
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private int maxSize;
    private String historyFile;
    private Deque<HistoryEntry> entries;
    private Map<String, CommandStats> commandStats;
    private Set<String> sessions;
    private AtomicInteger totalCommands;
    private List<HistoryListener> listeners;
    private boolean autoSave;
    private boolean persistent;
    private boolean duplicateDetection;
    private int currentIndex;
    
    // ============================================================
    // INTERFACES
    // ============================================================
    
    /**
     * History listener for events
     */
    public interface HistoryListener {
        void onCommandAdded(HistoryEntry entry);
        void onHistoryCleared();
        void onHistoryLoaded(List<HistoryEntry> entries);
        void onHistoryExported(File file);
        void onError(String message, Exception e);
    }
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor
     */
    public TerminalHistory() {
        this(DEFAULT_MAX_SIZE, DEFAULT_HISTORY_FILE);
    }
    
    /**
     * Constructor with custom size
     * 
     * @param maxSize The maximum history size
     */
    public TerminalHistory(int maxSize) {
        this(maxSize, DEFAULT_HISTORY_FILE);
    }
    
    /**
     * Constructor with custom size and file
     * 
     * @param maxSize The maximum history size
     * @param historyFile The history file path
     */
    public TerminalHistory(int maxSize, String historyFile) {
        this.maxSize = maxSize;
        this.historyFile = historyFile;
        this.entries = new ConcurrentLinkedDeque<>();
        this.commandStats = new ConcurrentHashMap<>();
        this.sessions = new HashSet<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.totalCommands = new AtomicInteger(0);
        this.autoSave = true;
        this.persistent = true;
        this.duplicateDetection = false;
        this.currentIndex = 0;
        
        // Load existing history
        if (persistent) {
            loadHistory();
        }
    }
    
    // ============================================================
    // HISTORY MANAGEMENT
    // ============================================================
    
    /**
     * Adds a command to history
     * 
     * @param command The command to add
     * @return The created history entry
     */
    public HistoryEntry addCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return null;
        }
        
        command = command.trim();
        
        // Check for duplicate
        if (duplicateDetection) {
            HistoryEntry last = getLast();
            if (last != null && last.getCommand().equals(command)) {
                // Update timestamp of last entry instead of adding new
                last.setTimestamp(LocalDateTime.now());
                if (autoSave) {
                    saveHistory();
                }
                return last;
            }
        }
        
        HistoryEntry entry = new HistoryEntry(command);
        entries.addFirst(entry);
        
        // Update statistics
        updateCommandStats(entry);
        
        // Trim history if needed
        while (entries.size() > maxSize) {
            entries.removeLast();
        }
        
        totalCommands.incrementAndGet();
        
        // Notify listeners
        notifyCommandAdded(entry);
        
        // Auto-save
        if (autoSave) {
            saveHistory();
        }
        
        return entry;
    }
    
    /**
     * Adds a command with full details
     * 
     * @param command The command
     * @param exitCode The exit code
     * @param executionTime The execution time in milliseconds
     * @param output The command output
     * @return The created history entry
     */
    public HistoryEntry addCommand(String command, int exitCode, long executionTime, String output) {
        HistoryEntry entry = addCommand(command);
        if (entry != null) {
            entry.setExitCode(exitCode);
            entry.setExecutionTime(executionTime);
            entry.setOutput(output);
            if (autoSave) {
                saveHistory();
            }
        }
        return entry;
    }
    
    /**
     * Gets the last command in history
     * 
     * @return The last history entry, or null if empty
     */
    public HistoryEntry getLast() {
        return entries.isEmpty() ? null : entries.getFirst();
    }
    
    /**
     * Gets the command at the specified index
     * 
     * @param index The index (0 = most recent)
     * @return The history entry, or null if not found
     */
    public HistoryEntry getAt(int index) {
        if (index < 0 || index >= entries.size()) {
            return null;
        }
        return entries.stream().skip(index).findFirst().orElse(null);
    }
    
    /**
     * Gets all history entries
     * 
     * @return List of all entries
     */
    public List<HistoryEntry> getEntries() {
        return new ArrayList<>(entries);
    }
    
    /**
     * Gets the current history size
     * 
     * @return The number of entries
     */
    public int getSize() {
        return entries.size();
    }
    
    /**
     * Checks if history is empty
     * 
     * @return true if empty
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
    
    /**
     * Clears the entire history
     */
    public void clear() {
        entries.clear();
        commandStats.clear();
        totalCommands.set(0);
        currentIndex = 0;
        
        if (autoSave) {
            saveHistory();
        }
        
        notifyHistoryCleared();
    }
    
    /**
     * Removes a specific entry from history
     * 
     * @param entry The entry to remove
     * @return true if removed
     */
    public boolean removeEntry(HistoryEntry entry) {
        boolean removed = entries.remove(entry);
        if (removed) {
            // Rebuild statistics
            rebuildStats();
            if (autoSave) {
                saveHistory();
            }
        }
        return removed;
    }
    
    /**
     * Removes entries older than the specified date
     * 
     * @param before The date threshold
     * @return The number of removed entries
     */
    public int removeOlderThan(LocalDateTime before) {
        List<HistoryEntry> toRemove = new ArrayList<>();
        for (HistoryEntry entry : entries) {
            if (entry.getTimestamp().isBefore(before)) {
                toRemove.add(entry);
            }
        }
        
        for (HistoryEntry entry : toRemove) {
            entries.remove(entry);
        }
        
        if (!toRemove.isEmpty()) {
            rebuildStats();
            if (autoSave) {
                saveHistory();
            }
        }
        
        return toRemove.size();
    }
    
    // ============================================================
    // NAVIGATION METHODS
    // ============================================================
    
    /**
     * Gets the previous command in history (for up arrow)
     * 
     * @param current The current command text
     * @return The previous command, or null
     */
    public String getPrevious(String current) {
        if (entries.isEmpty()) {
            return null;
        }
        
        // Find matching command if current is partial
        if (current != null && !current.isEmpty()) {
            for (HistoryEntry entry : entries) {
                if (entry.getCommand().startsWith(current)) {
                    return entry.getCommand();
                }
            }
            return null;
        }
        
        // Navigation
        if (currentIndex >= entries.size() - 1) {
            return null;
        }
        
        HistoryEntry entry = getAt(currentIndex + 1);
        if (entry != null) {
            currentIndex++;
            return entry.getCommand();
        }
        return null;
    }
    
    /**
     * Gets the next command in history (for down arrow)
     * 
     * @param current The current command text
     * @return The next command, or null
     */
    public String getNext(String current) {
        if (entries.isEmpty()) {
            return null;
        }
        
        if (currentIndex <= 0) {
            currentIndex = 0;
            return null;
        }
        
        currentIndex--;
        HistoryEntry entry = getAt(currentIndex);
        return entry != null ? entry.getCommand() : null;
    }
    
    /**
     * Resets the navigation index
     */
    public void resetNavigation() {
        currentIndex = 0;
    }
    
    // ============================================================
    // SEARCH METHODS
    // ============================================================
    
    /**
     * Searches history for commands containing the search text
     * 
     * @param searchText The text to search for
     * @return List of matching entries
     */
    public List<HistoryEntry> search(String searchText) {
        return search(searchText, false);
    }
    
    /**
     * Searches history for commands
     * 
     * @param searchText The text to search for
     * @param caseSensitive Whether search is case sensitive
     * @return List of matching entries
     */
    public List<HistoryEntry> search(String searchText, boolean caseSensitive) {
        List<HistoryEntry> results = new ArrayList<>();
        if (searchText == null || searchText.isEmpty()) {
            return new ArrayList<>(entries);
        }
        
        String search = caseSensitive ? searchText : searchText.toLowerCase();
        for (HistoryEntry entry : entries) {
            String command = caseSensitive ? entry.getCommand() : entry.getCommand().toLowerCase();
            if (command.contains(search)) {
                results.add(entry);
            }
        }
        return results;
    }
    
    /**
     * Searches history with regex pattern
     * 
     * @param pattern The regex pattern
     * @return List of matching entries
     */
    public List<HistoryEntry> searchRegex(String pattern) {
        List<HistoryEntry> results = new ArrayList<>();
        if (pattern == null || pattern.isEmpty()) {
            return new ArrayList<>(entries);
        }
        
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        for (HistoryEntry entry : entries) {
            if (p.matcher(entry.getCommand()).matches()) {
                results.add(entry);
            }
        }
        return results;
    }
    
    /**
     * Gets the most recent command containing the search text
     * 
     * @param searchText The text to search for
     * @return The most recent matching command, or null
     */
    public String findLast(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return null;
        }
        
        for (HistoryEntry entry : entries) {
            if (entry.getCommand().contains(searchText)) {
                return entry.getCommand();
            }
        }
        return null;
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Gets command statistics
     * 
     * @return Map of command to statistics
     */
    public Map<String, CommandStats> getCommandStats() {
        return new HashMap<>(commandStats);
    }
    
    /**
     * Gets the most frequently used commands
     * 
     * @param limit The number of commands to return
     * @return List of most frequent commands
     */
    public List<CommandStats> getMostFrequentCommands(int limit) {
        return commandStats.values().stream()
            .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
            .limit(limit)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets the most recent commands
     * 
     * @param limit The number of commands to return
     * @return List of recent commands
     */
    public List<HistoryEntry> getRecentCommands(int limit) {
        return entries.stream()
            .limit(limit)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets the total number of commands in history
     * 
     * @return Total command count
     */
    public int getTotalCommands() {
        return totalCommands.get();
    }
    
    /**
     * Updates command statistics
     * 
     * @param entry The history entry
     */
    private void updateCommandStats(HistoryEntry entry) {
        String cmd = entry.getCommand();
        CommandStats stats = commandStats.computeIfAbsent(cmd, CommandStats::new);
        stats.addUsage(entry.getExecutionTime(), entry.isSuccess(), entry.getSessionId());
    }
    
    /**
     * Rebuilds command statistics from history
     */
    private void rebuildStats() {
        commandStats.clear();
        for (HistoryEntry entry : entries) {
            updateCommandStats(entry);
        }
    }
    
    // ============================================================
    // PERSISTENCE
    // ============================================================
    
    /**
     * Loads history from file
     */
    public void loadHistory() {
        File file = new File(historyFile);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            entries.clear();
            commandStats.clear();
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    HistoryEntry entry = HistoryEntry.fromSerialized(line);
                    entries.add(entry);
                    updateCommandStats(entry);
                    sessions.add(entry.getSessionId());
                } catch (Exception e) {
                    // Skip invalid entries
                }
            }
            
            // Trim to max size
            while (entries.size() > maxSize) {
                entries.removeLast();
            }
            
            totalCommands.set(entries.size());
            notifyHistoryLoaded(getEntries());
            
        } catch (IOException e) {
            notifyError("Failed to load history: " + e.getMessage(), e);
        }
    }
    
    /**
     * Saves history to file
     */
    public void saveHistory() {
        if (!persistent) {
            return;
        }
        
        try {
            File file = new File(historyFile);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (HistoryEntry entry : entries) {
                    writer.write(entry.toSerialized());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            notifyError("Failed to save history: " + e.getMessage(), e);
        }
    }
    
    /**
     * Exports history to a file
     * 
     * @param outputFile The output file
     * @param format The export format (text, csv, json)
     */
    public void exportHistory(File outputFile, String format) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            if ("csv".equalsIgnoreCase(format)) {
                exportCSV(writer);
            } else if ("json".equalsIgnoreCase(format)) {
                exportJSON(writer);
            } else {
                exportText(writer);
            }
        }
        notifyHistoryExported(outputFile);
    }
    
    /**
     * Exports history as text
     */
    private void exportText(BufferedWriter writer) throws IOException {
        writer.write("Terminal History Export\n");
        writer.write("═".repeat(60) + "\n");
        writer.write("Generated: " + LocalDateTime.now().format(TIMESTAMP_FORMAT) + "\n");
        writer.write("Total Commands: " + entries.size() + "\n");
        writer.write("═".repeat(60) + "\n\n");
        
        for (HistoryEntry entry : entries) {
            writer.write(entry.toDetailedString());
            writer.write("\n");
        }
    }
    
    /**
     * Exports history as CSV
     */
    private void exportCSV(BufferedWriter writer) throws IOException {
        writer.write("Timestamp,Command,WorkingDirectory,ExitCode,User,ExecutionTime,Success,SessionId,Tags\n");
        for (HistoryEntry entry : entries) {
            writer.write(String.format("%s,\"%s\",\"%s\",%d,\"%s\",%d,%b,\"%s\",\"%s\"\n",
                entry.getTimestamp().format(TIMESTAMP_FORMAT),
                escapeCSV(entry.getCommand()),
                escapeCSV(entry.getWorkingDirectory()),
                entry.getExitCode(),
                escapeCSV(entry.getUser()),
                entry.getExecutionTime(),
                entry.isSuccess(),
                entry.getSessionId(),
                String.join(";", entry.getTags())
            ));
        }
    }
    
    /**
     * Exports history as JSON
     */
    private void exportJSON(BufferedWriter writer) throws IOException {
        writer.write("{\n");
        writer.write("  \"version\": \"1.0\",\n");
        writer.write("  \"exported_at\": \"" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + "\",\n");
        writer.write("  \"total_commands\": " + entries.size() + ",\n");
        writer.write("  \"entries\": [\n");
        
        int i = 0;
        for (HistoryEntry entry : entries) {
            writer.write("    {\n");
            writer.write("      \"timestamp\": \"" + entry.getTimestamp().format(TIMESTAMP_FORMAT) + "\",\n");
            writer.write("      \"command\": \"" + escapeJSON(entry.getCommand()) + "\",\n");
            writer.write("      \"working_directory\": \"" + escapeJSON(entry.getWorkingDirectory()) + "\",\n");
            writer.write("      \"exit_code\": " + entry.getExitCode() + ",\n");
            writer.write("      \"user\": \"" + escapeJSON(entry.getUser()) + "\",\n");
            writer.write("      \"execution_time\": " + entry.getExecutionTime() + ",\n");
            writer.write("      \"success\": " + entry.isSuccess() + ",\n");
            writer.write("      \"session_id\": \"" + entry.getSessionId() + "\",\n");
            writer.write("      \"tags\": [" + 
                entry.getTags().stream().map(t -> "\"" + escapeJSON(t) + "\"").reduce((a,b) -> a + "," + b).orElse("") + "]\n");
            writer.write("    }" + (i < entries.size() - 1 ? "," : "") + "\n");
            i++;
        }
        
        writer.write("  ]\n");
        writer.write("}\n");
    }
    
    /**
     * Escapes CSV special characters
     */
    private String escapeCSV(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }
    
    /**
     * Escapes JSON special characters
     */
    private String escapeJSON(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    // ============================================================
    // LISTENER MANAGEMENT
    // ============================================================
    
    /**
     * Adds a history listener
     * 
     * @param listener The listener to add
     */
    public void addListener(HistoryListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a history listener
     * 
     * @param listener The listener to remove
     */
    public void removeListener(HistoryListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies listeners of command added
     */
    private void notifyCommandAdded(HistoryEntry entry) {
        for (HistoryListener listener : listeners) {
            try {
                listener.onCommandAdded(entry);
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
    
    /**
     * Notifies listeners of history cleared
     */
    private void notifyHistoryCleared() {
        for (HistoryListener listener : listeners) {
            try {
                listener.onHistoryCleared();
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
    
    /**
     * Notifies listeners of history loaded
     */
    private void notifyHistoryLoaded(List<HistoryEntry> entries) {
        for (HistoryListener listener : listeners) {
            try {
                listener.onHistoryLoaded(entries);
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
    
    /**
     * Notifies listeners of history exported
     */
    private void notifyHistoryExported(File file) {
        for (HistoryListener listener : listeners) {
            try {
                listener.onHistoryExported(file);
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
    
    /**
     * Notifies listeners of errors
     */
    private void notifyError(String message, Exception e) {
        for (HistoryListener listener : listeners) {
            try {
                listener.onError(message, e);
            } catch (Exception ex) {
                // Ignore listener errors
            }
        }
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public int getMaxSize() { return maxSize; }
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        while (entries.size() > maxSize) {
            entries.removeLast();
        }
        if (autoSave) {
            saveHistory();
        }
    }
    
    public String getHistoryFile() { return historyFile; }
    public void setHistoryFile(String historyFile) {
        this.historyFile = historyFile;
        if (persistent) {
            loadHistory();
        }
    }
    
    public boolean isAutoSave() { return autoSave; }
    public void setAutoSave(boolean autoSave) { 
        this.autoSave = autoSave;
        if (autoSave) {
            saveHistory();
        }
    }
    
    public boolean isPersistent() { return persistent; }
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
        if (persistent) {
            loadHistory();
        }
    }
    
    public boolean isDuplicateDetection() { return duplicateDetection; }
    public void setDuplicateDetection(boolean duplicateDetection) { 
        this.duplicateDetection = duplicateDetection; 
    }
    
    public Set<String> getSessions() { return new HashSet<>(sessions); }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Gets history as an array of strings
     * 
     * @return Array of command strings
     */
    public String[] toArray() {
        return entries.stream()
            .map(HistoryEntry::getCommand)
            .toArray(String[]::new);
    }
    
    /**
     * Gets history as a list of command strings
     * 
     * @return List of command strings
     */
    public List<String> toCommandList() {
        return entries.stream()
            .map(HistoryEntry::getCommand)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Prints history to console
     */
    public void printHistory() {
        System.out.println("Terminal History (" + entries.size() + " entries)");
        System.out.println("═".repeat(60));
        int i = 0;
        for (HistoryEntry entry : entries) {
            System.out.printf("%3d: %s\n", ++i, entry);
        }
    }
    
    /**
     * Searches history and returns matching commands
     * 
     * @param searchText The text to search for
     * @return List of matching command strings
     */
    public List<String> searchCommands(String searchText) {
        return search(searchText).stream()
            .map(HistoryEntry::getCommand)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of TerminalHistory
     */
    public static void main(String[] args) {
        System.out.println("📝 TerminalHistory Demo");
        System.out.println("═".repeat(60));
        
        TerminalHistory history = new TerminalHistory(10);
        
        // Add listener
        history.addListener(new HistoryListener() {
            @Override
            public void onCommandAdded(HistoryEntry entry) {
                System.out.println("➕ Added: " + entry.getCommand());
            }
            
            @Override
            public void onHistoryCleared() {
                System.out.println("🗑️ History cleared");
            }
            
            @Override
            public void onHistoryLoaded(List<HistoryEntry> entries) {
                System.out.println("📂 Loaded " + entries.size() + " entries");
            }
            
            @Override
            public void onHistoryExported(File file) {
                System.out.println("📤 Exported to: " + file.getName());
            }
            
            @Override
            public void onError(String message, Exception e) {
                System.err.println("❌ Error: " + message);
            }
        });
        
        // Add commands
        System.out.println("\n📝 Adding commands:");
        history.addCommand("ls -la");
        history.addCommand("cd /home/user");
        history.addCommand("python script.py");
        history.addCommand("git status");
        history.addCommand("docker ps");
        history.addCommand("kubectl get pods");
        history.addCommand("systemctl status nginx");
        
        // Add command with details
        history.addCommand("java -jar app.jar", 0, 1500, "Application started successfully");
        
        // Print history
        System.out.println("\n📋 History:");
        history.printHistory();
        
        // Search
        System.out.println("\n🔍 Searching for 'git':");
        List<HistoryEntry> results = history.search("git");
        for (HistoryEntry entry : results) {
            System.out.println("  " + entry);
        }
        
        // Statistics
        System.out.println("\n📊 Statistics:");
        Map<String, CommandStats> stats = history.getCommandStats();
        for (Map.Entry<String, CommandStats> entry : stats.entrySet()) {
            System.out.println("  " + entry.getValue());
        }
        
        // Most frequent commands
        System.out.println("\n📈 Most Frequent Commands:");
        List<CommandStats> top = history.getMostFrequentCommands(5);
        for (CommandStats stat : top) {
            System.out.println("  " + stat);
        }
        
        // Navigation
        System.out.println("\n🧭 Navigation Test:");
        String[] navCommands = {"python", "git", "docker", "kubectl"};
        System.out.println("  Current: " + history.getNext(navCommands[0]));
        System.out.println("  Previous: " + history.getPrevious(navCommands[0]));
        System.out.println("  Previous: " + history.getPrevious(navCommands[0]));
        System.out.println("  Next: " + history.getNext(navCommands[0]));
        
        // Export
        try {
            File exportFile = new File("history_export.txt");
            history.exportHistory(exportFile, "text");
            System.out.println("\n📤 Exported to: " + exportFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
        
        System.out.println("\n✅ Demo completed!");
    }
}