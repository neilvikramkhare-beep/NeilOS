package com.neilos.apps;

import com.neilos.NeilOS;
import com.neilos.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * AppManager - Comprehensive application management for NeilOS
 * Handles application lifecycle, window management, app registration,
 * inter-app communication, and application state management.
 * 
 * Features:
 * - Application registration and discovery
 * - Application lifecycle management (start, stop, pause, resume)
 * - Window management (create, close, minimize, maximize)
 * - Inter-app communication (events, messages)
 * - Application state persistence
 * - Application shortcuts
 * - Application categories
 * - Application preferences
 * - Multi-instance support
 * - Application monitoring
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class AppManager {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Application state constants */
    public static final int STATE_STOPPED = 0;
    public static final int STATE_STARTING = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_PAUSED = 3;
    public static final int STATE_STOPPING = 4;
    public static final int STATE_ERROR = 5;
    
    /** Application priority levels */
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_CRITICAL = 3;
    
    /** Application categories */
    public static final String CATEGORY_SYSTEM = "System";
    public static final String CATEGORY_PRODUCTIVITY = "Productivity";
    public static final String CATEGORY_DEVELOPMENT = "Development";
    public static final String CATEGORY_MULTIMEDIA = "Multimedia";
    public static final String CATEGORY_GAMES = "Games";
    public static final String CATEGORY_COMMUNICATION = "Communication";
    public static final String CATEGORY_UTILITIES = "Utilities";
    public static final String CATEGORY_SECURITY = "Security";
    public static final String CATEGORY_NETWORK = "Network";
    public static final String CATEGORY_EDUCATION = "Education";
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * Application descriptor containing all metadata
     */
    public static class AppDescriptor {
        private String id;
        private String name;
        private String version;
        private String author;
        private String description;
        private String category;
        private String icon;
        private int priority;
        private boolean systemApp;
        private boolean multiInstance;
        private boolean hidden;
        private boolean pinned;
        private String mainClass;
        private List<String> dependencies;
        private List<String> permissions;
        private Map<String, String> metadata;
        private Date installedAt;
        private Date updatedAt;
        private String installPath;
        private String iconPath;
        private List<String> tags;
        private String website;
        private String supportEmail;
        private long size;
        private int minMemory;
        private int maxMemory;
        
        public AppDescriptor(String id, String name) {
            this.id = id;
            this.name = name;
            this.dependencies = new ArrayList<>();
            this.permissions = new ArrayList<>();
            this.metadata = new HashMap<>();
            this.tags = new ArrayList<>();
            this.priority = PRIORITY_NORMAL;
            this.installedAt = new Date();
            this.updatedAt = new Date();
            this.category = CATEGORY_UTILITIES;
            this.minMemory = 64;
            this.maxMemory = 512;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public boolean isSystemApp() { return systemApp; }
        public void setSystemApp(boolean systemApp) { this.systemApp = systemApp; }
        
        public boolean isMultiInstance() { return multiInstance; }
        public void setMultiInstance(boolean multiInstance) { this.multiInstance = multiInstance; }
        
        public boolean isHidden() { return hidden; }
        public void setHidden(boolean hidden) { this.hidden = hidden; }
        
        public boolean isPinned() { return pinned; }
        public void setPinned(boolean pinned) { this.pinned = pinned; }
        
        public String getMainClass() { return mainClass; }
        public void setMainClass(String mainClass) { this.mainClass = mainClass; }
        
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
        public void addDependency(String dep) { 
            if (!dependencies.contains(dep)) {
                dependencies.add(dep);
            }
        }
        
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
        public void addPermission(String permission) {
            if (!permissions.contains(permission)) {
                permissions.add(permission);
            }
        }
        
        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
        public void setMetadata(String key, String value) { metadata.put(key, value); }
        public String getMetadata(String key) { return metadata.get(key); }
        
        public Date getInstalledAt() { return installedAt; }
        public void setInstalledAt(Date installedAt) { this.installedAt = installedAt; }
        
        public Date getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
        
        public String getInstallPath() { return installPath; }
        public void setInstallPath(String installPath) { this.installPath = installPath; }
        
        public String getIconPath() { return iconPath; }
        public void setIconPath(String iconPath) { this.iconPath = iconPath; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public void addTag(String tag) { 
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }
        public void removeTag(String tag) { tags.remove(tag); }
        
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        
        public String getSupportEmail() { return supportEmail; }
        public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }
        
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        
        public int getMinMemory() { return minMemory; }
        public void setMinMemory(int minMemory) { this.minMemory = minMemory; }
        
        public int getMaxMemory() { return maxMemory; }
        public void setMaxMemory(int maxMemory) { this.maxMemory = maxMemory; }
        
        @Override
        public String toString() {
            return String.format("%s[%s] v%s - %s", name, id, version, category);
        }
        
        public String toDetailedString() {
            StringBuilder sb = new StringBuilder();
            sb.append("═".repeat(60)).append("\n");
            sb.append("📱 APPLICATION: ").append(name).append("\n");
            sb.append("═".repeat(60)).append("\n");
            sb.append("ID:           ").append(id).append("\n");
            sb.append("Version:      ").append(version != null ? version : "N/A").append("\n");
            sb.append("Author:       ").append(author != null ? author : "Unknown").append("\n");
            sb.append("Category:     ").append(category).append("\n");
            sb.append("Description:  ").append(description != null ? description : "N/A").append("\n");
            sb.append("Priority:     ").append(getPriorityString(priority)).append("\n");
            sb.append("System App:   ").append(systemApp ? "✅ Yes" : "❌ No").append("\n");
            sb.append("Multi-inst:   ").append(multiInstance ? "✅ Yes" : "❌ No").append("\n");
            sb.append("Pinned:       ").append(pinned ? "✅ Yes" : "❌ No").append("\n");
            sb.append("Size:         ").append(formatSize(size)).append("\n");
            sb.append("Memory:       ").append(minMemory).append("-").append(maxMemory).append(" MB\n");
            sb.append("Installed:    ").append(installedAt).append("\n");
            sb.append("Updated:      ").append(updatedAt).append("\n");
            
            if (!dependencies.isEmpty()) {
                sb.append("Dependencies: ").append(String.join(", ", dependencies)).append("\n");
            }
            if (!permissions.isEmpty()) {
                sb.append("Permissions:  ").append(String.join(", ", permissions)).append("\n");
            }
            if (!tags.isEmpty()) {
                sb.append("Tags:         ").append(String.join(", ", tags)).append("\n");
            }
            if (website != null) {
                sb.append("Website:      ").append(website).append("\n");
            }
            if (supportEmail != null) {
                sb.append("Support:      ").append(supportEmail).append("\n");
            }
            
            sb.append("═".repeat(60)).append("\n");
            return sb.toString();
        }
        
        private String getPriorityString(int priority) {
            switch (priority) {
                case PRIORITY_LOW: return "Low";
                case PRIORITY_NORMAL: return "Normal";
                case PRIORITY_HIGH: return "High";
                case PRIORITY_CRITICAL: return "Critical";
                default: return "Unknown";
            }
        }
        
        private String formatSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Running application instance
     */
    public static class AppInstance {
        private AppDescriptor descriptor;
        private int instanceId;
        private int state;
        private JFrame window;
        private JPanel content;
        private Component component;
        private Process process;
        private Thread thread;
        private Future<?> future;
        private long startTime;
        private long lastActivity;
        private Map<String, Object> data;
        private Map<String, Object> stateData;
        private List<AppInstance> children;
        private AppInstance parent;
        private boolean focused;
        private boolean visible;
        private Rectangle bounds;
        private int windowState;
        
        public AppInstance(AppDescriptor descriptor) {
            this.descriptor = descriptor;
            this.instanceId = generateInstanceId();
            this.state = STATE_STOPPED;
            this.data = new ConcurrentHashMap<>();
            this.stateData = new ConcurrentHashMap<>();
            this.children = new ArrayList<>();
            this.bounds = new Rectangle(100, 100, 800, 600);
            this.windowState = JFrame.NORMAL;
        }
        
        public AppDescriptor getDescriptor() { return descriptor; }
        public void setDescriptor(AppDescriptor descriptor) { this.descriptor = descriptor; }
        
        public int getInstanceId() { return instanceId; }
        public void setInstanceId(int instanceId) { this.instanceId = instanceId; }
        
        public int getState() { return state; }
        public void setState(int state) { this.state = state; }
        public String getStateString() {
            switch (state) {
                case STATE_STOPPED: return "Stopped";
                case STATE_STARTING: return "Starting";
                case STATE_RUNNING: return "Running";
                case STATE_PAUSED: return "Paused";
                case STATE_STOPPING: return "Stopping";
                case STATE_ERROR: return "Error";
                default: return "Unknown";
            }
        }
        
        public JFrame getWindow() { return window; }
        public void setWindow(JFrame window) { this.window = window; }
        
        public JPanel getContent() { return content; }
        public void setContent(JPanel content) { this.content = content; }
        
        public Component getComponent() { return component; }
        public void setComponent(Component component) { this.component = component; }
        
        public Process getProcess() { return process; }
        public void setProcess(Process process) { this.process = process; }
        
        public Thread getThread() { return thread; }
        public void setThread(Thread thread) { this.thread = thread; }
        
        public Future<?> getFuture() { return future; }
        public void setFuture(Future<?> future) { this.future = future; }
        
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        
        public long getLastActivity() { return lastActivity; }
        public void setLastActivity(long lastActivity) { this.lastActivity = lastActivity; }
        public void updateActivity() { this.lastActivity = System.currentTimeMillis(); }
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        public void setData(String key, Object value) { data.put(key, value); }
        public Object getData(String key) { return data.get(key); }
        
        public Map<String, Object> getStateData() { return stateData; }
        public void setStateData(Map<String, Object> stateData) { this.stateData = stateData; }
        public void setStateData(String key, Object value) { stateData.put(key, value); }
        public Object getStateData(String key) { return stateData.get(key); }
        
        public List<AppInstance> getChildren() { return children; }
        public void setChildren(List<AppInstance> children) { this.children = children; }
        public void addChild(AppInstance child) { 
            child.parent = this;
            children.add(child); 
        }
        public void removeChild(AppInstance child) { 
            child.parent = null;
            children.remove(child); 
        }
        
        public AppInstance getParent() { return parent; }
        public void setParent(AppInstance parent) { this.parent = parent; }
        
        public boolean isFocused() { return focused; }
        public void setFocused(boolean focused) { this.focused = focused; }
        
        public boolean isVisible() { return visible; }
        public void setVisible(boolean visible) { this.visible = visible; }
        
        public Rectangle getBounds() { return bounds; }
        public void setBounds(Rectangle bounds) { this.bounds = bounds; }
        
        public int getWindowState() { return windowState; }
        public void setWindowState(int windowState) { this.windowState = windowState; }
        
        public long getUptime() {
            if (state == STATE_RUNNING || state == STATE_PAUSED) {
                return System.currentTimeMillis() - startTime;
            }
            return 0;
        }
        
        public String getInstanceLabel() {
            return descriptor.getName() + " #" + instanceId;
        }
        
        private static int generateInstanceId() {
            return (int) (System.currentTimeMillis() % 100000);
        }
    }
    
    /**
     * Application event
     */
    public static class AppEvent {
        public static final String EVENT_START = "start";
        public static final String EVENT_STOP = "stop";
        public static final String EVENT_PAUSE = "pause";
        public static final String EVENT_RESUME = "resume";
        public static final String EVENT_FOCUS = "focus";
        public static final String EVENT_BLUR = "blur";
        public static final String EVENT_RESIZE = "resize";
        public static final String EVENT_MOVE = "move";
        public static final String EVENT_DATA = "data";
        public static final String EVENT_ERROR = "error";
        
        private String type;
        private AppInstance source;
        private AppInstance target;
        private Object data;
        private Date timestamp;
        private Map<String, Object> metadata;
        
        public AppEvent(String type, AppInstance source) {
            this.type = type;
            this.source = source;
            this.timestamp = new Date();
            this.metadata = new HashMap<>();
        }
        
        public AppEvent(String type, AppInstance source, Object data) {
            this(type, source);
            this.data = data;
        }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public AppInstance getSource() { return source; }
        public void setSource(AppInstance source) { this.source = source; }
        
        public AppInstance getTarget() { return target; }
        public void setTarget(AppInstance target) { this.target = target; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public void setMetadata(String key, Object value) { metadata.put(key, value); }
        public Object getMetadata(String key) { return metadata.get(key); }
        
        @Override
        public String toString() {
            return String.format("AppEvent[%s] from %s at %s", 
                type, source.getInstanceLabel(), timestamp);
        }
    }
    
    /**
     * Application listener interface
     */
    public interface AppListener {
        void onAppInstalled(AppDescriptor descriptor);
        void onAppUninstalled(String appId);
        void onAppStarted(AppInstance instance);
        void onAppStopped(AppInstance instance);
        void onAppPaused(AppInstance instance);
        void onAppResumed(AppInstance instance);
        void onAppEvent(AppEvent event);
        void onAppError(AppInstance instance, String error, Exception e);
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private NeilOS neilos;
    private String appDir;
    private Map<String, AppDescriptor> descriptors;
    private Map<String, List<AppInstance>> instances;
    private Map<String, AppDescriptor> systemApps;
    private Map<String, AppDescriptor> userApps;
    private List<AppListener> listeners;
    private ExecutorService executor;
    private boolean autoStart;
    private ThemeManager themeManager;
    
    // Application state persistence
    private String stateFile;
    private Map<String, Object> appState;
    private Map<String, Map<String, Object>> appPreferences;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Constructor
     */
    public AppManager(NeilOS neilos) {
        this(neilos, "apps");
    }
    
    /**
     * Constructor with custom app directory
     */
    public AppManager(NeilOS neilos, String appDir) {
        this.neilos = neilos;
        this.appDir = appDir;
        this.descriptors = new ConcurrentHashMap<>();
        this.instances = new ConcurrentHashMap<>();
        this.systemApps = new ConcurrentHashMap<>();
        this.userApps = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
        this.appState = new ConcurrentHashMap<>();
        this.appPreferences = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
        this.autoStart = true;
        this.stateFile = System.getProperty("user.home") + File.separator + ".neilos_apps";
        
        // Initialize directories
        initializeDirectories();
        
        // Load system apps
        loadSystemApps();
        
        // Load user apps
        loadUserApps();
        
        // Load application state
        loadAppState();
    }
    
    // ============================================================
    // INITIALIZATION
    // ============================================================
    
    /**
     * Initializes application directories
     */
    private void initializeDirectories() {
        try {
            Path path = Paths.get(appDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Failed to create app directory: " + e.getMessage());
        }
    }
    
    /**
     * Loads system applications
     */
    private void loadSystemApps() {
        // Register all system apps
        registerSystemApp("bank", "Bank", "💰", CATEGORY_SYSTEM);
        registerSystemApp("files", "Files", "📁", CATEGORY_SYSTEM);
        registerSystemApp("terminal", "Terminal", "💻", CATEGORY_SYSTEM);
        registerSystemApp("search", "Search", "🔍", CATEGORY_SYSTEM);
        registerSystemApp("cyber", "Cyber Security", "🛡️", CATEGORY_SECURITY);
        registerSystemApp("network", "Network", "🌐", CATEGORY_NETWORK);
        registerSystemApp("ai", "AI Assistant", "🤖", CATEGORY_UTILITIES);
        registerSystemApp("monitor", "System Monitor", "📊", CATEGORY_SYSTEM);
        registerSystemApp("calculator", "Calculator", "🧮", CATEGORY_UTILITIES);
        registerSystemApp("clinic", "Clinic", "🏥", CATEGORY_UTILITIES);
        registerSystemApp("social", "SocialNet", "🌍", CATEGORY_COMMUNICATION);
        registerSystemApp("deploy", "Deploy", "🚀", CATEGORY_DEVELOPMENT);
        registerSystemApp("api", "API Center", "🔌", CATEGORY_DEVELOPMENT);
        registerSystemApp("games", "Games", "🎮", CATEGORY_GAMES);
        registerSystemApp("kernel", "Kernel", "⚙️", CATEGORY_SYSTEM);
        registerSystemApp("notes", "Notes", "📝", CATEGORY_PRODUCTIVITY);
        registerSystemApp("code_studio", "Code Studio", "💻", CATEGORY_DEVELOPMENT);
        registerSystemApp("animator", "Animator", "🎬", CATEGORY_MULTIMEDIA);
    }
    
    /**
     * Registers a system application
     */
    private void registerSystemApp(String id, String name, String icon, String category) {
        AppDescriptor desc = new AppDescriptor(id, name);
        desc.setIcon(icon);
        desc.setCategory(category);
        desc.setSystemApp(true);
        desc.setVersion("1.0.0");
        desc.setAuthor("NeilOS Team");
        desc.setDescription(name + " application for NeilOS");
        desc.addTag("system");
        desc.addTag("builtin");
        desc.setPriority(PRIORITY_NORMAL);
        
        descriptors.put(id, desc);
        systemApps.put(id, desc);
        instances.put(id, new ArrayList<>());
    }
    
    /**
     * Loads user applications
     */
    private void loadUserApps() {
        try {
            File dir = new File(appDir);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".app"));
            if (files == null) return;
            
            for (File file : files) {
                try {
                    AppDescriptor desc = loadAppDescriptor(file);
                    if (desc != null) {
                        descriptors.put(desc.getId(), desc);
                        userApps.put(desc.getId(), desc);
                        instances.put(desc.getId(), new ArrayList<>());
                        notifyAppInstalled(desc);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load app from " + file.getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load user apps: " + e.getMessage());
        }
    }
    
    /**
     * Loads an app descriptor from file
     */
    private AppDescriptor loadAppDescriptor(File file) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        }
        
        String id = props.getProperty("app.id");
        String name = props.getProperty("app.name");
        if (id == null || name == null) {
            return null;
        }
        
        AppDescriptor desc = new AppDescriptor(id, name);
        desc.setVersion(props.getProperty("app.version"));
        desc.setAuthor(props.getProperty("app.author"));
        desc.setDescription(props.getProperty("app.description"));
        desc.setCategory(props.getProperty("app.category", CATEGORY_UTILITIES));
        desc.setIcon(props.getProperty("app.icon"));
        desc.setMainClass(props.getProperty("app.mainClass"));
        desc.setSystemApp(Boolean.parseBoolean(props.getProperty("app.system", "false")));
        desc.setMultiInstance(Boolean.parseBoolean(props.getProperty("app.multiInstance", "false")));
        desc.setHidden(Boolean.parseBoolean(props.getProperty("app.hidden", "false")));
        desc.setPinned(Boolean.parseBoolean(props.getProperty("app.pinned", "false")));
        desc.setInstallPath(file.getParent());
        desc.setIconPath(props.getProperty("app.iconPath"));
        desc.setWebsite(props.getProperty("app.website"));
        desc.setSupportEmail(props.getProperty("app.supportEmail"));
        
        try {
            desc.setMinMemory(Integer.parseInt(props.getProperty("app.minMemory", "64")));
            desc.setMaxMemory(Integer.parseInt(props.getProperty("app.maxMemory", "512")));
        } catch (NumberFormatException e) {
            // Ignore
        }
        
        // Load dependencies
        String deps = props.getProperty("app.dependencies");
        if (deps != null && !deps.isEmpty()) {
            for (String dep : deps.split(",")) {
                desc.addDependency(dep.trim());
            }
        }
        
        // Load permissions
        String perms = props.getProperty("app.permissions");
        if (perms != null && !perms.isEmpty()) {
            for (String perm : perms.split(",")) {
                desc.addPermission(perm.trim());
            }
        }
        
        // Load tags
        String tags = props.getProperty("app.tags");
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags.split(",")) {
                desc.addTag(tag.trim());
            }
        }
        
        // Load metadata
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith("metadata.")) {
                String metaKey = key.substring(9);
                desc.setMetadata(metaKey, entry.getValue().toString());
            }
        }
        
        return desc;
    }
    
    /**
     * Saves an app descriptor to file
     */
    public void saveAppDescriptor(AppDescriptor desc) throws IOException {
        String filename = desc.getId() + ".app";
        File file = new File(appDir, filename);
        
        Properties props = new Properties();
        props.setProperty("app.id", desc.getId());
        props.setProperty("app.name", desc.getName());
        if (desc.getVersion() != null) props.setProperty("app.version", desc.getVersion());
        if (desc.getAuthor() != null) props.setProperty("app.author", desc.getAuthor());
        if (desc.getDescription() != null) props.setProperty("app.description", desc.getDescription());
        props.setProperty("app.category", desc.getCategory());
        if (desc.getIcon() != null) props.setProperty("app.icon", desc.getIcon());
        if (desc.getMainClass() != null) props.setProperty("app.mainClass", desc.getMainClass());
        props.setProperty("app.system", String.valueOf(desc.isSystemApp()));
        props.setProperty("app.multiInstance", String.valueOf(desc.isMultiInstance()));
        props.setProperty("app.hidden", String.valueOf(desc.isHidden()));
        props.setProperty("app.pinned", String.valueOf(desc.isPinned()));
        props.setProperty("app.minMemory", String.valueOf(desc.getMinMemory()));
        props.setProperty("app.maxMemory", String.valueOf(desc.getMaxMemory()));
        
        if (!desc.getDependencies().isEmpty()) {
            props.setProperty("app.dependencies", String.join(",", desc.getDependencies()));
        }
        if (!desc.getPermissions().isEmpty()) {
            props.setProperty("app.permissions", String.join(",", desc.getPermissions()));
        }
        if (!desc.getTags().isEmpty()) {
            props.setProperty("app.tags", String.join(",", desc.getTags()));
        }
        if (desc.getWebsite() != null) props.setProperty("app.website", desc.getWebsite());
        if (desc.getSupportEmail() != null) props.setProperty("app.supportEmail", desc.getSupportEmail());
        if (desc.getIconPath() != null) props.setProperty("app.iconPath", desc.getIconPath());
        
        for (Map.Entry<String, String> entry : desc.getMetadata().entrySet()) {
            props.setProperty("metadata." + entry.getKey(), entry.getValue());
        }
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "NeilOS Application: " + desc.getName());
        }
    }
    
    // ============================================================
    // APPLICATION LIFECYCLE
    // ============================================================
    
    /**
     * Starts an application
     */
    public AppInstance startApp(String appId) {
        return startApp(appId, null);
    }
    
    /**
     * Starts an application with data
     */
    public AppInstance startApp(String appId, Map<String, Object> startData) {
        AppDescriptor desc = descriptors.get(appId);
        if (desc == null) {
            notifyError(null, "Application not found: " + appId, null);
            return null;
        }
        
        // Check if application is already running
        if (!desc.isMultiInstance()) {
            List<AppInstance> running = instances.get(appId);
            if (running != null && !running.isEmpty()) {
                AppInstance existing = running.stream()
                    .filter(inst -> inst.getState() == STATE_RUNNING || inst.getState() == STATE_PAUSED)
                    .findFirst()
                    .orElse(null);
                if (existing != null) {
                    // Bring to front
                    if (existing.getWindow() != null) {
                        existing.getWindow().toFront();
                    }
                    return existing;
                }
            }
        }
        
        // Check dependencies
        for (String dep : desc.getDependencies()) {
            if (!isAppRunning(dep)) {
                AppInstance depInstance = startApp(dep);
                if (depInstance == null) {
                    notifyError(null, "Failed to start dependency: " + dep, null);
                    return null;
                }
            }
        }
        
        // Create instance
        AppInstance instance = new AppInstance(desc);
        instance.setState(STATE_STARTING);
        instance.setStartTime(System.currentTimeMillis());
        
        // Add to instances
        instances.computeIfAbsent(appId, k -> new ArrayList<>()).add(instance);
        
        // Start application in thread
        Future<?> future = executor.submit(() -> {
            try {
                runApp(instance, startData);
            } catch (Exception e) {
                instance.setState(STATE_ERROR);
                notifyError(instance, "Application error: " + e.getMessage(), e);
            }
        });
        instance.setFuture(future);
        
        return instance;
    }
    
    /**
     * Runs an application
     */
    private void runApp(AppInstance instance, Map<String, Object> startData) {
        AppDescriptor desc = instance.getDescriptor();
        String appId = desc.getId();
        
        // Create window
        JFrame window = new JFrame(desc.getName());
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);
        
        // Apply theme
        if (themeManager != null) {
            themeManager.applyToWindow(window, themeManager.getCurrentTheme());
        }
        
        // Create content panel
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(window.getBackground());
        
        // Create application component based on appId
        Component appComponent = createAppComponent(appId, startData);
        if (appComponent != null) {
            content.add(appComponent, BorderLayout.CENTER);
            instance.setComponent(appComponent);
        }
        
        window.setContentPane(content);
        
        // Set window properties
        instance.setWindow(window);
        instance.setContent(content);
        instance.setState(STATE_RUNNING);
        instance.setVisible(true);
        
        // Window listeners
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopApp(instance);
            }
            
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                instance.setFocused(true);
                instance.updateActivity();
                notifyEvent(new AppEvent(AppEvent.EVENT_FOCUS, instance));
            }
            
            @Override
            public void windowDeactivated(java.awt.event.WindowEvent e) {
                instance.setFocused(false);
                notifyEvent(new AppEvent(AppEvent.EVENT_BLUR, instance));
            }
        });
        
        window.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                instance.setBounds(window.getBounds());
                notifyEvent(new AppEvent(AppEvent.EVENT_RESIZE, instance));
            }
            
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                instance.setBounds(window.getBounds());
                notifyEvent(new AppEvent(AppEvent.EVENT_MOVE, instance));
            }
        });
        
        // Show window
        window.setVisible(true);
        
        // Notify listeners
        notifyAppStarted(instance);
        
        // Store state
        appState.put(appId + "_state", "running");
        appState.put(appId + "_instance", instance.getInstanceId());
        
        // Wait for window to close
        while (instance.getState() != STATE_STOPPED && 
               instance.getState() != STATE_ERROR) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Creates the application component based on app ID
     */
    private Component createAppComponent(String appId, Map<String, Object> startData) {
        // This method would be expanded with actual app creation
        // For now, return a simple placeholder
        JPanel placeholder = new JPanel();
        placeholder.setBackground(new Color(20, 20, 40));
        placeholder.setLayout(new GridBagLayout());
        
        JLabel label = new JLabel("📱 " + appId);
        label.setFont(new Font("Consolas", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        placeholder.add(label);
        
        return placeholder;
    }
    
    /**
     * Stops an application
     */
    public boolean stopApp(AppInstance instance) {
        if (instance == null) {
            return false;
        }
        
        instance.setState(STATE_STOPPING);
        
        // Close window
        if (instance.getWindow() != null) {
            instance.getWindow().dispose();
        }
        
        // Cancel future
        if (instance.getFuture() != null) {
            instance.getFuture().cancel(true);
        }
        
        // Stop process
        if (instance.getProcess() != null) {
            instance.getProcess().destroyForcibly();
        }
        
        // Remove from instances        String appId = instance.getDescriptor().getId();
        List<AppInstance> appInstances = instances.get(appId);
        if (appInstances != null) {
            appInstances.remove(instance);
        }
        
        instance.setState(STATE_STOPPED);
        
        // Notify listeners
        notifyAppStopped(instance);
        
        // Update state
        appState.remove(appId + "_state");
        appState.remove(appId + "_instance");
        
        return true;
    }
    
    /**
     * Stops all instances of an application
     */
    public boolean stopApp(String appId) {
        List<AppInstance> appInstances = instances.get(appId);
        if (appInstances == null || appInstances.isEmpty()) {
            return false;
        }
        
        boolean stopped = true;
        for (AppInstance instance : new ArrayList<>(appInstances)) {
            if (!stopApp(instance)) {
                stopped = false;
            }
        }
        return stopped;
    }
    
    /**
     * Pauses an application
     */
    public boolean pauseApp(AppInstance instance) {
        if (instance == null || instance.getState() != STATE_RUNNING) {
            return false;
        }
        
        instance.setState(STATE_PAUSED);
        notifyAppPaused(instance);
        return true;
    }
    
    /**
     * Resumes an application
     */
    public boolean resumeApp(AppInstance instance) {
        if (instance == null || instance.getState() != STATE_PAUSED) {
            return false;
        }
        
        instance.setState(STATE_RUNNING);
        notifyAppResumed(instance);
        return true;
    }
    
    // ============================================================
    // APPLICATION QUERIES
    // ============================================================
    
    /**
     * Gets all application descriptors
     */
    public List<AppDescriptor> getAllApps() {
        return new ArrayList<>(descriptors.values());
    }
    
    /**
     * Gets visible applications (not hidden)
     */
    public List<AppDescriptor> getVisibleApps() {
        return descriptors.values().stream()
            .filter(desc -> !desc.isHidden())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets system applications
     */
    public List<AppDescriptor> getSystemApps() {
        return new ArrayList<>(systemApps.values());
    }
    
    /**
     * Gets user applications
     */
    public List<AppDescriptor> getUserApps() {
        return new ArrayList<>(userApps.values());
    }
    
    /**
     * Gets applications by category
     */
    public List<AppDescriptor> getAppsByCategory(String category) {
        return descriptors.values().stream()
            .filter(desc -> desc.getCategory().equals(category))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets applications by tag
     */
    public List<AppDescriptor> getAppsByTag(String tag) {
        return descriptors.values().stream()
            .filter(desc -> desc.getTags().contains(tag))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets running applications
     */
    public List<AppInstance> getRunningApps() {
        List<AppInstance> running = new ArrayList<>();
        for (List<AppInstance> appInstances : instances.values()) {
            for (AppInstance instance : appInstances) {
                if (instance.getState() == STATE_RUNNING || 
                    instance.getState() == STATE_PAUSED) {
                    running.add(instance);
                }
            }
        }
        return running;
    }
    
    /**
     * Gets an application descriptor by ID
     */
    public AppDescriptor getAppDescriptor(String appId) {
        return descriptors.get(appId);
    }
    
    /**
     * Gets all instances of an application
     */
    public List<AppInstance> getAppInstances(String appId) {
        return instances.getOrDefault(appId, new ArrayList<>());
    }
    
    /**
     * Checks if an application is running
     */
    public boolean isAppRunning(String appId) {
        List<AppInstance> appInstances = instances.get(appId);
        if (appInstances == null) {
            return false;
        }
        return appInstances.stream()
            .anyMatch(inst -> inst.getState() == STATE_RUNNING || 
                             inst.getState() == STATE_PAUSED);
    }
    
    /**
     * Gets the currently focused application
     */
    public AppInstance getFocusedApp() {
        for (List<AppInstance> appInstances : instances.values()) {
            for (AppInstance instance : appInstances) {
                if (instance.isFocused()) {
                    return instance;
                }
            }
        }
        return null;
    }
    
    // ============================================================
    // APPLICATION INSTALLATION
    // ============================================================
    
    /**
     * Installs an application
     */
    public boolean installApp(AppDescriptor desc) {
        if (descriptors.containsKey(desc.getId())) {
            return false;
        }
        
        try {
            // Save descriptor
            saveAppDescriptor(desc);
            
            // Add to managers
            descriptors.put(desc.getId(), desc);
            userApps.put(desc.getId(), desc);
            instances.put(desc.getId(), new ArrayList<>());
            
            // Notify listeners
            notifyAppInstalled(desc);
            
            // Update state
            appState.put(desc.getId() + "_installed", "true");
            appState.put(desc.getId() + "_installed_at", new Date().toString());
            
            return true;
        } catch (IOException e) {
            notifyError(null, "Failed to install app: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Uninstalls an application
     */
    public boolean uninstallApp(String appId) {
        AppDescriptor desc = descriptors.get(appId);
        if (desc == null || desc.isSystemApp()) {
            return false;
        }
        
        // Stop all instances
        stopApp(appId);
        
        // Remove descriptor file
        try {
            String filename = appId + ".app";
            File file = new File(appDir, filename);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // Remove from managers
        descriptors.remove(appId);
        userApps.remove(appId);
        instances.remove(appId);
        
        // Notify listeners
        notifyAppUninstalled(appId);
        
        // Update state
        appState.remove(appId + "_installed");
        appState.remove(appId + "_installed_at");
        
        return true;
    }
    
    // ============================================================
    // APPLICATION PREFERENCES
    // ============================================================
    
    /**
     * Saves application preferences
     */
    public void saveAppPreferences(String appId, Map<String, Object> prefs) {
        appPreferences.put(appId, new HashMap<>(prefs));
        saveAppState();
    }
    
    /**
     * Gets application preferences
     */
    public Map<String, Object> getAppPreferences(String appId) {
        return appPreferences.getOrDefault(appId, new HashMap<>());
    }
    
    /**
     * Gets a specific preference value
     */
    public Object getAppPreference(String appId, String key, Object defaultValue) {
        Map<String, Object> prefs = appPreferences.get(appId);
        if (prefs == null) {
            return defaultValue;
        }
        return prefs.getOrDefault(key, defaultValue);
    }
    
    /**
     * Sets a specific preference value
     */
    public void setAppPreference(String appId, String key, Object value) {
        Map<String, Object> prefs = appPreferences.computeIfAbsent(appId, k -> new HashMap<>());
        prefs.put(key, value);
        saveAppState();
    }
    
    // ============================================================
    // STATE PERSISTENCE
    // ============================================================
    
    /**
     * Saves application state
     */
    private void saveAppState() {
        try {
            Properties props = new Properties();
            
            // Save app state
            for (Map.Entry<String, Object> entry : appState.entrySet()) {
                props.setProperty("state." + entry.getKey(), entry.getValue().toString());
            }
            
            // Save app preferences
            for (Map.Entry<String, Map<String, Object>> entry : appPreferences.entrySet()) {
                String appId = entry.getKey();
                Map<String, Object> prefs = entry.getValue();
                for (Map.Entry<String, Object> pref : prefs.entrySet()) {
                    props.setProperty("pref." + appId + "." + pref.getKey(), 
                        pref.getValue() != null ? pref.getValue().toString() : "");
                }
            }
            
            try (FileOutputStream fos = new FileOutputStream(stateFile)) {
                props.store(fos, "NeilOS Application State");
            }
        } catch (IOException e) {
            System.err.println("Failed to save app state: " + e.getMessage());
        }
    }
    
    /**
     * Loads application state
     */
    private void loadAppState() {
        try {
            File file = new File(stateFile);
            if (!file.exists()) {
                return;
            }
            
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            }
            
            // Load app state
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                
                if (key.startsWith("state.")) {
                    String stateKey = key.substring(6);
                    appState.put(stateKey, value);
                } else if (key.startsWith("pref.")) {
                    String[] parts = key.substring(5).split("\\.", 2);
                    if (parts.length == 2) {
                        String appId = parts[0];
                        String prefKey = parts[1];
                        Map<String, Object> prefs = appPreferences.computeIfAbsent(appId, k -> new HashMap<>());
                        prefs.put(prefKey, value);
                    }
                }
            }
            
            // Auto-start applications if enabled
            if (autoStart) {
                for (AppDescriptor desc : descriptors.values()) {
                    String state = (String) appState.get(desc.getId() + "_state");
                    if ("running".equals(state)) {
                        startApp(desc.getId());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load app state: " + e.getMessage());
        }
    }
    
    // ============================================================
    // EVENT SYSTEM
    // ============================================================
    
    /**
     * Adds an application listener
     */
    public void addListener(AppListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes an application listener
     */
    public void removeListener(AppListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies listeners of app installation
     */
    private void notifyAppInstalled(AppDescriptor desc) {
        for (AppListener listener : listeners) {
            try {
                listener.onAppInstalled(desc);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of app uninstallation
     */
    private void notifyAppUninstalled(String appId) {
        for (AppListener listener : listeners) {
            try {
                listener.onAppUninstalled(appId);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of app start
     */
    private void notifyAppStarted(AppInstance instance) {
        for (AppListener listener : listeners) {
            try {
                listener.onAppStarted(instance);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of app stop
     */
    private void notifyAppStopped(AppInstance instance) {
        for (AppListener listener : listeners) {
            try {
                listener.onAppStopped(instance);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of app pause
     */
    private void notifyAppPaused(AppInstance instance) {
        for (AppListener listener : listeners) {
            try {
                listener.onAppPaused(instance);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of app resume
     */
    private void notifyAppResumed(AppInstance instance) {
        for (AppListener listener : listeners) {
            try {
                listener.onAppResumed(instance);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of app event
     */
    private void notifyEvent(AppEvent event) {
        for (AppListener listener : listeners) {
            try {
                listener.onAppEvent(event);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of app error
     */
    private void notifyError(AppInstance instance, String error, Exception e) {
        for (AppListener listener : listeners) {
            try {
                listener.onAppError(instance, error, e);
            } catch (Exception ex) {
                // Ignore
            }
        }
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public NeilOS getNeilOS() { return neilos; }
    public void setNeilOS(NeilOS neilos) { this.neilos = neilos; }
    
    public String getAppDir() { return appDir; }
    public void setAppDir(String appDir) { this.appDir = appDir; }
    
    public boolean isAutoStart() { return autoStart; }
    public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
    
    public ThemeManager getThemeManager() { return themeManager; }
    public void setThemeManager(ThemeManager themeManager) { 
        this.themeManager = themeManager; 
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Shuts down all applications
     */
    public void shutdownAll() {
        for (String appId : new ArrayList<>(instances.keySet())) {
            stopApp(appId);
        }
        executor.shutdown();
    }
    
    /**
     * Gets application statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_apps", descriptors.size());
        stats.put("system_apps", systemApps.size());
        stats.put("user_apps", userApps.size());
        stats.put("running_apps", getRunningApps().size());
        stats.put("total_instances", instances.values().stream().mapToInt(List::size).sum());
        return stats;
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of AppManager
     */
    public static void main(String[] args) {
        System.out.println("📱 AppManager Demo");
        System.out.println("═".repeat(60));
        
        // Create AppManager
        NeilOS neilos = new NeilOS();
        AppManager appManager = new AppManager(neilos);
        
        // List all apps
        System.out.println("\n📋 Available Applications:");
        for (AppDescriptor desc : appManager.getAllApps()) {
            System.out.println("  " + desc.getIcon() + " " + desc.getName() + 
                " (" + desc.getId() + ") - " + desc.getCategory());
        }
        
        // Get app by category
        System.out.println("\n📂 System Applications:");
        for (AppDescriptor desc : appManager.getSystemApps()) {
            System.out.println("  " + desc.getIcon() + " " + desc.getName());
        }
        
        // Get app details
        AppDescriptor kernel = appManager.getAppDescriptor("kernel");
        if (kernel != null) {
            System.out.println("\n📊 Kernel App Details:");
            System.out.println(kernel.toDetailedString());
        }
        
        // Start an app
        System.out.println("\n🚀 Starting Kernel App:");
        AppInstance instance = appManager.startApp("kernel");
        if (instance != null) {
            System.out.println("  ✅ Started: " + instance.getInstanceLabel());
            System.out.println("  State: " + instance.getStateString());
            
            // Wait a bit
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Stop the app
            System.out.println("\n🛑 Stopping Kernel App:");
            appManager.stopApp(instance);
            System.out.println("  ✅ Stopped: " + instance.getInstanceLabel());
        }
        
        // Get stats
        System.out.println("\n📊 Application Statistics:");
        Map<String, Object> stats = appManager.getStats();
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        // Shutdown
        appManager.shutdownAll();
        
        System.out.println("\n✅ Demo completed!");
    }
}