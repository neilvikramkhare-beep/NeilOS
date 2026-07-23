package com.neilos.ui;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

/**
 * ThemeManager - Advanced theme management for NeilOS
 * Provides comprehensive theming support with multiple themes,
 * custom color schemes, font management, and dynamic theme switching.
 * 
 * Features:
 * - Multiple built-in themes (Dark, Light, Blue, Matrix, Neon, etc.)
 * - Custom theme creation and loading
 * - Color scheme management
 * - Font management
 * - Component-specific styling
 * - Theme preview
 * - Theme persistence
 * - Dynamic theme switching without restart
 * - CSS-like styling support
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class ThemeManager {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default theme name */
    public static final String DEFAULT_THEME = "Dark";
    
    /** Theme file extension */
    public static final String THEME_EXTENSION = ".neilos-theme";
    
    /** Theme directory name */
    public static final String THEME_DIR = "themes";
    
    /** Built-in themes */
    public static final String[] BUILTIN_THEMES = {
        "Dark", "Light", "Blue", "Matrix", "Neon", "Ocean", 
        "Solarized", "Dracula", "Monokai", "Nord", "Gruvbox",
        "Material", "Pastel", "Retro", "Cyberpunk", "Minimal"
    };
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * Theme definition containing all styling information
     */
    public static class Theme {
        private String name;
        private String description;
        private String author;
        private String version;
        private boolean isBuiltin;
        private Map<String, Color> colors;
        private Map<String, Font> fonts;
        private Map<String, String> styles;
        private Map<String, Object> components;
        private Map<String, String> cssProperties;
        private String baseTheme;
        private List<String> tags;
        private Date createdAt;
        private Date updatedAt;
        
        public Theme(String name) {
            this.name = name;
            this.colors = new ConcurrentHashMap<>();
            this.fonts = new ConcurrentHashMap<>();
            this.styles = new ConcurrentHashMap<>();
            this.components = new ConcurrentHashMap<>();
            this.cssProperties = new ConcurrentHashMap<>();
            this.tags = new ArrayList<>();
            this.createdAt = new Date();
            this.updatedAt = new Date();
            this.isBuiltin = false;
        }
        
        public Theme(String name, boolean isBuiltin) {
            this(name);
            this.isBuiltin = isBuiltin;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public boolean isBuiltin() { return isBuiltin; }
        public void setBuiltin(boolean builtin) { isBuiltin = builtin; }
        
        public Map<String, Color> getColors() { return new HashMap<>(colors); }
        public void setColors(Map<String, Color> colors) { 
            this.colors = colors != null ? new ConcurrentHashMap<>(colors) : new ConcurrentHashMap<>();
        }
        
        public Color getColor(String key) {
            return colors.get(key);
        }
        
        public void setColor(String key, Color color) {
            colors.put(key, color);
            updatedAt = new Date();
        }
        
        public Color getColor(String key, Color defaultColor) {
            return colors.getOrDefault(key, defaultColor);
        }
        
        public Map<String, Font> getFonts() { return new HashMap<>(fonts); }
        public void setFonts(Map<String, Font> fonts) {
            this.fonts = fonts != null ? new ConcurrentHashMap<>(fonts) : new ConcurrentHashMap<>();
        }
        
        public Font getFont(String key) {
            return fonts.get(key);
        }
        
        public void setFont(String key, Font font) {
            fonts.put(key, font);
            updatedAt = new Date();
        }
        
        public Font getFont(String key, Font defaultFont) {
            return fonts.getOrDefault(key, defaultFont);
        }
        
        public Map<String, String> getStyles() { return new HashMap<>(styles); }
        public void setStyles(Map<String, String> styles) {
            this.styles = styles != null ? new ConcurrentHashMap<>(styles) : new ConcurrentHashMap<>();
        }
        
        public String getStyle(String key) {
            return styles.get(key);
        }
        
        public void setStyle(String key, String value) {
            styles.put(key, value);
            updatedAt = new Date();
        }
        
        public Map<String, Object> getComponents() { return new HashMap<>(components); }
        public void setComponents(Map<String, Object> components) {
            this.components = components != null ? new ConcurrentHashMap<>(components) : new ConcurrentHashMap<>();
        }
        
        public Object getComponent(String key) {
            return components.get(key);
        }
        
        public void setComponent(String key, Object value) {
            components.put(key, value);
            updatedAt = new Date();
        }
        
        public Map<String, String> getCssProperties() { return new HashMap<>(cssProperties); }
        public void setCssProperties(Map<String, String> cssProperties) {
            this.cssProperties = cssProperties != null ? new ConcurrentHashMap<>(cssProperties) : new ConcurrentHashMap<>();
        }
        
        public String getCssProperty(String key) {
            return cssProperties.get(key);
        }
        
        public void setCssProperty(String key, String value) {
            cssProperties.put(key, value);
            updatedAt = new Date();
        }
        
        public String getBaseTheme() { return baseTheme; }
        public void setBaseTheme(String baseTheme) { this.baseTheme = baseTheme; }
        
        public List<String> getTags() { return new ArrayList<>(tags); }
        public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }
        public void addTag(String tag) { 
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }
        public void removeTag(String tag) { tags.remove(tag); }
        
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        
        public Date getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
        
        /**
         * Applies the theme to a component
         */
        public void applyTo(JComponent component) {
            if (component == null) return;
            
            // Apply colors
            for (Map.Entry<String, Color> entry : colors.entrySet()) {
                String key = entry.getKey();
                Color color = entry.getValue();
                
                switch (key) {
                    case "background":
                        component.setBackground(color);
                        break;
                    case "foreground":
                        component.setForeground(color);
                        break;
                    case "selectionBackground":
                        component.setSelectionBackground(color);
                        break;
                    case "selectionForeground":
                        component.setSelectionForeground(color);
                        break;
                    case "disabledBackground":
                        component.setDisabledBackground(color);
                        break;
                    case "disabledForeground":
                        component.setDisabledForeground(color);
                        break;
                    case "border":
                        if (component instanceof JComponent) {
                            ((JComponent) component).setBorder(
                                BorderFactory.createLineBorder(color)
                            );
                        }
                        break;
                }
            }
            
            // Apply fonts
            for (Map.Entry<String, Font> entry : fonts.entrySet()) {
                String key = entry.getKey();
                Font font = entry.getValue();
                
                switch (key) {
                    case "font":
                        component.setFont(font);
                        break;
                }
            }
            
            // Apply styles
            for (Map.Entry<String, String> entry : styles.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                
                if (key.equals("opacity") && component instanceof JComponent) {
                    try {
                        float opacity = Float.parseFloat(value);
                        ((JComponent) component).setOpaque(opacity > 0.5f);
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            return name + (description != null ? " - " + description : "");
        }
        
        public String toDetailedString() {
            StringBuilder sb = new StringBuilder();
            sb.append("═".repeat(60)).append("\n");
            sb.append("🎨 THEME: ").append(name).append("\n");
            sb.append("═".repeat(60)).append("\n");
            sb.append("Description:  ").append(description != null ? description : "N/A").append("\n");
            sb.append("Author:       ").append(author != null ? author : "Unknown").append("\n");
            sb.append("Version:      ").append(version != null ? version : "1.0").append("\n");
            sb.append("Builtin:      ").append(isBuiltin ? "✅ Yes" : "❌ No").append("\n");
            sb.append("Base Theme:   ").append(baseTheme != null ? baseTheme : "None").append("\n");
            sb.append("Created:      ").append(createdAt).append("\n");
            sb.append("Updated:      ").append(updatedAt).append("\n");
            
            if (!tags.isEmpty()) {
                sb.append("Tags:         ").append(String.join(", ", tags)).append("\n");
            }
            
            if (!colors.isEmpty()) {
                sb.append("\n🎨 Colors:\n");
                for (Map.Entry<String, Color> entry : colors.entrySet()) {
                    sb.append("  ").append(entry.getKey()).append(": ")
                      .append(colorToHex(entry.getValue())).append("\n");
                }
            }
            
            if (!fonts.isEmpty()) {
                sb.append("\n🔤 Fonts:\n");
                for (Map.Entry<String, Font> entry : fonts.entrySet()) {
                    sb.append("  ").append(entry.getKey()).append(": ")
                      .append(entry.getValue().getName())
                      .append(" ").append(entry.getValue().getSize())
                      .append(" ").append(entry.getValue().getStyle()).append("\n");
                }
            }
            
            sb.append("═".repeat(60)).append("\n");
            return sb.toString();
        }
    }
    
    /**
     * Theme change event
     */
    public static class ThemeChangeEvent {
        private Theme oldTheme;
        private Theme newTheme;
        private Date timestamp;
        private String source;
        
        public ThemeChangeEvent(Theme oldTheme, Theme newTheme) {
            this.oldTheme = oldTheme;
            this.newTheme = newTheme;
            this.timestamp = new Date();
            this.source = "Unknown";
        }
        
        public ThemeChangeEvent(Theme oldTheme, Theme newTheme, String source) {
            this(oldTheme, newTheme);
            this.source = source;
        }
        
        public Theme getOldTheme() { return oldTheme; }
        public Theme getNewTheme() { return newTheme; }
        public Date getTimestamp() { return timestamp; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }
    
    /**
     * Theme listener interface
     */
    public interface ThemeListener {
        void onThemeChanged(ThemeChangeEvent event);
        void onThemeLoaded(Theme theme);
        void onThemeSaved(Theme theme);
        void onThemeDeleted(String themeName);
        void onError(String message, Exception e);
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private String themeDir;
    private Theme currentTheme;
    private Map<String, Theme> themes;
    private List<ThemeListener> listeners;
    private boolean autoApply;
    private boolean persist;
    private String lastAppliedTheme;
    private Map<String, Object> componentOverrides;
    private UIManager.LookAndFeelInfo[] lookAndFeels;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor
     */
    public ThemeManager() {
        this(THEME_DIR);
    }
    
    /**
     * Constructor with custom theme directory
     * 
     * @param themeDir The theme directory path
     */
    public ThemeManager(String themeDir) {
        this.themeDir = themeDir;
        this.themes = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
        this.componentOverrides = new ConcurrentHashMap<>();
        this.autoApply = true;
        this.persist = true;
        
        // Initialize theme directory
        initializeThemeDirectory();
        
        // Load built-in themes
        loadBuiltinThemes();
        
        // Load custom themes
        loadCustomThemes();
        
        // Set default theme
        if (themes.containsKey(DEFAULT_THEME)) {
            currentTheme = themes.get(DEFAULT_THEME);
        } else if (!themes.isEmpty()) {
            currentTheme = themes.values().iterator().next();
        } else {
            // Create default theme if none exist
            currentTheme = createDefaultTheme();
            themes.put(currentTheme.getName(), currentTheme);
        }
        
        // Apply current theme
        applyTheme(currentTheme);
        
        // Load saved theme preference
        loadThemePreference();
    }
    
    // ============================================================
    // INITIALIZATION
    // ============================================================
    
    /**
     * Initializes the theme directory
     */
    private void initializeThemeDirectory() {
        try {
            Path path = Paths.get(themeDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Failed to create theme directory: " + e.getMessage());
        }
    }
    
    /**
     * Creates the default theme
     */
    private Theme createDefaultTheme() {
        Theme theme = new Theme(DEFAULT_THEME, true);
        theme.setDescription("Default dark theme for NeilOS");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        // Dark theme colors
        theme.setColor("background", new Color(11, 16, 32));
        theme.setColor("foreground", new Color(0, 255, 238));
        theme.setColor("primary", new Color(0, 255, 238));
        theme.setColor("secondary", new Color(16, 24, 38));
        theme.setColor("accent", new Color(0, 255, 136));
        theme.setColor("error", new Color(239, 68, 68));
        theme.setColor("warning", new Color(245, 158, 11));
        theme.setColor("success", new Color(16, 185, 129));
        theme.setColor("info", new Color(59, 130, 246));
        theme.setColor("text", new Color(255, 255, 255));
        theme.setColor("textSecondary", new Color(160, 174, 192));
        theme.setColor("border", new Color(30, 41, 59));
        theme.setColor("hover", new Color(30, 41, 59));
        theme.setColor("selectionBackground", new Color(0, 255, 238, 50));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        theme.setColor("disabledBackground", new Color(30, 30, 30));
        theme.setColor("disabledForeground", new Color(100, 100, 100));
        theme.setColor("shadow", new Color(0, 0, 0, 100));
        theme.setColor("scrollbar", new Color(30, 41, 59));
        theme.setColor("scrollbarTrack", new Color(11, 16, 32));
        
        // Fonts
        Font defaultFont = new Font("Consolas", Font.PLAIN, 12);
        theme.setFont("default", defaultFont);
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("small", new Font("Consolas", Font.PLAIN, 10));
        theme.setFont("large", new Font("Consolas", Font.PLAIN, 16));
        
        // Styles
        theme.setStyle("opacity", "1.0");
        theme.setStyle("borderRadius", "8");
        theme.setStyle("shadowSize", "4");
        
        // Tags
        theme.addTag("dark");
        theme.addTag("default");
        theme.addTag("system");
        
        return theme;
    }
    
    // ============================================================
    // BUILT-IN THEMES
    // ============================================================
    
    /**
     * Loads all built-in themes
     */
    private void loadBuiltinThemes() {
        // Dark Theme
        Theme dark = createDarkTheme();
        themes.put(dark.getName(), dark);
        
        // Light Theme
        Theme light = createLightTheme();
        themes.put(light.getName(), light);
        
        // Blue Theme
        Theme blue = createBlueTheme();
        themes.put(blue.getName(), blue);
        
        // Matrix Theme
        Theme matrix = createMatrixTheme();
        themes.put(matrix.getName(), matrix);
        
        // Neon Theme
        Theme neon = createNeonTheme();
        themes.put(neon.getName(), neon);
        
        // Ocean Theme
        Theme ocean = createOceanTheme();
        themes.put(ocean.getName(), ocean);
        
        // Solarized Theme
        Theme solarized = createSolarizedTheme();
        themes.put(solarized.getName(), solarized);
        
        // Dracula Theme
        Theme dracula = createDraculaTheme();
        themes.put(dracula.getName(), dracula);
        
        // Monokai Theme
        Theme monokai = createMonokaiTheme();
        themes.put(monokai.getName(), monokai);
        
        // Nord Theme
        Theme nord = createNordTheme();
        themes.put(nord.getName(), nord);
        
        // Gruvbox Theme
        Theme gruvbox = createGruvboxTheme();
        themes.put(gruvbox.getName(), gruvbox);
        
        // Material Theme
        Theme material = createMaterialTheme();
        themes.put(material.getName(), material);
        
        // Pastel Theme
        Theme pastel = createPastelTheme();
        themes.put(pastel.getName(), pastel);
        
        // Retro Theme
        Theme retro = createRetroTheme();
        themes.put(retro.getName(), retro);
        
        // Cyberpunk Theme
        Theme cyberpunk = createCyberpunkTheme();
        themes.put(cyberpunk.getName(), cyberpunk);
        
        // Minimal Theme
        Theme minimal = createMinimalTheme();
        themes.put(minimal.getName(), minimal);
    }
    
    /**
     * Creates Dark theme
     */
    private Theme createDarkTheme() {
        Theme theme = new Theme("Dark", true);
        theme.setDescription("Classic dark theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(18, 18, 30));
        theme.setColor("foreground", new Color(0, 255, 238));
        theme.setColor("primary", new Color(0, 255, 238));
        theme.setColor("secondary", new Color(30, 30, 50));
        theme.setColor("accent", new Color(0, 255, 136));
        theme.setColor("error", new Color(239, 68, 68));
        theme.setColor("warning", new Color(245, 158, 11));
        theme.setColor("success", new Color(16, 185, 129));
        theme.setColor("info", new Color(59, 130, 246));
        theme.setColor("text", new Color(255, 255, 255));
        theme.setColor("textSecondary", new Color(160, 174, 192));
        theme.setColor("border", new Color(40, 40, 60));
        theme.setColor("hover", new Color(40, 40, 60));
        theme.setColor("selectionBackground", new Color(0, 255, 238, 40));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("dark");
        theme.addTag("classic");
        return theme;
    }
    
    /**
     * Creates Light theme
     */
    private Theme createLightTheme() {
        Theme theme = new Theme("Light", true);
        theme.setDescription("Clean light theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(245, 245, 250));
        theme.setColor("foreground", new Color(30, 30, 50));
        theme.setColor("primary", new Color(30, 30, 50));
        theme.setColor("secondary", new Color(235, 235, 240));
        theme.setColor("accent", new Color(0, 150, 136));
        theme.setColor("error", new Color(200, 50, 50));
        theme.setColor("warning", new Color(200, 150, 0));
        theme.setColor("success", new Color(0, 150, 100));
        theme.setColor("info", new Color(50, 100, 200));
        theme.setColor("text", new Color(30, 30, 50));
        theme.setColor("textSecondary", new Color(100, 100, 120));
        theme.setColor("border", new Color(200, 200, 210));
        theme.setColor("hover", new Color(220, 220, 230));
        theme.setColor("selectionBackground", new Color(30, 30, 50, 40));
        theme.setColor("selectionForeground", new Color(30, 30, 50));
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("light");
        theme.addTag("clean");
        return theme;
    }
    
    /**
     * Creates Blue theme
     */
    private Theme createBlueTheme() {
        Theme theme = new Theme("Blue", true);
        theme.setDescription("Calm blue theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(10, 15, 40));
        theme.setColor("foreground", new Color(100, 180, 255));
        theme.setColor("primary", new Color(59, 130, 246));
        theme.setColor("secondary", new Color(20, 25, 50));
        theme.setColor("accent", new Color(100, 180, 255));
        theme.setColor("error", new Color(239, 68, 68));
        theme.setColor("warning", new Color(245, 158, 11));
        theme.setColor("success", new Color(16, 185, 129));
        theme.setColor("info", new Color(59, 130, 246));
        theme.setColor("text", new Color(200, 220, 255));
        theme.setColor("textSecondary", new Color(130, 150, 200));
        theme.setColor("border", new Color(40, 50, 80));
        theme.setColor("hover", new Color(30, 40, 70));
        theme.setColor("selectionBackground", new Color(59, 130, 246, 40));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("blue");
        theme.addTag("calm");
        return theme;
    }
    
    /**
     * Creates Matrix theme
     */
    private Theme createMatrixTheme() {
        Theme theme = new Theme("Matrix", true);
        theme.setDescription("Matrix-inspired green theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(0, 10, 0));
        theme.setColor("foreground", new Color(0, 255, 0));
        theme.setColor("primary", new Color(0, 255, 0));
        theme.setColor("secondary", new Color(0, 20, 0));
        theme.setColor("accent", new Color(0, 255, 136));
        theme.setColor("error", new Color(255, 0, 0));
        theme.setColor("warning", new Color(255, 255, 0));
        theme.setColor("success", new Color(0, 255, 0));
        theme.setColor("info", new Color(0, 200, 255));
        theme.setColor("text", new Color(0, 255, 0));
        theme.setColor("textSecondary", new Color(0, 180, 0));
        theme.setColor("border", new Color(0, 50, 0));
        theme.setColor("hover", new Color(0, 30, 0));
        theme.setColor("selectionBackground", new Color(0, 255, 0, 30));
        theme.setColor("selectionForeground", new Color(0, 255, 0));
        
        theme.setFont("default", new Font("Monospaced", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Monospaced", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Monospaced", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Monospaced", Font.PLAIN, 12));
        
        theme.addTag("matrix");
        theme.addTag("green");
        theme.addTag("hacker");
        return theme;
    }
    
    /**
     * Creates Neon theme
     */
    private Theme createNeonTheme() {
        Theme theme = new Theme("Neon", true);
        theme.setDescription("Vibrant neon theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(20, 10, 30));
        theme.setColor("foreground", new Color(255, 0, 200));
        theme.setColor("primary", new Color(255, 0, 200));
        theme.setColor("secondary", new Color(30, 15, 45));
        theme.setColor("accent", new Color(0, 255, 255));
        theme.setColor("error", new Color(255, 0, 0));
        theme.setColor("warning", new Color(255, 200, 0));
        theme.setColor("success", new Color(0, 255, 100));
        theme.setColor("info", new Color(0, 200, 255));
        theme.setColor("text", new Color(255, 200, 240));
        theme.setColor("textSecondary", new Color(180, 100, 160));
        theme.setColor("border", new Color(60, 30, 80));
        theme.setColor("hover", new Color(50, 25, 70));
        theme.setColor("selectionBackground", new Color(255, 0, 200, 30));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("neon");
        theme.addTag("vibrant");
        theme.addTag("cyberpunk");
        return theme;
    }
    
    /**
     * Creates Ocean theme
     */
    private Theme createOceanTheme() {
        Theme theme = new Theme("Ocean", true);
        theme.setDescription("Ocean-inspired blue-green theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(8, 28, 48));
        theme.setColor("foreground", new Color(100, 220, 255));
        theme.setColor("primary", new Color(100, 220, 255));
        theme.setColor("secondary", new Color(15, 40, 65));
        theme.setColor("accent", new Color(0, 255, 200));
        theme.setColor("error", new Color(255, 80, 80));
        theme.setColor("warning", new Color(255, 200, 50));
        theme.setColor("success", new Color(50, 255, 150));
        theme.setColor("info", new Color(100, 200, 255));
        theme.setColor("text", new Color(200, 240, 255));
        theme.setColor("textSecondary", new Color(130, 180, 210));
        theme.setColor("border", new Color(30, 60, 85));
        theme.setColor("hover", new Color(20, 50, 75));
        theme.setColor("selectionBackground", new Color(100, 220, 255, 30));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("ocean");
        theme.addTag("blue");
        theme.addTag("calm");
        return theme;
    }
    
    /**
     * Creates Solarized theme
     */
    private Theme createSolarizedTheme() {
        Theme theme = new Theme("Solarized", true);
        theme.setDescription("Solarized color scheme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        // Solarized colors
        Color base03 = new Color(0, 43, 54);
        Color base02 = new Color(7, 54, 66);
        Color base01 = new Color(88, 110, 117);
        Color base00 = new Color(101, 123, 131);
        Color base0 = new Color(131, 148, 150);
        Color base1 = new Color(147, 161, 161);
        Color base2 = new Color(238, 232, 213);
        Color base3 = new Color(253, 246, 227);
        Color yellow = new Color(181, 137, 0);
        Color orange = new Color(203, 75, 22);
        Color red = new Color(220, 50, 47);
        Color magenta = new Color(211, 54, 130);
        Color violet = new Color(108, 113, 196);
        Color blue = new Color(38, 139, 210);
        Color cyan = new Color(42, 161, 152);
        Color green = new Color(133, 153, 0);
        
        theme.setColor("background", base03);
        theme.setColor("foreground", base0);
        theme.setColor("primary", cyan);
        theme.setColor("secondary", base02);
        theme.setColor("accent", green);
        theme.setColor("error", red);
        theme.setColor("warning", orange);
        theme.setColor("success", green);
        theme.setColor("info", blue);
        theme.setColor("text", base2);
        theme.setColor("textSecondary", base01);
        theme.setColor("border", base02);
        theme.setColor("hover", base02);
        theme.setColor("selectionBackground", cyan);
        theme.setColor("selectionForeground", base3);
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("solarized");
        theme.addTag("calm");
        return theme;
    }
    
    /**
     * Creates Dracula theme
     */
    private Theme createDraculaTheme() {
        Theme theme = new Theme("Dracula", true);
        theme.setDescription("Dracula color scheme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        Color bg = new Color(40, 42, 54);
        Color fg = new Color(248, 248, 242);
        Color comment = new Color(98, 114, 164);
        Color cyan = new Color(139, 233, 253);
        Color green = new Color(80, 250, 123);
        Color orange = new Color(255, 184, 108);
        Color pink = new Color(255, 121, 198);
        Color purple = new Color(189, 147, 249);
        Color red = new Color(255, 85, 85);
        Color yellow = new Color(241, 250, 140);
        
        theme.setColor("background", bg);
        theme.setColor("foreground", fg);
        theme.setColor("primary", purple);
        theme.setColor("secondary", new Color(50, 52, 65));
        theme.setColor("accent", cyan);
        theme.setColor("error", red);
        theme.setColor("warning", yellow);
        theme.setColor("success", green);
        theme.setColor("info", cyan);
        theme.setColor("text", fg);
        theme.setColor("textSecondary", comment);
        theme.setColor("border", new Color(60, 62, 75));
        theme.setColor("hover", new Color(55, 57, 70));
        theme.setColor("selectionBackground", purple);
        theme.setColor("selectionForeground", fg);
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("dracula");
        theme.addTag("dark");
        return theme;
    }
    
    /**
     * Creates Monokai theme
     */
    private Theme createMonokaiTheme() {
        Theme theme = new Theme("Monokai", true);
        theme.setDescription("Monokai color scheme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        Color bg = new Color(39, 40, 34);
        Color fg = new Color(248, 248, 242);
        Color yellow = new Color(230, 219, 116);
        Color orange = new Color(253, 151, 31);
        Color pink = new Color(249, 38, 114);
        Color purple = new Color(174, 129, 255);
        Color blue = new Color(102, 217, 239);
        Color green = new Color(166, 226, 46);
        Color red = new Color(249, 38, 114);
        Color gray = new Color(117, 113, 94);
        
        theme.setColor("background", bg);
        theme.setColor("foreground", fg);
        theme.setColor("primary", pink);
        theme.setColor("secondary", new Color(49, 50, 44));
        theme.setColor("accent", green);
        theme.setColor("error", red);
        theme.setColor("warning", orange);
        theme.setColor("success", green);
        theme.setColor("info", blue);
        theme.setColor("text", fg);
        theme.setColor("textSecondary", gray);
        theme.setColor("border", new Color(59, 60, 54));
        theme.setColor("hover", new Color(54, 55, 49));
        theme.setColor("selectionBackground", pink);
        theme.setColor("selectionForeground", fg);
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("monokai");
        theme.addTag("dark");
        return theme;
    }
    
    /**
     * Creates Nord theme
     */
    private Theme createNordTheme() {
        Theme theme = new Theme("Nord", true);
        theme.setDescription("Nord color scheme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        Color nord0 = new Color(46, 52, 64);
        Color nord1 = new Color(59, 66, 82);
        Color nord2 = new Color(67, 76, 94);
        Color nord3 = new Color(76, 86, 106);
        Color nord4 = new Color(216, 222, 233);
        Color nord5 = new Color(229, 233, 240);
        Color nord6 = new Color(236, 239, 244);
        Color nord7 = new Color(143, 188, 187);
        Color nord8 = new Color(136, 192, 208);
        Color nord9 = new Color(129, 161, 193);
        Color nord10 = new Color(94, 129, 172);
        Color nord11 = new Color(191, 97, 106);
        Color nord12 = new Color(208, 135, 112);
        Color nord13 = new Color(235, 203, 139);
        Color nord14 = new Color(163, 190, 140);
        Color nord15 = new Color(180, 142, 173);
        
        theme.setColor("background", nord0);
        theme.setColor("foreground", nord4);
        theme.setColor("primary", nord9);
        theme.setColor("secondary", nord1);
        theme.setColor("accent", nord8);
        theme.setColor("error", nord11);
        theme.setColor("warning", nord13);
        theme.setColor("success", nord14);
        theme.setColor("info", nord9);
        theme.setColor("text", nord5);
        theme.setColor("textSecondary", nord3);
        theme.setColor("border", nord2);
        theme.setColor("hover", nord2);
        theme.setColor("selectionBackground", nord9);
        theme.setColor("selectionForeground", nord6);
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("nord");
        theme.addTag("calm");
        theme.addTag("blue");
        return theme;
    }
    
    /**
     * Creates Gruvbox theme
     */
    private Theme createGruvboxTheme() {
        Theme theme = new Theme("Gruvbox", true);
        theme.setDescription("Gruvbox color scheme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        Color bg = new Color(40, 40, 40);
        Color fg = new Color(235, 219, 178);
        Color red = new Color(204, 36, 29);
        Color green = new Color(152, 151, 26);
        Color yellow = new Color(215, 153, 33);
        Color blue = new Color(69, 133, 136);
        Color purple = new Color(177, 98, 134);
        Color aqua = new Color(104, 157, 106);
        Color orange = new Color(214, 93, 14);
        Color gray = new Color(146, 131, 116);
        
        theme.setColor("background", bg);
        theme.setColor("foreground", fg);
        theme.setColor("primary", yellow);
        theme.setColor("secondary", new Color(50, 50, 50));
        theme.setColor("accent", aqua);
        theme.setColor("error", red);
        theme.setColor("warning", orange);
        theme.setColor("success", green);
        theme.setColor("info", blue);
        theme.setColor("text", fg);
        theme.setColor("textSecondary", gray);
        theme.setColor("border", new Color(60, 60, 60));
        theme.setColor("hover", new Color(55, 55, 55));
        theme.setColor("selectionBackground", yellow);
        theme.setColor("selectionForeground", bg);
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("gruvbox");
        theme.addTag("warm");
        return theme;
    }
    
    /**
     * Creates Material theme
     */
    private Theme createMaterialTheme() {
        Theme theme = new Theme("Material", true);
        theme.setDescription("Material Design inspired theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(30, 30, 40));
        theme.setColor("foreground", new Color(255, 255, 255));
        theme.setColor("primary", new Color(98, 0, 238));
        theme.setColor("secondary", new Color(40, 40, 55));
        theme.setColor("accent", new Color(0, 200, 200));
        theme.setColor("error", new Color(255, 60, 60));
        theme.setColor("warning", new Color(255, 180, 0));
        theme.setColor("success", new Color(0, 230, 118));
        theme.setColor("info", new Color(0, 150, 255));
        theme.setColor("text", new Color(255, 255, 255));
        theme.setColor("textSecondary", new Color(180, 180, 200));
        theme.setColor("border", new Color(50, 50, 65));
        theme.setColor("hover", new Color(45, 45, 60));
        theme.setColor("selectionBackground", new Color(98, 0, 238, 50));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        
        theme.setFont("default", new Font("Roboto", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Roboto", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Roboto", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("material");
        theme.addTag("modern");
        return theme;
    }
    
    /**
     * Creates Pastel theme
     */
    private Theme createPastelTheme() {
        Theme theme = new Theme("Pastel", true);
        theme.setDescription("Soft pastel colors");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(250, 240, 245));
        theme.setColor("foreground", new Color(80, 60, 80));
        theme.setColor("primary", new Color(200, 150, 200));
        theme.setColor("secondary", new Color(240, 230, 235));
        theme.setColor("accent", new Color(180, 220, 220));
        theme.setColor("error", new Color(220, 150, 150));
        theme.setColor("warning", new Color(220, 200, 150));
        theme.setColor("success", new Color(150, 200, 170));
        theme.setColor("info", new Color(150, 180, 220));
        theme.setColor("text", new Color(80, 60, 80));
        theme.setColor("textSecondary", new Color(140, 120, 140));
        theme.setColor("border", new Color(220, 210, 220));
        theme.setColor("hover", new Color(230, 220, 230));
        theme.setColor("selectionBackground", new Color(200, 150, 200, 40));
        theme.setColor("selectionForeground", new Color(80, 60, 80));
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("pastel");
        theme.addTag("soft");
        theme.addTag("light");
        return theme;
    }
    
    /**
     * Creates Retro theme
     */
    private Theme createRetroTheme() {
        Theme theme = new Theme("Retro", true);
        theme.setDescription("Retro/vintage color scheme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(50, 40, 30));
        theme.setColor("foreground", new Color(200, 180, 150));
        theme.setColor("primary", new Color(200, 150, 80));
        theme.setColor("secondary", new Color(60, 50, 40));
        theme.setColor("accent", new Color(180, 130, 70));
        theme.setColor("error", new Color(200, 100, 80));
        theme.setColor("warning", new Color(200, 180, 80));
        theme.setColor("success", new Color(150, 180, 100));
        theme.setColor("info", new Color(130, 160, 200));
        theme.setColor("text", new Color(220, 200, 180));
        theme.setColor("textSecondary", new Color(150, 130, 110));
        theme.setColor("border", new Color(80, 70, 60));
        theme.setColor("hover", new Color(70, 60, 50));
        theme.setColor("selectionBackground", new Color(200, 150, 80, 30));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        
        theme.setFont("default", new Font("Courier New", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Courier New", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Courier New", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Courier New", Font.PLAIN, 12));
        
        theme.addTag("retro");
        theme.addTag("vintage");
        theme.addTag("warm");
        return theme;
    }
    
    /**
     * Creates Cyberpunk theme
     */
    private Theme createCyberpunkTheme() {
        Theme theme = new Theme("Cyberpunk", true);
        theme.setDescription("Cyberpunk neon theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(10, 5, 20));
        theme.setColor("foreground", new Color(0, 255, 255));
        theme.setColor("primary", new Color(255, 0, 150));
        theme.setColor("secondary", new Color(20, 10, 35));
        theme.setColor("accent", new Color(255, 200, 0));
        theme.setColor("error", new Color(255, 0, 50));
        theme.setColor("warning", new Color(255, 150, 0));
        theme.setColor("success", new Color(0, 255, 100));
        theme.setColor("info", new Color(100, 200, 255));
        theme.setColor("text", new Color(200, 200, 255));
        theme.setColor("textSecondary", new Color(150, 100, 200));
        theme.setColor("border", new Color(50, 20, 80));
        theme.setColor("hover", new Color(40, 15, 65));
        theme.setColor("selectionBackground", new Color(255, 0, 150, 30));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        
        theme.setFont("default", new Font("Consolas", Font.PLAIN, 12));
        theme.setFont("heading", new Font("Consolas", Font.BOLD, 16));
        theme.setFont("subheading", new Font("Consolas", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("cyberpunk");
        theme.addTag("neon");
        theme.addTag("vibrant");
        return theme;
    }
    
    /**
     * Creates Minimal theme
     */
    private Theme createMinimalTheme() {
        Theme theme = new Theme("Minimal", true);
        theme.setDescription("Minimalist clean theme");
        theme.setAuthor("NeilOS Team");
        theme.setVersion("1.0.0");
        
        theme.setColor("background", new Color(20, 20, 20));
        theme.setColor("foreground", new Color(200, 200, 200));
        theme.setColor("primary", new Color(150, 150, 150));
        theme.setColor("secondary", new Color(30, 30, 30));
        theme.setColor("accent", new Color(180, 180, 180));
        theme.setColor("error", new Color(180, 80, 80));
        theme.setColor("warning", new Color(180, 160, 80));
        theme.setColor("success", new Color(80, 180, 120));
        theme.setColor("info", new Color(80, 140, 180));
        theme.setColor("text", new Color(200, 200, 200));
        theme.setColor("textSecondary", new Color(130, 130, 130));
        theme.setColor("border", new Color(40, 40, 40));
        theme.setColor("hover", new Color(35, 35, 35));
        theme.setColor("selectionBackground", new Color(150, 150, 150, 30));
        theme.setColor("selectionForeground", new Color(255, 255, 255));
        
        theme.setFont("default", new Font("SansSerif", Font.PLAIN, 12));
        theme.setFont("heading", new Font("SansSerif", Font.BOLD, 16));
        theme.setFont("subheading", new Font("SansSerif", Font.BOLD, 14));
        theme.setFont("monospace", new Font("Consolas", Font.PLAIN, 12));
        
        theme.addTag("minimal");
        theme.addTag("clean");
        theme.addTag("simple");
        return theme;
    }
    
    // ============================================================
    // CUSTOM THEME MANAGEMENT
    // ============================================================
    
    /**
     * Loads custom themes from the theme directory
     */
    private void loadCustomThemes() {
        try {
            File dir = new File(themeDir);
            File[] files = dir.listFiles((d, name) -> name.endsWith(THEME_EXTENSION));
            if (files == null) return;
            
            for (File file : files) {
                try {
                    Theme theme = loadThemeFromFile(file);
                    if (theme != null && !themes.containsKey(theme.getName())) {
                        themes.put(theme.getName(), theme);
                        notifyThemeLoaded(theme);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load theme from " + file.getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom themes: " + e.getMessage());
        }
    }
    
    /**
     * Loads a theme from a file
     */
    private Theme loadThemeFromFile(File file) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        }
        
        String name = props.getProperty("theme.name");
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        Theme theme = new Theme(name);
        theme.setDescription(props.getProperty("theme.description"));
        theme.setAuthor(props.getProperty("theme.author"));
        theme.setVersion(props.getProperty("theme.version"));
        theme.setBaseTheme(props.getProperty("theme.base"));
        theme.setBuiltin(false);
        
        // Load colors
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith("color.")) {
                String colorKey = key.substring(6);
                String colorValue = entry.getValue().toString();
                try {
                    Color color = Color.decode(colorValue);
                    theme.setColor(colorKey, color);
                } catch (NumberFormatException e) {
                    // Parse hex color
                    if (colorValue.startsWith("#")) {
                        try {
                            Color color = Color.decode(colorValue);
                            theme.setColor(colorKey, color);
                        } catch (NumberFormatException ex) {
                            // Ignore
                        }
                    }
                }
            } else if (key.startsWith("font.")) {
                String fontKey = key.substring(5);
                String fontValue = entry.getValue().toString();
                String[] parts = fontValue.split(",");
                if (parts.length >= 3) {
                    try {
                        String fontName = parts[0];
                        int style = Integer.parseInt(parts[1]);
                        int size = Integer.parseInt(parts[2]);
                        theme.setFont(fontKey, new Font(fontName, style, size));
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            } else if (key.startsWith("style.")) {
                String styleKey = key.substring(6);
                theme.setStyle(styleKey, entry.getValue().toString());
            }
        }
        
        // Load tags
        String tags = props.getProperty("theme.tags");
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags.split(",")) {
                theme.addTag(tag.trim());
            }
        }
        
        // Load CSS properties
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith("css.")) {
                String cssKey = key.substring(4);
                theme.setCssProperty(cssKey, entry.getValue().toString());
            }
        }
        
        return theme;
    }
    
    /**
     * Saves a theme to a file
     */
    public void saveTheme(Theme theme) throws IOException {
        if (theme == null) return;
        
        String filename = theme.getName().toLowerCase().replace(" ", "_") + THEME_EXTENSION;
        File file = new File(themeDir, filename);
        
        Properties props = new Properties();
        props.setProperty("theme.name", theme.getName());
        if (theme.getDescription() != null) {
            props.setProperty("theme.description", theme.getDescription());
        }
        if (theme.getAuthor() != null) {
            props.setProperty("theme.author", theme.getAuthor());
        }
        if (theme.getVersion() != null) {
            props.setProperty("theme.version", theme.getVersion());
        }
        if (theme.getBaseTheme() != null) {
            props.setProperty("theme.base", theme.getBaseTheme());
        }
        
        // Save colors
        for (Map.Entry<String, Color> entry : theme.getColors().entrySet()) {
            props.setProperty("color." + entry.getKey(), colorToHex(entry.getValue()));
        }
        
        // Save fonts
        for (Map.Entry<String, Font> entry : theme.getFonts().entrySet()) {
            Font font = entry.getValue();
            props.setProperty("font." + entry.getKey(), 
                font.getName() + "," + font.getStyle() + "," + font.getSize());
        }
        
        // Save styles
        for (Map.Entry<String, String> entry : theme.getStyles().entrySet()) {
            props.setProperty("style." + entry.getKey(), entry.getValue());
        }
        
        // Save tags
        if (!theme.getTags().isEmpty()) {
            props.setProperty("theme.tags", String.join(",", theme.getTags()));
        }
        
        // Save CSS properties
        for (Map.Entry<String, String> entry : theme.getCssProperties().entrySet()) {
            props.setProperty("css." + entry.getKey(), entry.getValue());
        }
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "NeilOS Theme: " + theme.getName());
        }
        
        notifyThemeSaved(theme);
    }
    
    /**
     * Deletes a custom theme
     */
    public boolean deleteTheme(String themeName) {
        if (themeName == null || themeName.isEmpty()) {
            return false;
        }
        
        Theme theme = themes.get(themeName);
        if (theme == null || theme.isBuiltin()) {
            return false;
        }
        
        String filename = themeName.toLowerCase().replace(" ", "_") + THEME_EXTENSION;
        File file = new File(themeDir, filename);
        
        if (file.exists() && file.delete()) {
            themes.remove(themeName);
            notifyThemeDeleted(themeName);
            
            // If current theme was deleted, switch to default
            if (currentTheme != null && currentTheme.getName().equals(themeName)) {
                applyTheme(themes.get(DEFAULT_THEME));
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Creates a new custom theme based on an existing theme
     */
    public Theme createCustomTheme(String name, String baseThemeName) {
        Theme base = themes.get(baseThemeName);
        if (base == null) {
            base = currentTheme;
        }
        
        Theme theme = new Theme(name);
        theme.setDescription("Custom theme based on " + base.getName());
        theme.setAuthor(System.getProperty("user.name"));
        theme.setVersion("1.0.0");
        theme.setBaseTheme(base.getName());
        theme.setBuiltin(false);
        
        // Copy colors
        for (Map.Entry<String, Color> entry : base.getColors().entrySet()) {
            theme.setColor(entry.getKey(), entry.getValue());
        }
        
        // Copy fonts
        for (Map.Entry<String, Font> entry : base.getFonts().entrySet()) {
            theme.setFont(entry.getKey(), entry.getValue());
        }
        
        // Copy styles
        for (Map.Entry<String, String> entry : base.getStyles().entrySet()) {
            theme.setStyle(entry.getKey(), entry.getValue());
        }
        
        // Copy CSS properties
        for (Map.Entry<String, String> entry : base.getCssProperties().entrySet()) {
            theme.setCssProperty(entry.getKey(), entry.getValue());
        }
        
        theme.addTag("custom");
        
        themes.put(name, theme);
        try {
            saveTheme(theme);
        } catch (IOException e) {
            System.err.println("Failed to save custom theme: " + e.getMessage());
        }
        
        return theme;
    }
    
    // ============================================================
    // THEME APPLICATION
    // ============================================================
    
    /**
     * Applies a theme
     */
    public void applyTheme(String themeName) {
        Theme theme = themes.get(themeName);
        if (theme == null) {
            System.err.println("Theme not found: " + themeName);
            return;
        }
        
        applyTheme(theme);
    }
    
    /**
     * Applies a theme
     */
    public void applyTheme(Theme theme) {
        if (theme == null) {
            return;
        }
        
        Theme oldTheme = currentTheme;
        
        // Update current theme
        currentTheme = theme;
        lastAppliedTheme = theme.getName();
        
        // Apply to UIManager
        applyToUIManager(theme);
        
        // Apply to all windows
        applyToAllWindows(theme);
        
        // Notify listeners
        notifyThemeChanged(oldTheme, theme);
        
        // Save preference
        if (persist) {
            saveThemePreference(theme.getName());
        }
    }
    
    /**
     * Applies theme to UIManager
     */
    private void applyToUIManager(Theme theme) {
        // Apply colors
        for (Map.Entry<String, Color> entry : theme.getColors().entrySet()) {
            String key = entry.getKey();
            Color color = entry.getValue();
            
            // Map theme colors to UI defaults
            String uiKey = mapColorKeyToUIKey(key);
            if (uiKey != null) {
                UIManager.put(uiKey, color);
                UIManager.put(uiKey + "UIResource", new ColorUIResource(color));
            }
        }
        
        // Apply fonts
        for (Map.Entry<String, Font> entry : theme.getFonts().entrySet()) {
            String key = entry.getKey();
            Font font = entry.getValue();
            
            String uiKey = mapFontKeyToUIKey(key);
            if (uiKey != null) {
                UIManager.put(uiKey, font);
                UIManager.put(uiKey + "UIResource", new FontUIResource(font));
            }
        }
        
        // Apply custom properties
        for (Map.Entry<String, String> entry : theme.getStyles().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (key.equals("borderRadius")) {
                try {
                    int radius = Integer.parseInt(value);
                    UIManager.put("Button.arc", radius);
                    UIManager.put("Component.arc", radius);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Maps color key to UI key
     */
    private String mapColorKeyToUIKey(String key) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("background", "Panel.background");
        mapping.put("foreground", "Panel.foreground");
        mapping.put("primary", "Button.background");
        mapping.put("text", "Label.foreground");
        mapping.put("textSecondary", "Label.disabledForeground");
        mapping.put("selectionBackground", "List.selectionBackground");
        mapping.put("selectionForeground", "List.selectionForeground");
        mapping.put("disabledBackground", "Button.disabledBackground");
        mapping.put("disabledForeground", "Button.disabledForeground");
        mapping.put("border", "Border.color");
        mapping.put("scrollbar", "ScrollBar.background");
        mapping.put("scrollbarTrack", "ScrollBar.trackBackground");
        mapping.put("hover", "Button.hoverBackground");
        
        return mapping.get(key);
    }
    
    /**
     * Maps font key to UI key
     */
    private String mapFontKeyToUIKey(String key) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("default", "Label.font");
        mapping.put("heading", "Label.font");
        mapping.put("subheading", "Label.font");
        mapping.put("monospace", "TextArea.font");
        mapping.put("small", "Label.smallFont");
        mapping.put("large", "Label.largeFont");
        
        return mapping.get(key);
    }
    
    /**
     * Applies theme to all windows
     */
    private void applyToAllWindows(Theme theme) {
        for (Window window : Window.getWindows()) {
            applyToWindow(window, theme);
            window.repaint();
        }
        
        // Update look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore
        }
        
        // Force update of all components
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame) {
                ((JFrame) window).getContentPane().revalidate();
                ((JFrame) window).getContentPane().repaint();
            }
            window.repaint();
        }
    }
    
    /**
     * Applies theme to a single window
     */
    private void applyToWindow(Window window, Theme theme) {
        if (window == null) return;
        
        // Set background
        window.setBackground(theme.getColor("background", Color.BLACK));
        
        // Apply to all components recursively
        if (window instanceof Container) {
            applyToContainer((Container) window, theme);
        }
    }
    
    /**
     * Applies theme to a container recursively
     */
    private void applyToContainer(Container container, Theme theme) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComponent) {
                theme.applyTo((JComponent) comp);
            }
            
            if (comp instanceof Container) {
                applyToContainer((Container) comp, theme);
            }
        }
    }
    
    // ============================================================
    // PERSISTENCE
    // ============================================================
    
    /**
     * Saves theme preference
     */
    private void saveThemePreference(String themeName) {
        try {
            File prefsFile = new File(System.getProperty("user.home"), ".neilos_theme");
            try (PrintWriter writer = new PrintWriter(prefsFile)) {
                writer.println(themeName);
            }
        } catch (IOException e) {
            // Ignore
        }
    }
    
    /**
     * Loads theme preference
     */
    private void loadThemePreference() {
        try {
            File prefsFile = new File(System.getProperty("user.home"), ".neilos_theme");
            if (prefsFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(prefsFile))) {
                    String themeName = reader.readLine();
                    if (themeName != null && !themeName.isEmpty() && themes.containsKey(themeName)) {
                        applyTheme(themeName);
                    }
                }
            }
        } catch (IOException e) {
            // Ignore
        }
    }
    
    // ============================================================
    // LISTENER MANAGEMENT
    // ============================================================
    
    /**
     * Adds a theme listener
     */
    public void addListener(ThemeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a theme listener
     */
    public void removeListener(ThemeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies listeners of theme change
     */
    private void notifyThemeChanged(Theme oldTheme, Theme newTheme) {
        ThemeChangeEvent event = new ThemeChangeEvent(oldTheme, newTheme, "ThemeManager");
        for (ThemeListener listener : listeners) {
            try {
                listener.onThemeChanged(event);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of theme loaded
     */
    private void notifyThemeLoaded(Theme theme) {
        for (ThemeListener listener : listeners) {
            try {
                listener.onThemeLoaded(theme);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of theme saved
     */
    private void notifyThemeSaved(Theme theme) {
        for (ThemeListener listener : listeners) {
            try {
                listener.onThemeSaved(theme);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Notifies listeners of theme deleted
     */
    private void notifyThemeDeleted(String themeName) {
        for (ThemeListener listener : listeners) {
            try {
                listener.onThemeDeleted(themeName);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Converts a Color to hex string
     */
    private String colorToHex(Color color) {
        if (color == null) {
            return "#000000";
        }
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Gets all theme names
     */
    public List<String> getThemeNames() {
        return new ArrayList<>(themes.keySet());
    }
    
    /**
     * Gets all themes
     */
    public List<Theme> getThemes() {
        return new ArrayList<>(themes.values());
    }
    
    /**
     * Gets a theme by name
     */
    public Theme getTheme(String name) {
        return themes.get(name);
    }
    
    /**
     * Gets the current theme
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Gets the current theme name
     */
    public String getCurrentThemeName() {
        return currentTheme != null ? currentTheme.getName() : null;
    }
    
    /**
     * Checks if a theme exists
     */
    public boolean themeExists(String name) {
        return themes.containsKey(name);
    }
    
    /**
     * Gets all built-in theme names
     */
    public List<String> getBuiltinThemeNames() {
        List<String> names = new ArrayList<>();
        for (Theme theme : themes.values()) {
            if (theme.isBuiltin()) {
                names.add(theme.getName());
            }
        }
        return names;
    }
    
    /**
     * Gets all custom theme names
     */
    public List<String> getCustomThemeNames() {
        List<String> names = new ArrayList<>();
        for (Theme theme : themes.values()) {
            if (!theme.isBuiltin()) {
                names.add(theme.getName());
            }
        }
        return names;
    }
    
    /**
     * Gets themes by tag
     */
    public List<Theme> getThemesByTag(String tag) {
        List<Theme> result = new ArrayList<>();
        for (Theme theme : themes.values()) {
            if (theme.getTags().contains(tag)) {
                result.add(theme);
            }
        }
        return result;
    }
    
    /**
     * Gets all available tags
     */
    public Set<String> getAllTags() {
        Set<String> tags = new HashSet<>();
        for (Theme theme : themes.values()) {
            tags.addAll(theme.getTags());
        }
        return tags;
    }
    
    // ============================================================
    // COMPONENT OVERRIDES
    // ============================================================
    
    /**
     * Sets a component override
     */
    public void setComponentOverride(String componentId, String property, Object value) {
        String key = componentId + "." + property;
        componentOverrides.put(key, value);
    }
    
    /**
     * Gets a component override
     */
    public Object getComponentOverride(String componentId, String property) {
        String key = componentId + "." + property;
        return componentOverrides.get(key);
    }
    
    /**
     * Clears a component override
     */
    public void clearComponentOverride(String componentId, String property) {
        String key = componentId + "." + property;
        componentOverrides.remove(key);
    }
    
    /**
     * Clears all component overrides
     */
    public void clearAllOverrides() {
        componentOverrides.clear();
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of ThemeManager
     */
    public static void main(String[] args) {
        System.out.println("🎨 ThemeManager Demo");
        System.out.println("═".repeat(60));
        
        ThemeManager tm = new ThemeManager();
        
        // List all themes
        System.out.println("\n📋 Available Themes:");
        for (String name : tm.getThemeNames()) {
            Theme theme = tm.getTheme(name);
            System.out.println("  " + name + (theme.isBuiltin() ? " (builtin)" : " (custom)") +
                (theme.equals(tm.getCurrentTheme()) ? " ✅ CURRENT" : ""));
        }
        
        // Show current theme details
        System.out.println("\n📊 Current Theme Details:");
        System.out.println(tm.getCurrentTheme().toDetailedString());
        
        // Switch themes
        System.out.println("\n🔄 Switching Themes:");
        String[] themes = {"Neon", "Ocean", "Matrix"};
        for (String name : themes) {
            if (tm.themeExists(name)) {
                System.out.println("  Applying: " + name);
                tm.applyTheme(name);
                System.out.println("  ✅ Applied " + name);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        // Create custom theme
        System.out.println("\n🔧 Creating Custom Theme:");
        Theme custom = tm.createCustomTheme("MyCustomTheme", "Dark");
        custom.setDescription("My awesome custom theme");
        custom.setColor("primary", Color.MAGENTA);
        custom.setColor("accent", Color.CYAN);
        custom.setFont("heading", new Font("Arial", Font.BOLD, 18));
        custom.addTag("custom");
        custom.addTag("my-theme");
        
        try {
            tm.saveTheme(custom);
            System.out.println("  ✅ Custom theme saved: " + custom.getName());
        } catch (IOException e) {
            System.err.println("  ❌ Failed to save custom theme: " + e.getMessage());
        }
        
        // Custom theme details
        System.out.println("\n📊 Custom Theme Details:");
        System.out.println(custom.toDetailedString());
        
        // Apply custom theme
        tm.applyTheme("MyCustomTheme");
        System.out.println("\n  ✅ Applied custom theme");
        
        // Get themes by tag
        System.out.println("\n🏷️ Themes with tag 'dark':");
        for (Theme theme : tm.getThemesByTag("dark")) {
            System.out.println("  " + theme.getName());
        }
        
        // All tags
        System.out.println("\n🏷️ All Tags:");
        for (String tag : tm.getAllTags()) {
            System.out.println("  " + tag);
        }
        
        // Get built-in themes
        System.out.println("\n📦 Built-in Themes:");
        for (String name : tm.getBuiltinThemeNames()) {
            System.out.println("  " + name);
        }
        
        // Get custom themes
        System.out.println("\n🔧 Custom Themes:");
        for (String name : tm.getCustomThemeNames()) {
            System.out.println("  " + name);
        }
        
        // Delete custom theme
        System.out.println("\n🗑️ Deleting Custom Theme:");
        if (tm.deleteTheme("MyCustomTheme")) {
            System.out.println("  ✅ Deleted MyCustomTheme");
        } else {
            System.out.println("  ❌ Failed to delete MyCustomTheme");
        }
        
        System.out.println("\n✅ Demo completed!");
    }
}